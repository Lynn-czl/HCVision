
package vision;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * JDialogPlayBack ������ ��Զ���ļ��طŲ���
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
class JDialogPlayBack extends javax.swing.JDialog {

	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private static PlayCtrl playControl = PlayCtrl.INSTANCE;

	private NativeLongByReference lport = new NativeLongByReference();

	private NativeLong m_lPlayBackHandle;// �طž��
	private NativeLong m_lDownloadHandle;// ���ؾ��
	private NativeLong m_lUserID;// �û�ID
	private Timer Downloadtimer;// �����ö�ʱ��
	private Timer Playbacktimer;// �ط��ö�ʱ��

	private IntByReference m_nFileTime;// �ļ���ʱ��
	private IntByReference m_nTotalFrames;// �ļ���֡��

	private int m_nTotalSecond;// ������
	private int m_nTotalMinute;// �ܷ�����
	private int m_nTotalHour;// ��Сʱ

	private boolean m_bGetMaxTime;// �Ƿ��ѻ���ܲ���ʱ��,�ڼ�ʱ���캯����,ֻ��Ҫ����һ��
	private boolean m_bSaveFile;// �Ƿ��ڱ����ļ�

	private File video = null;

	/**
	 * ����: JDialogPlayBack ��������: ���캯�� Creates new form JDialogPlayBack
	 * 
	 * @param parent
	 * @param modal
	 * @param lUserID
	 */
	public JDialogPlayBack(java.awt.Frame parent, boolean modal, NativeLong lUserID) {
		super(parent, modal);
		initComponents();
		initialDialog();
		m_lUserID = lUserID;
		m_lPlayBackHandle = new NativeLong(-1);
		m_lDownloadHandle = new NativeLong(-1);
		m_nFileTime = new IntByReference(0);
		m_nTotalFrames = new IntByReference(0);
	}

	public void search() {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			video = jfc.getSelectedFile();
		} else
			System.out.println("No file is selected!");
	}

	/**
	 * ����: "����" ��ť������Ӧ���� ��������: ����������Ϣ����¼���ļ�
	 * 
	 * @param evt
	 */
	private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {
		// ���������ļ�
		((DefaultTableModel) jTableFile.getModel()).getDataVector().removeAllElements();// ����ǰ������б�
		// �Ѹı���ʾ���б�ؼ�
		((DefaultTableModel) jTableFile.getModel()).fireTableStructureChanged();

		HCNetSDK.NET_DVR_FILECOND m_strFilecond = new HCNetSDK.NET_DVR_FILECOND();
		m_strFilecond.struStartTime = new HCNetSDK.NET_DVR_TIME();
		m_strFilecond.struStopTime = new HCNetSDK.NET_DVR_TIME();
		m_strFilecond.struStartTime.dwYear = Integer.parseInt(jTextFieldsYear.getText());// ��ʼʱ��
		m_strFilecond.struStartTime.dwMonth = Integer.parseInt(jTextFieldsMonth.getText());
		m_strFilecond.struStartTime.dwDay = Integer.parseInt(jTextFieldsDay.getText());
		m_strFilecond.struStartTime.dwHour = Integer.parseInt(jTextFieldsHour.getText());
		m_strFilecond.struStartTime.dwMinute = Integer.parseInt(jTextFieldsMinute.getText());
		m_strFilecond.struStartTime.dwSecond = Integer.parseInt(jTextFieldsSecond.getText());
		m_strFilecond.struStopTime.dwYear = Integer.parseInt(jTextFieldeYear.getText());// ����ʱ��
		m_strFilecond.struStopTime.dwMonth = Integer.parseInt(jTextFieldeMonth.getText());
		m_strFilecond.struStopTime.dwDay = Integer.parseInt(jTextFieldeDay.getText());
		m_strFilecond.struStopTime.dwHour = Integer.parseInt(jTextFieldeHour.getText());
		m_strFilecond.struStopTime.dwMinute = Integer.parseInt(jTextFieldeMinute.getText());
		m_strFilecond.struStopTime.dwSecond = Integer.parseInt(jTextFieldeSecond.getText());
		m_strFilecond.lChannel = new NativeLong(Integer.parseInt(jTextFieldChannelNumber.getText()));// ͨ����
		m_strFilecond.dwFileType = jComboBoxFlieType.getSelectedIndex();// �ļ�����
		m_strFilecond.dwIsLocked = 0xff;
		m_strFilecond.dwUseCardNo = jRadioButtonByCardNumber.isSelected() ? 1 : 0; // �Ƿ�ʹ�ÿ���
		if (m_strFilecond.dwUseCardNo == 1) {
			m_strFilecond.sCardNumber = jTextFieldCardNumber.getText().getBytes();// ����
			System.out.printf("����%s", m_strFilecond.sCardNumber);
		}

		NativeLong lFindFile = hCNetSDK.NET_DVR_FindFile_V30(m_lUserID, m_strFilecond);
		HCNetSDK.NET_DVR_FINDDATA_V30 strFile = new HCNetSDK.NET_DVR_FINDDATA_V30();
		long findFile = lFindFile.longValue();
		if (findFile > -1) {
			System.out.println("file" + findFile);
		}
		NativeLong lnext;
		strFile = new HCNetSDK.NET_DVR_FINDDATA_V30();

		while (true) {
			lnext = hCNetSDK.NET_DVR_FindNextFile_V30(lFindFile, strFile);
			if (lnext.longValue() == HCNetSDK.NET_DVR_FILE_SUCCESS) {
				// �����ɹ�
				DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFile.getModel());// ��ȡ���ģ��
				Vector<String> newRow = new Vector<String>();

				// ����ļ�����Ϣ
				String[] s = new String[2];
				s = new String(strFile.sFileName).split("\0", 2);
				newRow.add(new String(s[0]));

				int iTemp;
				String MyString;
				if (strFile.dwFileSize < 1024 * 1024) {
					iTemp = (strFile.dwFileSize) / (1024);
					MyString = iTemp + "K";
				} else {
					iTemp = (strFile.dwFileSize) / (1024 * 1024);
					MyString = iTemp + "M   ";
					iTemp = ((strFile.dwFileSize) % (1024 * 1024)) / (1204);
					MyString = MyString + iTemp + "K";
				}
				newRow.add(MyString); // ����ļ���С��Ϣ
				newRow.add(strFile.struStartTime.toStringTime());// ��ӿ�ʼʱ����Ϣ
				newRow.add(strFile.struStopTime.toStringTime()); // ��ӽ���ʱ����Ϣ

				FileTableModel.getDataVector().add(newRow);
				((DefaultTableModel) jTableFile.getModel()).fireTableStructureChanged();
			} else {
				if (lnext.longValue() == HCNetSDK.NET_DVR_ISFINDING) {// ������
					System.out.println("������");
					continue;
				} else {
					if (lnext.longValue() == HCNetSDK.NET_DVR_FILE_NOFIND) {
						JOptionPane.showMessageDialog(this, "û���ѵ��ļ�");
						return;
					} else {
						System.out.println("�����ļ�����");
						boolean flag = hCNetSDK.NET_DVR_FindClose_V30(lFindFile);
						if (flag == false) {
							System.out.println("��������ʧ��");
						}
						return;
					}
				}
			}
		}
	}

	/**
	 * ����: "Pause" ��ť������Ӧ���� ��������: ��ͣ���� ����: "�˳�" ��ť������Ӧ���� ��������: ���ٶԻ���
	 */
	private void jToggleButtonExitActionPerformed(java.awt.event.ActionEvent evt) {
		StopPlay();
		dispose();
	}

	/**
	 * ����: "����" ��ť������Ӧ���� ��������: ����ѡ���ļ�
	 * 
	 * @param evt
	 */
	private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {
		// �����������,��ʼ����
		if (m_lDownloadHandle.intValue() == -1) {
			// δѡ���ļ�ʱ��ʾѡ��Ҫ���ص��ļ�
			if (jTableFile.getSelectedRow() == -1) {
				JOptionPane.showMessageDialog(this, "��ѡ��Ҫ���ص��ļ�");
				return;
			}
			// ��ȡ�ļ���
			DefaultTableModel FileTableModel = ((DefaultTableModel) jTableFile.getModel());
			String sFileName = FileTableModel.getValueAt(jTableFile.getSelectedRow(), 0).toString();
			// ���ҽ��ļ�����Ϊ���������
			m_lDownloadHandle = hCNetSDK.NET_DVR_GetFileByName(m_lUserID, sFileName, sFileName);
			if (m_lDownloadHandle.intValue() >= 0) {
				hCNetSDK.NET_DVR_PlayBackControl(m_lDownloadHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
				jButtonDownload.setText("ֹͣ����");
				jProgressBar.setValue(0);
				jProgressBar.setVisible(true);

				// ��ʼ��ʱ��
				Downloadtimer = new Timer();// �½���ʱ��
				Downloadtimer.schedule(new DownloadTask(), 0, 1000);// 0���ʼ��Ӧ����
			} else {
				JOptionPane.showMessageDialog(this, "�����ļ�ʧ��");
				return;
			}
		}
		// ���������,ֹͣ����
		else {
			hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
			m_lDownloadHandle.setValue(-1);
			jButtonDownload.setText("����");
			jProgressBar.setValue(0);
			jProgressBar.setVisible(false);
			Downloadtimer.cancel();
		}

	}

	/**
	 * ����: "����" ��ť������Ӧ���� ��������: �������ڻطŵ�����
	 * 
	 * @param evt
	 */
	private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {
		if (m_lPlayBackHandle.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "���Ȳ����ļ�");
			return;
		}
		JFileChooser myJFileChooser = new JFileChooser();
		myJFileChooser.showSaveDialog(this);
		String sFileName = myJFileChooser.getSelectedFile() + ".mp4";

		if (hCNetSDK.NET_DVR_PlayBackSaveData(m_lPlayBackHandle, sFileName)) {
			m_bSaveFile = true;
		} else {
			JOptionPane.showMessageDialog(this, "�����ļ�ʧ��");
		}
	}

	/**
	 * ����: "ֹͣ" ��ť������Ӧ���� ��������: ֹͣ����
	 * 
	 * @param evt
	 */
	private void jButtonStopSaveActionPerformed(java.awt.event.ActionEvent evt) {
		if (m_bSaveFile) {
			hCNetSDK.NET_DVR_StopPlayBackSave(m_lPlayBackHandle);
			m_bSaveFile = false;
			JOptionPane.showMessageDialog(this, "ֹͣ����ɹ�");
		}
	}

	/**
	 * ����: "������" ���Releaseʱ�䰴ť��Ӧ���� ��������: �������õĽ��Ȳ����ļ�
	 * 
	 * @param evt
	 */
	private void jSliderPlaybackMouseReleased(java.awt.event.MouseEvent evt) {
		int iPos = jSliderPlayback.getValue();
		if (m_lPlayBackHandle.intValue() >= 0) {
			if ((iPos >= 0) && (iPos <= 100)) {
				if (iPos == 100) {
					StopPlay();
					iPos = 99;
				} else {
					if (hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle, HCNetSDK.NET_DVR_PLAYSETPOS, iPos, null)) {
						System.out.println("���ò��Ž��ȳɹ�");
					} else {
						System.out.println("���ò��Ž���ʧ��");
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	private void jButtonPlayActionPerformed(java.awt.event.ActionEvent evt) {
		if (video == null) {
			JOptionPane.showMessageDialog(this, "��ѡ��Ҫ���ŵ��ļ�");
			return;
		}

		// ����Ѿ��ڻط�
		if (lport.getValue().longValue() != -1) {
			hCNetSDK.NET_DVR_StopPlayBack(m_lPlayBackHandle);
			lport.setValue(new NativeLong(-1));
		}

		// ��ȡ���ھ��
		HWND hwnd = new HWND();
		hwnd.setPointer(Native.getComponentPointer(panelPlayBack));// ��ȡ���ڵ�ָ��

		// ���ýӿڿ�ʼ�ط�
		if (playControl.PlayM4_GetPort(lport)) {
			System.out.println(playControl.PlayM4_OpenFile(lport.getValue(), video.getAbsolutePath()));
			System.out.println(playControl.PlayM4_Play(lport.getValue(), hwnd));
		} else {
			System.out.println("��ò��ź�ʧ��");
		}

		jTextFieldFileName.setText(video.getName());
		jTextFieldTotalBytes.setText(String.valueOf(video.getTotalSpace()));
		// ��ʼ��ʱ��
		Playbacktimer = new Timer();// �½���ʱ��
		Playbacktimer.schedule(new PlaybackTask(), 0, 1000);// 0���ʼ��Ӧ����
	}

	/**
	 * ����: "Stop" ��ť������Ӧ���� ��������: ֹͣ����
	 * 
	 * @param evt
	 */
	private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {
		StopPlay();
	}

	/**
	 * ����: "Normal" ��ť������Ӧ���� ��������: �ָ���������
	 */
	private void jButtonNormalActionPerformed(java.awt.event.ActionEvent evt) {
		if (lport.getValue().longValue() == -1) {
			return;
		}
		if (0 == playControl.PlayM4_Pause(lport.getValue(), 0)) {
			JOptionPane.showMessageDialog(this, "�ָ�ʧ��");
		}
	}

	/**
	 * ����: "Pause" ��ť������Ӧ���� ��������: ��ͣ����
	 * 
	 * @param evt
	 */
	private void jButtonPauseActionPerformed(java.awt.event.ActionEvent evt) {
		if (lport.getValue().longValue() == -1) {
			return;
		}
		if (0 == playControl.PlayM4_Pause(lport.getValue(), 1)) {
			JOptionPane.showMessageDialog(this, "��ͣʧ��");
		}
	}

	/**
	 * ����: "Slow" ��ť������Ӧ���� ��������: ����
	 * 
	 * @param evt
	 */
	private void jButtonSlowActionPerformed(java.awt.event.ActionEvent evt) {
		playControl.PlayM4_Slow(lport.getValue());
		// hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle,
		// hCNetSDK.NET_DVR_PLAYSLOW, 0, null);
	}

	/**
	 * ����: "Fast" ��ť������Ӧ���� ��������: ���
	 * 
	 * @param evt
	 */
	private void jButtonFastActionPerformed(java.awt.event.ActionEvent evt) {
		playControl.PlayM4_Fast(lport.getValue());
		// hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle,
		// hCNetSDK.NET_DVR_PLAYFAST, 0, null);
	}

	/**
	 * ����: "Capture" ��ť������Ӧ���� ��������: ��ͼ
	 * 
	 * @param evt
	 */
	private void jButtonCaptureActionPerformed(java.awt.event.ActionEvent evt) {
		if (m_lPlayBackHandle.intValue() == -1) {
			return;
		}
		String sPicName = "C:/Picture/" + Integer.parseInt(jTextFieldChannelNumber.getText())
				+ System.currentTimeMillis() + ".bmp";
		if (hCNetSDK.NET_DVR_PlayBackCaptureFile(m_lPlayBackHandle, sPicName)) {
			System.out.println("ץͼ:" + sPicName);
			return;
		} else {
			JOptionPane.showMessageDialog(this, "ץͼʧ��");
		}
	}

	/**
	 * ����: initialTableModel ��������: ��ʼ���ļ��б�
	 * 
	 * @return
	 */
	private DefaultTableModel initialTableModel() {
		String tabeTile[];
		tabeTile = new String[] { "�ļ�����", "��С", "��ʼʱ��", "����ʱ��" };
		DefaultTableModel fileTableModel = new DefaultTableModel(tabeTile, 10);
		return fileTableModel;
	}

	/**
	 * ����: initialDialog ��������: ��ʼ������ʱ����Ϣ
	 */
	private void initialDialog() {
		Date today = new Date();// ����ʱ��,��������ʱ������ʱ��(�����0:0:0-23:59:59)
		Calendar now = Calendar.getInstance();// �������� //�õ���ǰ����
		now.setTime(today); // ����ʱ��

		// ��ʼʱ��
		jTextFieldsYear.setText(now.get(Calendar.YEAR) + "");
		jTextFieldsMonth.setText((now.get(Calendar.MONTH) + 1) + "");
		jTextFieldsDay.setText(1 + "");
		jTextFieldsHour.setText("0");
		jTextFieldsMinute.setText("0");
		jTextFieldsSecond.setText("0");

		// ����ʱ��
		jTextFieldeYear.setText(now.get(Calendar.YEAR) + "");
		jTextFieldeMonth.setText((now.get(Calendar.MONTH) + 1) + "");
		jTextFieldeDay.setText(now.get(Calendar.DATE) + "");
		jTextFieldeHour.setText("23");
		jTextFieldeMinute.setText("59");
		jTextFieldeSecond.setText("59");

		jProgressBar.setVisible(false);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				StopPlay();
			}
		});
	}

	/**
	 * DownloadTask ������: ���ض�ʱ����Ӧ����
	 * 
	 * @author WSL
	 *
	 */
	class DownloadTask extends java.util.TimerTask {// ��ʱ������
		@Override
		public void run() {
			int iPos = hCNetSDK.NET_DVR_GetDownloadPos(m_lDownloadHandle);
			System.out.println(iPos);
			if (iPos < 0) {// failed
				hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
				jProgressBar.setVisible(false);
				jButtonDownload.setText("����");
				m_lDownloadHandle.setValue(-1);
				JOptionPane.showMessageDialog(null, "��ȡ���ؽ���ʧ��");
				Downloadtimer.cancel();
			}
			if (iPos == 100) {// end download
				hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
				jProgressBar.setVisible(false);
				jButtonDownload.setText("����");
				m_lDownloadHandle.setValue(-1);
				JOptionPane.showMessageDialog(null, "�������");
				Downloadtimer.cancel();
			}
			if (iPos > 100) {// download exception for network problems or DVR
				hCNetSDK.NET_DVR_StopGetFile(m_lDownloadHandle);
				jProgressBar.setVisible(false);
				jButtonDownload.setText("����");
				m_lDownloadHandle.setValue(-1);
				JOptionPane.showMessageDialog(null, "��������ԭ���DVRæ,�����쳣��ֹ");
				Downloadtimer.cancel();
			} else {
				jProgressBar.setValue(iPos);
			}
		}
	}

	/**
	 * PlaybackTask ������: �طŶ�ʱ����Ӧ����
	 * 
	 * @author WSL
	 *
	 */
	class PlaybackTask extends java.util.TimerTask {// ��ʱ������
		@Override
		public void run() {
			IntByReference nCurrentTime = new IntByReference(0);
			IntByReference nCurrentFrame = new IntByReference(0);
			IntByReference nPos = new IntByReference(0);
			int nHour, nMinute, nSecond;
			if (!m_bGetMaxTime) {
				hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle, HCNetSDK.NET_DVR_GETTOTALTIME, 0, m_nFileTime);
				if (m_nFileTime.getValue() == 0) {
					return;
				}
				if (hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle, HCNetSDK.NET_DVR_GETTOTALFRAMES, 0,
						m_nTotalFrames)) {
					if (m_nTotalFrames.getValue() == 0) {
						return;
					}
				} else {
					System.out.println("��ȡ��֡��ʧ��");
				}
				m_nTotalHour = m_nFileTime.getValue() / 3600;
				m_nTotalMinute = (m_nFileTime.getValue() % 3600) / 60;
				m_nTotalSecond = m_nFileTime.getValue() % 60;
				m_bGetMaxTime = true;
			}

			hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle, HCNetSDK.NET_DVR_PLAYGETTIME, 0, nCurrentTime);
			if (nCurrentTime.getValue() >= m_nFileTime.getValue()) {
				nCurrentTime.setValue(m_nFileTime.getValue());
			}
			nHour = (nCurrentTime.getValue() / 3600) % 24;
			nMinute = (nCurrentTime.getValue() % 3600) / 60;
			nSecond = nCurrentTime.getValue() % 60;
			hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle, HCNetSDK.NET_DVR_PLAYGETFRAME, 0, nCurrentFrame);
			if (nCurrentFrame.getValue() > m_nTotalFrames.getValue()) {
				nCurrentFrame.setValue(m_nTotalFrames.getValue());
			}

			String sPlayTime;// ����ʱ��
			sPlayTime = String.format("%02d:%02d:%02d/%02d:%02d:%02d ", nHour, nMinute, nSecond, m_nTotalHour,
					m_nTotalMinute, m_nTotalSecond);
			jTextFieldPlayTime.setText(sPlayTime);

			hCNetSDK.NET_DVR_PlayBackControl(m_lPlayBackHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 0, nPos);
			if (nPos.getValue() > 100) {// 200 indicates network exception
				StopPlay();
				JOptionPane.showMessageDialog(JDialogPlayBack.this, "��������ԭ���DVRæ,�ط��쳣��ֹ!");
			} else {
				jSliderPlayback.setValue(nPos.getValue());
				if (nPos.getValue() == 100) {
					StopPlay();
				}
			}
		}
	}

	/**
	 * StopPlay ��������: ֹͣ����
	 */
	private void StopPlay() {
		if (lport.getValue().longValue() >= 0) {
			if (!playControl.PlayM4_Stop(lport.getValue())) {
				System.out.println("NET_DVR_StopPlayBack failed");
				return;
			} else {
				panelPlayBack.repaint();
				lport.setValue(new NativeLong(-1));
				jSliderPlayback.setValue(-1);
				Playbacktimer.cancel();
				jTextFieldFileName.setText("");
				jTextFieldTotalBytes.setText("");
				jTextFieldPlayTime.setText("");
				jSliderPlayback.setValue(0);
			}
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void initComponents() {
		buttonGroup1 = new javax.swing.ButtonGroup();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTableFile = new javax.swing.JTable();
		jPanel1 = new javax.swing.JPanel();
		jLabelType = new javax.swing.JLabel();
		jLabelFileType = new javax.swing.JLabel();
		jComboBoxRecodType = new javax.swing.JComboBox();
		jComboBoxFlieType = new javax.swing.JComboBox();
		jRadioButtonByCardNumber = new javax.swing.JRadioButton();
		jTextFieldCardNumber = new javax.swing.JTextField();
		jLabel14 = new javax.swing.JLabel();
		jTextFieldChannelNumber = new javax.swing.JTextField();
		jButtonSearch = new javax.swing.JButton();
		jPanelTime = new javax.swing.JPanel();
		jLabelStartTime = new javax.swing.JLabel();
		jLabelEndTime = new javax.swing.JLabel();
		jTextFieldsYear = new javax.swing.JTextField();
		jTextFieldsMonth = new javax.swing.JTextField();
		jTextFieldsDay = new javax.swing.JTextField();
		jTextFieldsHour = new javax.swing.JTextField();
		jTextFieldsMinute = new javax.swing.JTextField();
		jTextFieldsSecond = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jTextFieldeMonth = new javax.swing.JTextField();
		jTextFieldeDay = new javax.swing.JTextField();
		jTextFieldeHour = new javax.swing.JTextField();
		jTextFieldeMinute = new javax.swing.JTextField();
		jTextFieldeSecond = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		jTextFieldeYear = new javax.swing.JTextField();
		jLabel13 = new javax.swing.JLabel();
		jLabel20 = new javax.swing.JLabel();
		jPanelPlayControl = new javax.swing.JPanel();
		panelPlayBack = new java.awt.Panel();
		jSliderPlayback = new javax.swing.JSlider();
		jToolBar2 = new javax.swing.JToolBar();
		jButtonPlay = new javax.swing.JButton();
		jButtonStop = new javax.swing.JButton();
		jLabel17 = new javax.swing.JLabel();
		jButtonNormal = new javax.swing.JButton();
		jButtonPause = new javax.swing.JButton();
		jLabel18 = new javax.swing.JLabel();
		jButtonSlow = new javax.swing.JButton();
		jButtonFast = new javax.swing.JButton();
		jLabel19 = new javax.swing.JLabel();
		jButtonCapture = new javax.swing.JButton();
		jToggleButtonExit = new javax.swing.JToggleButton();
		jButtonDownload = new javax.swing.JButton();
		jProgressBar = new javax.swing.JProgressBar();
		jPanelPlayInfo = new javax.swing.JPanel();
		jTextFieldPlayTime = new javax.swing.JTextField();
		jButtonSave = new javax.swing.JButton();
		jButtonStopSave = new javax.swing.JButton();
		jLabel15 = new javax.swing.JLabel();
		jTextFieldFileName = new javax.swing.JTextField();
		jLabel16 = new javax.swing.JLabel();
		jTextFieldTotalBytes = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("���ļ��ط�");
		getContentPane().setLayout(null);

		jTableFile.setModel(this.initialTableModel());
		jScrollPane1.setViewportView(jTableFile);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(10, 390, 530, 180);

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("������Ϣ"));
		jPanel1.setLayout(null);

		jLabelType.setText("¼������");
		jPanel1.add(jLabelType);
		jLabelType.setBounds(10, 30, 60, 15);

		jLabelFileType.setText("�ļ�����");
		jPanel1.add(jLabelFileType);
		jLabelFileType.setBounds(10, 60, 60, 15);

		jComboBoxRecodType.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "ȫ��", "��ʱ¼��", "�ƶ����", "��������", "����|����", "����&����", "�����", "�ֶ�¼��" }));
		jPanel1.add(jComboBoxRecodType);
		jComboBoxRecodType.setBounds(90, 30, 110, 21);

		jComboBoxFlieType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ȫ��", "����", "����" }));
		jPanel1.add(jComboBoxFlieType);
		jComboBoxFlieType.setBounds(90, 60, 110, 21);
		jPanel1.add(jRadioButtonByCardNumber);
		jRadioButtonByCardNumber.setBounds(90, 120, 70, 21);

		jTextFieldCardNumber.setText("0");
		jPanel1.add(jTextFieldCardNumber);
		jTextFieldCardNumber.setBounds(90, 150, 190, 21);

		jLabel14.setText("ͨ����");
		jPanel1.add(jLabel14);
		jLabel14.setBounds(10, 90, 36, 15);

		jTextFieldChannelNumber.setText("1");
		jPanel1.add(jTextFieldChannelNumber);
		jTextFieldChannelNumber.setBounds(90, 90, 110, 21);

		jButtonSearch.setText("����");
		jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// jButtonSearchActionPerformed(evt);
				search();
			}
		});
		jPanel1.add(jButtonSearch);
		jButtonSearch.setBounds(10, 340, 60, 23);

		jPanelTime.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
		jPanelTime.setLayout(null);

		jLabelStartTime.setText("��ʼʱ��");
		jPanelTime.add(jLabelStartTime);
		jLabelStartTime.setBounds(10, 10, 50, 50);

		jLabelEndTime.setText("����ʱ��");
		jPanelTime.add(jLabelEndTime);
		jLabelEndTime.setBounds(10, 70, 60, 50);
		jPanelTime.add(jTextFieldsYear);
		jTextFieldsYear.setBounds(80, 10, 30, 21);
		jPanelTime.add(jTextFieldsMonth);
		jTextFieldsMonth.setBounds(150, 10, 30, 21);
		jPanelTime.add(jTextFieldsDay);
		jTextFieldsDay.setBounds(210, 10, 30, 21);
		jPanelTime.add(jTextFieldsHour);
		jTextFieldsHour.setBounds(80, 40, 30, 21);
		jPanelTime.add(jTextFieldsMinute);
		jTextFieldsMinute.setBounds(150, 40, 30, 21);
		jPanelTime.add(jTextFieldsSecond);
		jTextFieldsSecond.setBounds(210, 40, 30, 21);

		jLabel1.setText("��");
		jPanelTime.add(jLabel1);
		jLabel1.setBounds(120, 10, 20, 15);

		jLabel2.setText("��");
		jPanelTime.add(jLabel2);
		jLabel2.setBounds(190, 10, 20, 15);

		jLabel3.setText("��");
		jPanelTime.add(jLabel3);
		jLabel3.setBounds(250, 10, 30, 15);

		jLabel4.setText("ʱ");
		jPanelTime.add(jLabel4);
		jLabel4.setBounds(120, 40, 20, 15);

		jLabel5.setText("��");
		jPanelTime.add(jLabel5);
		jLabel5.setBounds(190, 40, 20, 15);

		jLabel6.setText("��");
		jPanelTime.add(jLabel6);
		jLabel6.setBounds(250, 40, 30, 15);
		jPanelTime.add(jTextFieldeMonth);
		jTextFieldeMonth.setBounds(150, 70, 30, 21);
		jPanelTime.add(jTextFieldeDay);
		jTextFieldeDay.setBounds(210, 70, 30, 21);
		jPanelTime.add(jTextFieldeHour);
		jTextFieldeHour.setBounds(80, 100, 30, 21);
		jPanelTime.add(jTextFieldeMinute);
		jTextFieldeMinute.setBounds(150, 100, 30, 21);
		jPanelTime.add(jTextFieldeSecond);
		jTextFieldeSecond.setBounds(210, 100, 30, 21);

		jLabel7.setText("��");
		jPanelTime.add(jLabel7);
		jLabel7.setBounds(120, 70, 20, 15);

		jLabel8.setText("��");
		jPanelTime.add(jLabel8);
		jLabel8.setBounds(190, 70, 20, 15);

		jLabel9.setText("��");
		jPanelTime.add(jLabel9);
		jLabel9.setBounds(250, 70, 30, 15);

		jLabel10.setText("ʱ");
		jPanelTime.add(jLabel10);
		jLabel10.setBounds(120, 100, 20, 15);

		jLabel11.setText("��");
		jPanelTime.add(jLabel11);
		jLabel11.setBounds(190, 100, 20, 15);

		jLabel12.setText("��");
		jPanelTime.add(jLabel12);
		jLabel12.setBounds(250, 100, 30, 15);
		jPanelTime.add(jTextFieldeYear);
		jTextFieldeYear.setBounds(80, 70, 30, 21);

		jPanel1.add(jPanelTime);
		jPanelTime.setBounds(10, 190, 270, 130);

		jLabel13.setText("����");
		jPanel1.add(jLabel13);
		jLabel13.setBounds(10, 150, 40, 15);

		jLabel20.setText("�Ƿ񰴿���");
		jPanel1.add(jLabel20);
		jLabel20.setBounds(10, 120, 80, 15);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(10, 10, 290, 380);

		jPanelPlayControl.setBorder(javax.swing.BorderFactory.createTitledBorder("�طſ���"));
		jPanelPlayControl.setLayout(null);

		panelPlayBack.setBackground(new java.awt.Color(240, 255, 255));
		panelPlayBack.setForeground(new java.awt.Color(153, 255, 255));
		jPanelPlayControl.add(panelPlayBack);
		panelPlayBack.setBounds(10, 20, 390, 300);

		jSliderPlayback.setValue(0);
		jSliderPlayback.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jSliderPlaybackMouseReleased(evt);
			}
		});
		jPanelPlayControl.add(jSliderPlayback);
		jSliderPlayback.setBounds(0, 330, 400, 20);

		jToolBar2.setRollover(true);

		jButtonPlay.setBackground(new java.awt.Color(204, 204, 255));
		jButtonPlay.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonPlay.setForeground(new java.awt.Color(51, 51, 255));
		jButtonPlay.setText("Play");
		jButtonPlay.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jButtonPlay.setFocusable(false);
		jButtonPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonPlayActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonPlay);

		jButtonStop.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonStop.setForeground(new java.awt.Color(51, 51, 255));
		jButtonStop.setText("Stop");
		jButtonStop.setFocusable(false);
		jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonStop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonStopActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonStop);

		jLabel17.setText("      ");
		jToolBar2.add(jLabel17);

		jButtonNormal.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonNormal.setForeground(new java.awt.Color(51, 51, 255));
		jButtonNormal.setText("Normal");
		jButtonNormal.setFocusable(false);
		jButtonNormal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonNormal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonNormal.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonNormalActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonNormal);

		jButtonPause.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonPause.setForeground(new java.awt.Color(51, 51, 255));
		jButtonPause.setText("Pause");
		jButtonPause.setFocusable(false);
		jButtonPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonPause.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonPauseActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonPause);

		jLabel18.setText("      ");
		jToolBar2.add(jLabel18);

		jButtonSlow.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonSlow.setForeground(new java.awt.Color(51, 51, 255));
		jButtonSlow.setText("Slow");
		jButtonSlow.setFocusable(false);
		jButtonSlow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonSlow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonSlow.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSlowActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonSlow);

		jButtonFast.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonFast.setForeground(new java.awt.Color(51, 51, 255));
		jButtonFast.setText("Fast");
		jButtonFast.setFocusable(false);
		jButtonFast.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonFast.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonFast.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonFastActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonFast);

		jLabel19.setText("      ");
		jToolBar2.add(jLabel19);

		jButtonCapture.setFont(new java.awt.Font("΢���ź�", 0, 12));
		jButtonCapture.setForeground(new java.awt.Color(51, 51, 255));
		jButtonCapture.setText("Capture");
		jButtonCapture.setFocusable(false);
		jButtonCapture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonCapture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonCapture.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonCaptureActionPerformed(evt);
			}
		});
		jToolBar2.add(jButtonCapture);

		jPanelPlayControl.add(jToolBar2);
		jToolBar2.setBounds(10, 350, 380, 25);

		getContentPane().add(jPanelPlayControl);
		jPanelPlayControl.setBounds(300, 10, 410, 380);

		jToggleButtonExit.setText("�˳�");
		jToggleButtonExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jToggleButtonExitActionPerformed(evt);
			}
		});
		getContentPane().add(jToggleButtonExit);
		jToggleButtonExit.setBounds(630, 580, 60, 23);

		jButtonDownload.setText("����");
		jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonDownloadActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonDownload);
		jButtonDownload.setBounds(20, 580, 90, 23);
		getContentPane().add(jProgressBar);
		jProgressBar.setBounds(130, 580, 410, 20);

		jPanelPlayInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
		jPanelPlayInfo.setLayout(null);
		jPanelPlayInfo.add(jTextFieldPlayTime);
		jTextFieldPlayTime.setBounds(0, 0, 160, 21);

		jButtonSave.setText("����");
		jButtonSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSaveActionPerformed(evt);
			}
		});
		jPanelPlayInfo.add(jButtonSave);
		jButtonSave.setBounds(0, 140, 70, 23);

		jButtonStopSave.setText("ֹͣ");
		jButtonStopSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonStopSaveActionPerformed(evt);
			}
		});
		jPanelPlayInfo.add(jButtonStopSave);
		jButtonStopSave.setBounds(80, 140, 70, 23);

		jLabel15.setText("�ļ���");
		jPanelPlayInfo.add(jLabel15);
		jLabel15.setBounds(10, 30, 40, 15);
		jPanelPlayInfo.add(jTextFieldFileName);
		jTextFieldFileName.setBounds(0, 50, 160, 21);

		jLabel16.setText("���ֽ���");
		jPanelPlayInfo.add(jLabel16);
		jLabel16.setBounds(10, 80, 48, 15);
		jPanelPlayInfo.add(jTextFieldTotalBytes);
		jTextFieldTotalBytes.setBounds(0, 100, 160, 21);

		getContentPane().add(jPanelPlayInfo);
		jPanelPlayInfo.setBounds(550, 390, 160, 180);

		pack();
	}

	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JButton jButtonCapture;
	private javax.swing.JButton jButtonDownload;
	private javax.swing.JButton jButtonFast;
	private javax.swing.JButton jButtonNormal;
	private javax.swing.JButton jButtonPause;
	private javax.swing.JButton jButtonPlay;
	private javax.swing.JButton jButtonSave;
	private javax.swing.JButton jButtonSearch;
	private javax.swing.JButton jButtonSlow;
	private javax.swing.JButton jButtonStop;
	private javax.swing.JButton jButtonStopSave;
	private javax.swing.JComboBox jComboBoxFlieType;
	private javax.swing.JComboBox jComboBoxRecodType;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel17;
	private javax.swing.JLabel jLabel18;
	private javax.swing.JLabel jLabel19;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel20;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JLabel jLabelEndTime;
	private javax.swing.JLabel jLabelFileType;
	private javax.swing.JLabel jLabelStartTime;
	private javax.swing.JLabel jLabelType;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanelPlayControl;
	private javax.swing.JPanel jPanelPlayInfo;
	private javax.swing.JPanel jPanelTime;
	private javax.swing.JProgressBar jProgressBar;
	private javax.swing.JRadioButton jRadioButtonByCardNumber;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSlider jSliderPlayback;
	private javax.swing.JTable jTableFile;
	private javax.swing.JTextField jTextFieldCardNumber;
	private javax.swing.JTextField jTextFieldChannelNumber;
	private javax.swing.JTextField jTextFieldFileName;
	private javax.swing.JTextField jTextFieldPlayTime;
	private javax.swing.JTextField jTextFieldTotalBytes;
	private javax.swing.JTextField jTextFieldeDay;
	private javax.swing.JTextField jTextFieldeHour;
	private javax.swing.JTextField jTextFieldeMinute;
	private javax.swing.JTextField jTextFieldeMonth;
	private javax.swing.JTextField jTextFieldeSecond;
	private javax.swing.JTextField jTextFieldeYear;
	private javax.swing.JTextField jTextFieldsDay;
	private javax.swing.JTextField jTextFieldsHour;
	private javax.swing.JTextField jTextFieldsMinute;
	private javax.swing.JTextField jTextFieldsMonth;
	private javax.swing.JTextField jTextFieldsSecond;
	private javax.swing.JTextField jTextFieldsYear;
	private javax.swing.JToggleButton jToggleButtonExit;
	private javax.swing.JToolBar jToolBar2;
	private java.awt.Panel panelPlayBack;
}
