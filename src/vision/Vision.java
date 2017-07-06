package vision;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * ��; ���û�ע�ᣬԤ�����������ò˵� ������Jframe
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class Vision extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	static PlayCtrl playControl = PlayCtrl.INSTANCE;

	public static NativeLong g_lVoiceHandle;// ȫ�ֵ������Խ����

	HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;// �豸��Ϣ
	HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;// IP����
	HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;// �û�����

	boolean bRealPlay;// �Ƿ���Ԥ��.
	String m_sDeviceIP;// �ѵ�¼�豸��IP��ַ

	NativeLong lUserID;// �û����
	NativeLong lPreviewHandle;// Ԥ�����
	NativeLongByReference m_lPort;// �ص�Ԥ��ʱ���ſ�˿�ָ��

	NativeLong lAlarmHandle;// �����������
	NativeLong lListenHandle;// �����������

	FMSGCallBack fMSFCallBack;// �����ص�����ʵ��
	FRealDataCallBack fRealDataCallBack;// Ԥ���ص�����ʵ��

	JFramePTZControl framePTZControl;// ��̨���ƴ���

	int m_iTreeNodeNum;// ͨ�����ڵ���Ŀ
	DefaultMutableTreeNode m_DeviceRoot;// ͨ�������ڵ�

	private javax.swing.JButton jButtonExit;
	private javax.swing.JButton jButtonLogin;
	private javax.swing.JButton jButtonRealPlay;
	private javax.swing.JComboBox jComboBoxCallback;
	private javax.swing.JLabel jLabelIPAddress;
	private javax.swing.JLabel jLabelPassWord;
	private javax.swing.JLabel jLabelPortNumber;
	private javax.swing.JLabel jLabelUserName;
	private javax.swing.JMenuBar jMenuBarConfig;
	private javax.swing.JMenu jMenuConfig;
	private javax.swing.JMenuItem jMenuItemAlarmCfg;
	private javax.swing.JMenuItem jMenuItemBasicConfig;
	private javax.swing.JMenuItem jMenuItemChannel;
	private javax.swing.JMenuItem jMenuItemCheckTime;
	private javax.swing.JMenuItem jMenuItemDefault;
	private javax.swing.JMenuItem jMenuItemDeviceState;
	private javax.swing.JMenuItem jMenuItemFormat;
	private javax.swing.JMenuItem jMenuItemIPAccess;
	private javax.swing.JMenuItem jMenuItemNetwork;
	private javax.swing.JMenuItem jMenuItemPlayBackRemote;
	private javax.swing.JMenuItem jMenuItemPlayTime;
	private javax.swing.JMenuItem jMenuItemReboot;
	private javax.swing.JMenuItem jMenuItemRemoveAlarm;
	private javax.swing.JMenuItem jMenuItemSerialCfg;
	private javax.swing.JMenuItem jMenuItemShutDown;
	private javax.swing.JMenuItem jMenuItemUpgrade;
	private javax.swing.JMenuItem jMenuItemUserConfig;
	private javax.swing.JMenuItem jMenuItemVoiceCom;
	private javax.swing.JMenu jMenuManage;
	private javax.swing.JMenu jMenuPlayBack;
	private javax.swing.JMenu jMenuSetAlarm;
	private javax.swing.JMenu jMenuVoice;
	private javax.swing.JPanel jPanelRealplayArea;
	private javax.swing.JPanel jPanelUserInfo;
	private javax.swing.JPasswordField jPasswordFieldPassword;
	private javax.swing.JRadioButtonMenuItem jRadioButtonMenuListen;
	private javax.swing.JRadioButtonMenuItem jRadioButtonMenuSetAlarm;
	private javax.swing.JScrollPane jScrollPaneTree;
	private javax.swing.JScrollPane jScrollPanelAlarmList;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JSplitPane jSplitPaneHorizontal;
	private javax.swing.JSplitPane jSplitPaneVertical;
	private javax.swing.JTable jTableAlarm;
	private javax.swing.JTextField jTextFieldIPAddress;
	private javax.swing.JTextField jTextFieldPortNumber;
	private javax.swing.JTextField jTextFieldUserName;
	private javax.swing.JTree jTreeDevice;
	private java.awt.Panel panelRealplay;

	/**
	 * ����: ���๹�캯�� ��������: ��ʼ����Ա
	 */
	public Vision() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);// ��ֹ�����Ŵ���(AWT���)����
		initComponents();
		lUserID = new NativeLong(-1);
		lPreviewHandle = new NativeLong(-1);
		lAlarmHandle = new NativeLong(-1);
		lListenHandle = new NativeLong(-1);
		g_lVoiceHandle = new NativeLong(-1);
		m_lPort = new NativeLongByReference(new NativeLong(-1));
		fMSFCallBack = null;
		fRealDataCallBack = new FRealDataCallBack();
		m_iTreeNodeNum = 0;
	}

	/**
	 * ����: "ע��" ��ť������Ӧ���� ��������: ע���¼�豸
	 * 
	 * @param evt
	 */
	private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) {
		// ע��֮ǰ��ע����ע����û�,Ԥ������²���ע��
		if (bRealPlay) {
			JOptionPane.showMessageDialog(this, "ע�����û�����ֹͣ��ǰԤ��!");
			return;
		}
		if (lUserID.longValue() > -1) {
			// ��ע��
			hCNetSDK.NET_DVR_Logout_V30(lUserID);
			lUserID = new NativeLong(-1);
			m_iTreeNodeNum = 0;
			m_DeviceRoot.removeAllChildren();
		}
		// ע��
		m_sDeviceIP = jTextFieldIPAddress.getText();// �豸ip��ַ
		m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
		int iPort = Integer.parseInt(jTextFieldPortNumber.getText());
		lUserID = hCNetSDK.NET_DVR_Login_V30(m_sDeviceIP, (short) iPort, jTextFieldUserName.getText(),
				new String(jPasswordFieldPassword.getPassword()), m_strDeviceInfo);

		long userID = lUserID.longValue();
		if (userID == -1) {
			m_sDeviceIP = "";// ��¼δ�ɹ�,IP��Ϊ��
			JOptionPane.showMessageDialog(Vision.this, "ע��ʧ��");
		} else {
			CreateDeviceTree();
		}
	}

	/**
	 * ����: initialTableModel ��������: ��ʼ��������Ϣ�б�,д��������
	 * 
	 * @return
	 */
	public DefaultTableModel initialTableModel() {
		String tabeTile[];
		tabeTile = new String[] { "ʱ��", "������Ϣ", "�豸��Ϣ" };
		DefaultTableModel alarmTableModel = new DefaultTableModel(tabeTile, 0);
		return alarmTableModel;
	}

	/**
	 * ����: initialTreeModel ��������: ��ʼ���豸��
	 * 
	 * @return
	 */
	private DefaultTreeModel initialTreeModel() {
		m_DeviceRoot = new DefaultMutableTreeNode("Device");
		DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRoot);// ʹ�ø��ڵ㴴��ģ��
		return myDefaultTreeModel;
	}

	/**
	 * ����: " �˳�" ��ť��Ӧ���� ��������: ע�����˳�
	 * 
	 * @param evt
	 */
	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {
		// �����Ԥ��,��ֹͣԤ��, �ͷž��
		if (lPreviewHandle.longValue() > -1) {
			hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
			if (framePTZControl != null) {
				framePTZControl.dispose();
			}
		}

		// ��������
		if (lAlarmHandle.intValue() != -1) {
			hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle);
		}
		// ֹͣ����
		if (lListenHandle.intValue() != -1) {
			hCNetSDK.NET_DVR_StopListen_V30(lListenHandle);
		}

		// ����Ѿ�ע��,ע��
		if (lUserID.longValue() > -1) {
			hCNetSDK.NET_DVR_Logout_V30(lUserID);
		}
		// cleanup SDK
		hCNetSDK.NET_DVR_Cleanup();
		this.dispose();
	}

	/**
	 * ����: "��ձ�����Ϣ" �˵�����Ӧ���� ��������: ���������Ϣ�б�
	 * 
	 * @param evt
	 */
	private void jMenuItemRemoveAlarmMousePressed(java.awt.event.MouseEvent evt) {
		// ɾ��������
		((DefaultTableModel) jTableAlarm.getModel()).getDataVector().removeAllElements();
		// �Ѹı���ʾ���б�ؼ�
		((DefaultTableModel) jTableAlarm.getModel()).fireTableStructureChanged();
	}

	/**
	 * ����: "��������" �˵�����Ӧ���� ��������: ѡ�п�ʼ����,ȡ����������
	 * 
	 * @param evt
	 */
	private void jRadioButtonMenuListenActionPerformed(java.awt.event.ActionEvent evt) {
		if (jRadioButtonMenuListen.isSelected() == true) {// ѡ�����

			if (lListenHandle.intValue() == -1) {// ��δ����,��ʼ����
				if (fMSFCallBack == null) {
					fMSFCallBack = new FMSGCallBack();
				}
				Pointer pUser = null;
				if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser)) {
					System.out.println("���ûص�����ʧ��!");
				}
				// ����IP��ַ��Ϊnullʱ�Զ���ȡ����IP
				lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(null, (short) 7200, fMSFCallBack, null);
				if (lListenHandle.intValue() == -1) {
					JOptionPane.showMessageDialog(this, "��ʼ����ʧ��");
					jRadioButtonMenuListen.setSelected(false);
				}
			}
		} else {// ֹͣ����
			if (lListenHandle.intValue() != -1) {
				if (!hCNetSDK.NET_DVR_StopListen_V30(lListenHandle)) {
					JOptionPane.showMessageDialog(this, "ֹͣ����ʧ��");
					jRadioButtonMenuListen.setSelected(true);
					lListenHandle = new NativeLong(-1);
				} else {
					lListenHandle = new NativeLong(-1);
				}
			}
		}
	}

	/**
	 * ����: "��������" �˵�����Ӧ���� ��������: ѡ�в�����,ȡ������
	 * 
	 * @param evt
	 */
	private void jRadioButtonMenuSetAlarmActionPerformed(java.awt.event.ActionEvent evt) {// GEN-HEADEREND:event_jRadioButtonMenuSetAlarmActionPerformed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		if (jRadioButtonMenuSetAlarm.isSelected() == true) {// ��ѡ�񲼷�
			if (lAlarmHandle.intValue() == -1) {// ��δ����,��Ҫ����
				if (fMSFCallBack == null) {
					fMSFCallBack = new FMSGCallBack();
					Pointer pUser = null;
					if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser)) {
						System.out.println("���ûص�����ʧ��!");
					}
				}
				lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V30(lUserID);
				if (lAlarmHandle.intValue() == -1) {
					JOptionPane.showMessageDialog(this, "����ʧ��");
					jRadioButtonMenuSetAlarm.setSelected(false);
				}
			}
		} else {// δѡ�񲼷�
			if (lAlarmHandle.intValue() != -1) {
				if (!hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
					JOptionPane.showMessageDialog(this, "����ʧ��");
					jRadioButtonMenuSetAlarm.setSelected(true);
					lAlarmHandle = new NativeLong(-1);
				} else {
					lAlarmHandle = new NativeLong(-1);
				}
			}
		}
	}

	/**
	 * ����: "Ԥ��" ��ť������Ӧ���� ��������: ��ȡͨ����,�򿪲��Ŵ���,��ʼ��ͨ����Ԥ��
	 * 
	 * @param evt
	 */
	private void jButtonRealPlayActionPerformed(java.awt.event.ActionEvent evt) {// GEN-HEADEREND:event_jButtonRealPlayActionPerformed
		System.out.println(panelRealplay.getWidth());
		System.out.println(panelRealplay.getHeight());
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ���Ԥ������û��,����Ԥ��
		if (bRealPlay == false) {
			// ��ȡ���ھ��
			HWND hwnd = new HWND(Native.getComponentPointer(panelRealplay));
			// ��ȡͨ����
			int iChannelNum = getChannelNumber();// ͨ����
			if (iChannelNum == -1) {
				JOptionPane.showMessageDialog(this, "��ѡ��ҪԤ����ͨ��");
				return;
			}
			m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
			m_strClientInfo.lChannel = new NativeLong(iChannelNum);

			// �ڴ��ж��Ƿ�ص�Ԥ��,0,���ص� 1 �ص�
			if (jComboBoxCallback.getSelectedIndex() == 0) {
				m_strClientInfo.hPlayWnd = hwnd;
				lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID, m_strClientInfo, null, null, true);
			} else if (jComboBoxCallback.getSelectedIndex() == 1) {
				m_strClientInfo.hPlayWnd = null;
				lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID, m_strClientInfo, fRealDataCallBack, null, true);
			}
			long previewSucValue = lPreviewHandle.longValue();
			// Ԥ��ʧ��ʱ:
			if (previewSucValue == -1) {
				JOptionPane.showMessageDialog(this, "Ԥ��ʧ��");
				return;
			}
			// Ԥ���ɹ��Ĳ���
			jButtonRealPlay.setText("ֹͣ");
			bRealPlay = true;
			// ��ʾ��̨���ƴ���
			framePTZControl = new JFramePTZControl(lPreviewHandle, new NativeLong(iChannelNum), lUserID);
			framePTZControl.setLocation(this.getX() + this.getWidth(), this.getY());
			framePTZControl.setVisible(true);
		}
		// �����Ԥ��,ֹͣԤ��,�رմ���
		else {
			hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
			jButtonRealPlay.setText("Ԥ��");
			bRealPlay = false;
			if (m_lPort.getValue().intValue() != -1) {
				playControl.PlayM4_Stop(m_lPort.getValue());
				m_lPort.setValue(new NativeLong(-1));
			}
			framePTZControl.dispose();
			panelRealplay.repaint();
		}
	}

	/**
	 * ����: "����" �˵�����Ӧ���� ��������: �½�������ʾ����
	 * 
	 * @param evt
	 */
	private void jMenuItemSerialCfgMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogSerialCfg dlgSerialCfg = new JDialogSerialCfg(this, false, lUserID, m_strDeviceInfo);
		centerWindow(dlgSerialCfg);
		dlgSerialCfg.setVisible(true);
	}

	/**
	 * ����: "��������" �˵�����Ӧ���� ��������: �½�������ʾ��������
	 * 
	 * @param evt
	 */
	private void jMenuItemAlarmCfgMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemAlarmCfgMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogAlarmCfg dlgAlarmCfg = new JDialogAlarmCfg(this, false, lUserID, m_strDeviceInfo);
		dlgAlarmCfg.setLocation(this.getX(), this.getY());
		dlgAlarmCfg.setVisible(true);
	}

	/**
	 * ����: "ͨ������" �˵�����Ӧ���� ��������:�����ʾͨ����������
	 * 
	 * @param evt
	 */
	private void jMenuItemChannelMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemChannelMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogChannelConfig dialogChannelConfig = new JDialogChannelConfig(this, false, lUserID, m_strDeviceInfo);// ��ģʽ�Ի���
		dialogChannelConfig.setBounds(this.getX(), this.getY(), 670, 640);
		dialogChannelConfig.setVisible(true);
		// ������
		int iStartChan = m_strDeviceInfo.byStartChan;
		int iChannum = m_strDeviceInfo.byChanNum;
		// ��ʼ��ͨ������Ͽ�
		for (int i = 0; i < iChannum; i++) {
			dialogChannelConfig.jComboBoxChannelNumber.addItem("Camera" + (i + iStartChan));
		}
		for (int i = 0; i < HCNetSDK.MAX_IP_CHANNEL; i++) {
			if (m_strIpparaCfg.struIPChanInfo[i].byEnable == 1) {
				dialogChannelConfig.jComboBoxChannelNumber.addItem("IPCamara" + (i + iStartChan));
			}
		}
	}

	/**
	 * ����: "�������" �˵�����Ӧ���� ��������: �����ʾ�����������
	 * 
	 * @param evt
	 */
	private void jMenuItemNetworkMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemNetworkMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��Jframe
		JFrameNetWorkConfig frameNetwork = new JFrameNetWorkConfig(lUserID);
		frameNetwork.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frameNetwork.setSize(550, 380);
		centerWindow(frameNetwork);
		frameNetwork.setVisible(true);
	}// GEN-LAST:event_jMenuItemNetworkMousePressed

	/**
	 * ����: "������Ϣ" �˵�����Ӧ���� ��������: �½�����,��ʾ�豸������Ϣ
	 * 
	 * @param evt
	 */
	private void jMenuItemBasicConfigMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogBasicConfig dlgBasicConfig = new JDialogBasicConfig(this, false, lUserID);
		dlgBasicConfig.setSize(507, 400);
		centerWindow(dlgBasicConfig);
		dlgBasicConfig.setVisible(true);
	}// GEN-LAST:event_jMenuItemBasicConfigMousePressed

	/**
	 * ����: "�豸״̬" �˵�����Ӧ���� ��������: �½�������ʾ�豸״̬
	 * 
	 * @param evt
	 */
	private void jMenuItemDeviceStateMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemDeviceStateMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogDeviceState dlgDeviceState = new JDialogDeviceState(this, false, lUserID, m_strDeviceInfo, m_sDeviceIP);
		dlgDeviceState.setSize(680, 715);
		centerWindow(dlgDeviceState);
		dlgDeviceState.setVisible(true);
	}

	/**
	 * ����: "�ָ�Ĭ�ϲ���" �˵�����Ӧ���� ��������: ����ȷ�Ͽ�,�Ƿ�ָ�Ĭ�ϲ���
	 * 
	 * @param evt
	 */
	private void jMenuItemDefaultMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}

		int iResponse = JOptionPane.showConfirmDialog(this, "ȷ���ָ�Ĭ�ϲ���?", "�ָ�Ĭ�ϲ���", JOptionPane.OK_CANCEL_OPTION);
		if (iResponse == 0) {// ȷ��
			if (!hCNetSDK.NET_DVR_RestoreConfig(lUserID)) {
				JOptionPane.showMessageDialog(this, "�ָ�Ĭ�ϲ���ʧ��");
				return;
			}
		}
		if (iResponse == 2) {// ȡ��
			return;
		}
	}

	/**
	 * ����: "�ر�" �˵�����Ӧ���� ��������: ����ȷ�Ͽ�ѯ���Ƿ�ػ�
	 * 
	 * @param evt
	 */
	private void jMenuItemShutDownMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemShutDownMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		int iResponse = JOptionPane.showConfirmDialog(this, "ȷ���ر��豸?", "�ػ�", JOptionPane.OK_CANCEL_OPTION);
		// ȷ��
		if (iResponse == 0) {
			if (!hCNetSDK.NET_DVR_ShutDownDVR(lUserID)) {
				JOptionPane.showMessageDialog(this, "�ر��豸ʧ��");
				return;
			}
		}
		// ȡ��
		if (iResponse == 2) {
			return;
		}
	}

	/**
	 * ����: "����" �˵�����Ӧ���� ��������: ����ȷ�Ͽ�ѯ���Ƿ������豸
	 * 
	 * @param evt
	 */
	private void jMenuItemRebootMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}

		int iResponse = JOptionPane.showConfirmDialog(this, "ȷ�������豸?", "����", JOptionPane.OK_CANCEL_OPTION);
		// ȷ��
		if (iResponse == 0) {
			if (!hCNetSDK.NET_DVR_RebootDVR(lUserID)) {
				JOptionPane.showMessageDialog(this, "�豸����ʧ��");
				return;
			}
		}
		// ȡ��
		if (iResponse == 2) {
			return;
		}
	}

	/**
	 * ����: "����" �˵�����Ӧ���� ��������: �½�����,��ʾ����ѡ��
	 * 
	 * @param evt
	 */
	private void jMenuItemUpgradeMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogUpGrade dlgUpgrade = new JDialogUpGrade(this, false, lUserID);
		dlgUpgrade.setSize(440, 265);
		centerWindow(dlgUpgrade);
		dlgUpgrade.setVisible(true);
	}

	/**
	 * ����: "��ʽ��" �˵�����Ӧ���� ��������: �½�����,��ʾ��ʽ��ѡ��
	 * 
	 * @param evt
	 */
	private void jMenuItemFormatMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogFormatDisk dlgFormatDisk = new JDialogFormatDisk(this, false, lUserID);
		centerWindow(dlgFormatDisk);
		dlgFormatDisk.setVisible(true);
	}

	/**
	 * ����: "Уʱ" �˵�����Ӧ���� ��������: �½�����,��ʾУʱѡ��
	 * 
	 * @param evt
	 */
	private void jMenuItemCheckTimeMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogCheckTime dlgCheckTime = new JDialogCheckTime(this, false, lUserID);
		centerWindow(dlgCheckTime);
		dlgCheckTime.setVisible(true);
	}

	/**
	 * ����: "ʱ��ط�" �˵�����Ӧ���� ��������: �½�����ʱ��ط�
	 * 
	 * @param evt
	 */
	private void jMenuItemPlayTimeMousePressed(java.awt.event.MouseEvent evt) {
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		JDialogPlayBackByTime dlgPlayTime = new JDialogPlayBackByTime(this, false, lUserID, m_sDeviceIP);
		centerWindow(dlgPlayTime);
		dlgPlayTime.setVisible(true);
	}

	/**
	 * ����: "�ط�" ���ļ� �˵�����Ӧ���� ��������: ����򿪻طŽ���
	 * 
	 * @param evt
	 */
	private void jMenuItemPlayBackRemoteMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemPlayBackRemoteMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		// ��JDialog
		// ��ģʽ�Ի���
		JDialogPlayBack dialogPlayBack = new JDialogPlayBack(this, false, lUserID);
		dialogPlayBack.setBounds(this.getX(), this.getY(), 730, 650);
		centerWindow(dialogPlayBack);
		dialogPlayBack.setVisible(true);
	}

	/**
	 * ����: "�û�����" ���ļ� �˵�����Ӧ���� ��������: ����򿪶Ի���,��ʼ�û�����
	 * 
	 * @param evt
	 */
	private void jMenuItemUserConfigMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemUserConfigMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		JDialogUserConfig dlgUserConfig = new JDialogUserConfig(this, false, lUserID, m_strDeviceInfo, m_strIpparaCfg);
		centerWindow(dlgUserConfig);
		dlgUserConfig.setVisible(true);
	}

	/**
	 * ����: "�����Խ�" ���ļ� �˵�����Ӧ���� ��������: ����򿪶Ի���,��ʼ�����Խ���ز���
	 * 
	 * @param evt
	 */
	private void jMenuItemVoiceComMousePressed(java.awt.event.MouseEvent evt) {// GEN-HEADEREND:event_jMenuItemVoiceComMousePressed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		JDialogVoiceTalk dlgVoiceTalk = new JDialogVoiceTalk(this, false, lUserID, m_strDeviceInfo);
		centerWindow(dlgVoiceTalk);
		dlgVoiceTalk.setVisible(true);
	}

	/**
	 * ����: "Ip����" ���ļ� �˵�����Ӧ���� ��������: ����򿪶Ի���,IP��������
	 * 
	 * @param evt
	 */
	private void jMenuItemIPAccessActionPerformed(java.awt.event.ActionEvent evt) {// GEN-HEADEREND:event_jMenuItemIPAccessActionPerformed
		if (lUserID.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "����ע��");
			return;
		}
		JDialogIPAccessCfg dlgIPAccess = new JDialogIPAccessCfg(this, false, lUserID, m_strDeviceInfo);
		centerWindow(dlgIPAccess);
		dlgIPAccess.setVisible(true);
	}

	/**
	 * ����: "���Ŵ���" ˫����Ӧ���� ��������: ˫��ȫ��Ԥ����ǰԤ��ͨ��
	 * 
	 * @param evt
	 */
	private void panelRealplayMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_panelRealplayMousePressed
		if (!bRealPlay) {
			return;
		}
		// ��굥���¼�Ϊ˫��
		if (evt.getClickCount() == 2) {
			// �½�JWindow ȫ��Ԥ��
			final JWindow wnd = new JWindow();
			// ��ȡ��Ļ�ߴ�
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			wnd.setSize(screenSize);
			wnd.setVisible(true);

			final HWND hwnd = new HWND(Native.getComponentPointer(wnd));
			m_strClientInfo.hPlayWnd = hwnd;
			final NativeLong lRealHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID, m_strClientInfo, null, null, true);

			// JWindow����˫����Ӧ����,˫��ʱֹͣԤ��,�˳�ȫ��
			wnd.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						// ֹͣԤ��
						hCNetSDK.NET_DVR_StopRealPlay(lRealHandle);
						wnd.dispose();
					}
				}
			});

		}
	}

	/**
	 * ����: centerWindow ��������:��������
	 * 
	 * @param window
	 */
	public static void centerWindow(Container window) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = window.getSize().width;
		int h = window.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		window.setLocation(x, y);
	}

	/**
	 * ����: CreateDeviceTree ��������:�����豸ͨ����
	 */
	private void CreateDeviceTree() {
		IntByReference ibrBytesReturned = new IntByReference(0);// ��ȡIP�������ò���
		boolean bRet = false;
		m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		m_strIpparaCfg.write();
		Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
		bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig,
				m_strIpparaCfg.size(), ibrBytesReturned);
		m_strIpparaCfg.read();
		DefaultTreeModel TreeModel = ((DefaultTreeModel) jTreeDevice.getModel());// ��ȡ��ģ��
		if (!bRet) {
			// �豸��֧��,���ʾû��IPͨ��
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
						"Camera" + (iChannum + m_strDeviceInfo.byStartChan));
				TreeModel.insertNodeInto(newNode, m_DeviceRoot, iChannum);
			}
		} else {
			// �豸֧��IPͨ��
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				if (m_strIpparaCfg.byAnalogChanEnable[iChannum] == 1) {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
							"Camera" + (iChannum + m_strDeviceInfo.byStartChan));
					TreeModel.insertNodeInto(newNode, m_DeviceRoot, m_iTreeNodeNum);
					m_iTreeNodeNum++;
				}
			}
			for (int iChannum = 0; iChannum < HCNetSDK.MAX_IP_CHANNEL; iChannum++)
				if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
							"IPCamera" + (iChannum + m_strDeviceInfo.byStartChan));
					TreeModel.insertNodeInto(newNode, m_DeviceRoot, m_iTreeNodeNum);
				}
		}
		TreeModel.reload();// ����ӵĽڵ���ʾ������
		jTreeDevice.setSelectionInterval(1, 1);// ѡ�е�һ���ڵ�
	}

	/**
	 * ����: getChannelNumber ��������:���豸����ȡͨ����
	 * 
	 * @return
	 */
	public int getChannelNumber() {
		int iChannelNum = -1;
		TreePath tp = jTreeDevice.getSelectionPath();// ��ȡѡ�нڵ��·��
		if (tp != null) {// �ж�·���Ƿ���Ч,���ж��Ƿ���ͨ����ѡ��
							// ��ȡѡ�е�ͨ����,��ͨ�������з���:
			String sChannelName = ((DefaultMutableTreeNode) tp.getLastPathComponent()).toString();
			if (sChannelName.charAt(0) == 'C') {// Camara��ͷ��ʾģ��ͨ��
												// ���ַ����л�ȡͨ����
				iChannelNum = Integer.parseInt(sChannelName.substring(6));
			} else {
				if (sChannelName.charAt(0) == 'I') {// IPCamara��ͷ��ʾIPͨ��
													// ���ַ����л�ȡͨ����,IPͨ����Ҫ��32
					iChannelNum = Integer.parseInt(sChannelName.substring(8)) + 32;
				} else {
					return -1;
				}
			}
		} else {
			return -1;
		}
		return iChannelNum;
	}

	/**
	 * �ڲ���: FMSGCallBack ������Ϣ�ص�����
	 * 
	 * @author WSL
	 *
	 */
	public class FMSGCallBack implements HCNetSDK.FMSGCallBack {
		// ������Ϣ�ص�����
		public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo,
				int dwBufLen, Pointer pUser) {
			String sAlarmType = new String();
			DefaultTableModel alarmTableModel = ((DefaultTableModel) jTableAlarm.getModel());// ��ȡ���ģ��
			String[] newRow = new String[3];
			// ����ʱ��
			Date today = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String[] sIP = new String[2];
			// lCommand�Ǵ��ı�������
			switch (lCommand.intValue()) {
			// 9000����
			case HCNetSDK.COMM_ALARM_V30:
				HCNetSDK.NET_DVR_ALARMINFO_V30 strAlarmInfoV30 = new HCNetSDK.NET_DVR_ALARMINFO_V30();
				strAlarmInfoV30.write();
				Pointer pInfoV30 = strAlarmInfoV30.getPointer();
				pInfoV30.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfoV30.size());
				strAlarmInfoV30.read();
				switch (strAlarmInfoV30.dwAlarmType) {
				case 0:
					sAlarmType = new String("�ź�������");
					break;
				case 1:
					sAlarmType = new String("Ӳ����");
					break;
				case 2:
					sAlarmType = new String("�źŶ�ʧ");
					break;
				case 3:
					sAlarmType = new String("�ƶ����");
					break;
				case 4:
					sAlarmType = new String("Ӳ��δ��ʽ��");
					break;
				case 5:
					sAlarmType = new String("��дӲ�̳���");
					break;
				case 6:
					sAlarmType = new String("�ڵ�����");
					break;
				case 7:
					sAlarmType = new String("��ʽ��ƥ��");
					break;
				case 8:
					sAlarmType = new String("�Ƿ�����");
					break;
				}
				newRow[0] = dateFormat.format(today);
				// ��������
				newRow[1] = sAlarmType;
				// �����豸IP��ַ
				sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
				newRow[2] = sIP[0];
				alarmTableModel.insertRow(0, newRow);
				break;
			// 8000����
			case HCNetSDK.COMM_ALARM:
				HCNetSDK.NET_DVR_ALARMINFO strAlarmInfo = new HCNetSDK.NET_DVR_ALARMINFO();
				strAlarmInfo.write();
				Pointer pInfo = strAlarmInfo.getPointer();
				pInfo.write(0, pAlarmInfo.RecvBuffer, 0, strAlarmInfo.size());
				strAlarmInfo.read();
				switch (strAlarmInfo.dwAlarmType) {
				case 0:
					sAlarmType = new String("�ź�������");
					break;
				case 1:
					sAlarmType = new String("Ӳ����");
					break;
				case 2:
					sAlarmType = new String("�źŶ�ʧ");
					break;
				case 3:
					sAlarmType = new String("�ƶ����");
					break;
				case 4:
					sAlarmType = new String("Ӳ��δ��ʽ��");
					break;
				case 5:
					sAlarmType = new String("��дӲ�̳���");
					break;
				case 6:
					sAlarmType = new String("�ڵ�����");
					break;
				case 7:
					sAlarmType = new String("��ʽ��ƥ��");
					break;
				case 8:
					sAlarmType = new String("�Ƿ�����");
					break;
				}
				newRow[0] = dateFormat.format(today);
				// ��������
				newRow[1] = sAlarmType;
				// �����豸IP��ַ
				sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
				newRow[2] = sIP[0];
				alarmTableModel.insertRow(0, newRow);
				break;
			// ATM DVR transaction information
			case HCNetSDK.COMM_TRADEINFO:
				// ��������Ϣ����
				break;
			// IPC�������øı䱨��
			case HCNetSDK.COMM_IPCCFG:
				// ����IPC����
				break;
			default:
				System.out.println("δ֪��������");
				break;
			}
		}
	}

	/**
	 * �ڲ���: FRealDataCallBack ʵ��Ԥ���ص�����
	 * 
	 * @author WSL
	 *
	 */
	byte[] b_arr;

	class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
		// Ԥ���ص�
		public void invoke(NativeLong lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize,
				Pointer pUser) {
			HWND hwnd = new HWND(Native.getComponentPointer(panelRealplay));
			switch (dwDataType) {
			case HCNetSDK.NET_DVR_SYSHEAD: // ϵͳͷ
				if (!playControl.PlayM4_GetPort(m_lPort)) {// ��ȡ���ſ�δʹ�õ�ͨ����
					break;
				} else {
					System.out.println("call back --> PlayM4_GetPort");
				}
				if (dwBufSize > 0) {
					if (!playControl.PlayM4_SetStreamOpenMode(m_lPort.getValue(), PlayCtrl.STREAME_REALTIME)) {// ����ʵʱ������ģʽ
						break;
					} else {
						System.out.println("call back --> PlayM4_SetStreamOpenMode");
					}
					if (!playControl.PlayM4_OpenStream(m_lPort.getValue(), pBuffer, dwBufSize, 1024 * 1024)) {// �����ӿ�
						break;
					} else {
						System.out.println("call back --> PlayM4_OpenStream");
					}
					if (!playControl.PlayM4_Play(m_lPort.getValue(), hwnd)) {// ���ſ�ʼ
						break;
					} else {
						System.out.println("call back --> PlayM4_Play");
					}
				}
			case HCNetSDK.NET_DVR_STREAMDATA: // ��������
				if ((dwBufSize > 0) && (m_lPort.getValue().intValue() != -1)) {
					if (!playControl.PlayM4_InputData(m_lPort.getValue(), pBuffer, dwBufSize)) {// ����������
						break;
					} else {
						System.out.println("call back --> PlayM4_InputData");
					}
					Pointer p = pBuffer.getPointer();
					b_arr = p.getByteArray(0, dwBufSize);
					System.out.println("-->" + dwBufSize);
				}
			}
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void initComponents() {
		jSplitPaneHorizontal = new javax.swing.JSplitPane();
		jPanelUserInfo = new javax.swing.JPanel();
		jButtonRealPlay = new javax.swing.JButton();
		jButtonLogin = new javax.swing.JButton();
		jLabelUserName = new javax.swing.JLabel();
		jLabelIPAddress = new javax.swing.JLabel();
		jTextFieldPortNumber = new javax.swing.JTextField();
		jTextFieldIPAddress = new javax.swing.JTextField();
		jLabelPortNumber = new javax.swing.JLabel();
		jLabelPassWord = new javax.swing.JLabel();
		jPasswordFieldPassword = new javax.swing.JPasswordField();
		jTextFieldUserName = new javax.swing.JTextField();
		jButtonExit = new javax.swing.JButton();
		jScrollPaneTree = new javax.swing.JScrollPane();
		jTreeDevice = new javax.swing.JTree();
		jComboBoxCallback = new javax.swing.JComboBox();
		jSplitPaneVertical = new javax.swing.JSplitPane();
		jPanelRealplayArea = new javax.swing.JPanel();
		panelRealplay = new java.awt.Panel();
		jScrollPanelAlarmList = new javax.swing.JScrollPane();
		jTableAlarm = new javax.swing.JTable();
		jMenuBarConfig = new javax.swing.JMenuBar();
		jMenuConfig = new javax.swing.JMenu();
		jMenuItemBasicConfig = new javax.swing.JMenuItem();
		jMenuItemNetwork = new javax.swing.JMenuItem();
		jMenuItemChannel = new javax.swing.JMenuItem();
		jMenuItemAlarmCfg = new javax.swing.JMenuItem();
		jMenuItemSerialCfg = new javax.swing.JMenuItem();
		jMenuItemUserConfig = new javax.swing.JMenuItem();
		jMenuItemIPAccess = new javax.swing.JMenuItem();
		jMenuPlayBack = new javax.swing.JMenu();
		jMenuItemPlayBackRemote = new javax.swing.JMenuItem();
		jMenuItemPlayTime = new javax.swing.JMenuItem();
		jMenuSetAlarm = new javax.swing.JMenu();
		jRadioButtonMenuSetAlarm = new javax.swing.JRadioButtonMenuItem();
		jRadioButtonMenuListen = new javax.swing.JRadioButtonMenuItem();
		jMenuItemRemoveAlarm = new javax.swing.JMenuItem();
		jMenuManage = new javax.swing.JMenu();
		jMenuItemCheckTime = new javax.swing.JMenuItem();
		jMenuItemFormat = new javax.swing.JMenuItem();
		jMenuItemUpgrade = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		jMenuItemReboot = new javax.swing.JMenuItem();
		jMenuItemShutDown = new javax.swing.JMenuItem();
		jSeparator2 = new javax.swing.JSeparator();
		jMenuItemDefault = new javax.swing.JMenuItem();
		jMenuItemDeviceState = new javax.swing.JMenuItem();
		jMenuVoice = new javax.swing.JMenu();
		jMenuItemVoiceCom = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("ClientDemo");
		setFont(new java.awt.Font("����", 0, 10));

		jSplitPaneHorizontal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jSplitPaneHorizontal.setDividerLocation(155);
		jSplitPaneHorizontal.setDividerSize(2);

		jPanelUserInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 255, 255), null));

		jButtonRealPlay.setText("Ԥ��");
		jButtonRealPlay.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonRealPlayActionPerformed(evt);
			}
		});

		jButtonLogin.setText("ע��");
		jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonLoginActionPerformed(evt);
			}
		});

		jLabelUserName.setText("�û���");
		jLabelIPAddress.setText("IP��ַ");
		jTextFieldPortNumber.setText("8000");
		jTextFieldIPAddress.setText("192.168.1.64");
		jLabelPortNumber.setText("�˿�");
		jLabelPassWord.setText("����");
		jPasswordFieldPassword.setText("wsl87654321.");
		jTextFieldUserName.setText("admin");

		jButtonExit.setText("�˳�");
		jButtonExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});

		jTreeDevice.setModel(this.initialTreeModel());
		jScrollPaneTree.setViewportView(jTreeDevice);

		jComboBoxCallback.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ֱ��Ԥ��", "�ص�Ԥ��" }));

		javax.swing.GroupLayout jPanelUserInfoLayout = new javax.swing.GroupLayout(jPanelUserInfo);
		jPanelUserInfo.setLayout(jPanelUserInfoLayout);
		jPanelUserInfoLayout.setHorizontalGroup(jPanelUserInfoLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addGap(10, 10, 10).addComponent(jLabelIPAddress)
						.addGap(14, 14, 14).addComponent(jTextFieldIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE,
								80, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addGap(10, 10, 10).addComponent(jLabelUserName)
						.addGap(14, 14, 14).addComponent(jTextFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 80,
								javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addGap(10, 10, 10).addComponent(jLabelPassWord)
						.addGap(26, 26, 26).addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE,
								80, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addContainerGap().addComponent(jButtonLogin)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jButtonRealPlay))
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addGap(10, 10, 10).addComponent(jLabelPortNumber)
						.addGap(26, 26, 26).addComponent(jTextFieldPortNumber, javax.swing.GroupLayout.PREFERRED_SIZE,
								80, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanelUserInfoLayout.createSequentialGroup().addContainerGap(83, Short.MAX_VALUE)
								.addComponent(jButtonExit).addContainerGap())
				.addComponent(jScrollPaneTree, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addContainerGap()
						.addComponent(jComboBoxCallback, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(66, Short.MAX_VALUE)));
		jPanelUserInfoLayout.setVerticalGroup(jPanelUserInfoLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelUserInfoLayout.createSequentialGroup().addGap(18, 18, 18)
						.addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabelIPAddress).addComponent(jTextFieldIPAddress,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(9, 9, 9)
						.addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabelUserName).addComponent(jTextFieldUserName,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(9, 9, 9)
						.addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabelPassWord).addComponent(jPasswordFieldPassword,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(9, 9, 9)
						.addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabelPortNumber).addComponent(jTextFieldPortNumber,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanelUserInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonLogin).addComponent(jButtonRealPlay))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jScrollPaneTree, javax.swing.GroupLayout.PREFERRED_SIZE, 404,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jComboBoxCallback, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10).addComponent(jButtonExit).addContainerGap()));

		jSplitPaneHorizontal.setLeftComponent(jPanelUserInfo);

		jSplitPaneVertical.setDividerLocation(579);
		jSplitPaneVertical.setDividerSize(2);
		jSplitPaneVertical.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

		jPanelRealplayArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 255, 102)));

		panelRealplay.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				panelRealplayMousePressed(evt);
			}
		});

		javax.swing.GroupLayout panelRealplayLayout = new javax.swing.GroupLayout(panelRealplay);
		panelRealplay.setLayout(panelRealplayLayout);
		panelRealplayLayout.setHorizontalGroup(panelRealplayLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 704, Short.MAX_VALUE));
		panelRealplayLayout.setVerticalGroup(panelRealplayLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 576, Short.MAX_VALUE));

		javax.swing.GroupLayout jPanelRealplayAreaLayout = new javax.swing.GroupLayout(jPanelRealplayArea);
		jPanelRealplayArea.setLayout(jPanelRealplayAreaLayout);
		jPanelRealplayAreaLayout.setHorizontalGroup(
				jPanelRealplayAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
						panelRealplay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		jPanelRealplayAreaLayout.setVerticalGroup(jPanelRealplayAreaLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(panelRealplay,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		jSplitPaneVertical.setTopComponent(jPanelRealplayArea);

		jScrollPanelAlarmList.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		jTableAlarm.setModel(this.initialTableModel());
		jScrollPanelAlarmList.setViewportView(jTableAlarm);

		jSplitPaneVertical.setRightComponent(jScrollPanelAlarmList);

		jSplitPaneHorizontal.setRightComponent(jSplitPaneVertical);

		jMenuBarConfig.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

		jMenuConfig.setText("����");

		jMenuItemBasicConfig.setText("������Ϣ");
		jMenuItemBasicConfig.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemBasicConfigMousePressed(evt);
			}
		});
		jMenuConfig.add(jMenuItemBasicConfig);

		jMenuItemNetwork.setText("�������");
		jMenuItemNetwork.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemNetworkMousePressed(evt);
			}
		});
		jMenuConfig.add(jMenuItemNetwork);

		jMenuItemChannel.setText("ͨ������");
		jMenuItemChannel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemChannelMousePressed(evt);
			}
		});
		jMenuConfig.add(jMenuItemChannel);

		jMenuItemAlarmCfg.setText("��������");
		jMenuItemAlarmCfg.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemAlarmCfgMousePressed(evt);
			}
		});
		jMenuConfig.add(jMenuItemAlarmCfg);

		jMenuItemSerialCfg.setText("���ڲ���");
		jMenuItemSerialCfg.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemSerialCfgMousePressed(evt);
			}
		});
		jMenuConfig.add(jMenuItemSerialCfg);

		jMenuItemUserConfig.setText("�û�����");
		jMenuItemUserConfig.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemUserConfigMousePressed(evt);
			}
		});
		jMenuConfig.add(jMenuItemUserConfig);

		jMenuItemIPAccess.setText("IP��������");
		jMenuItemIPAccess.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItemIPAccessActionPerformed(evt);
			}
		});
		jMenuConfig.add(jMenuItemIPAccess);

		jMenuBarConfig.add(jMenuConfig);

		jMenuPlayBack.setText("�ط�");

		jMenuItemPlayBackRemote.setText("���ļ�");
		jMenuItemPlayBackRemote.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemPlayBackRemoteMousePressed(evt);
			}
		});
		jMenuPlayBack.add(jMenuItemPlayBackRemote);

		jMenuItemPlayTime.setText("��ʱ��");
		jMenuItemPlayTime.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemPlayTimeMousePressed(evt);
			}
		});
		jMenuPlayBack.add(jMenuItemPlayTime);

		jMenuBarConfig.add(jMenuPlayBack);

		jMenuSetAlarm.setBorder(null);
		jMenuSetAlarm.setText("����");

		jRadioButtonMenuSetAlarm.setText("������");
		jRadioButtonMenuSetAlarm.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButtonMenuSetAlarmActionPerformed(evt);
			}
		});
		jMenuSetAlarm.add(jRadioButtonMenuSetAlarm);

		jRadioButtonMenuListen.setText("������");
		jRadioButtonMenuListen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButtonMenuListenActionPerformed(evt);
			}
		});
		jMenuSetAlarm.add(jRadioButtonMenuListen);

		jMenuItemRemoveAlarm.setText("��ձ�����Ϣ");
		jMenuItemRemoveAlarm.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemRemoveAlarmMousePressed(evt);
			}
		});
		jMenuSetAlarm.add(jMenuItemRemoveAlarm);

		jMenuBarConfig.add(jMenuSetAlarm);

		jMenuManage.setText("����");

		jMenuItemCheckTime.setText("Уʱ");
		jMenuItemCheckTime.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemCheckTimeMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemCheckTime);

		jMenuItemFormat.setText("��ʽ��");
		jMenuItemFormat.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemFormatMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemFormat);

		jMenuItemUpgrade.setText("����");
		jMenuItemUpgrade.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemUpgradeMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemUpgrade);
		jMenuManage.add(jSeparator1);

		jMenuItemReboot.setText("����");
		jMenuItemReboot.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemRebootMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemReboot);

		jMenuItemShutDown.setText("�ر�");
		jMenuItemShutDown.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemShutDownMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemShutDown);
		jMenuManage.add(jSeparator2);

		jMenuItemDefault.setText("�ָ�Ĭ�ϲ���");
		jMenuItemDefault.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemDefaultMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemDefault);

		jMenuItemDeviceState.setText("�豸״̬");
		jMenuItemDeviceState.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemDeviceStateMousePressed(evt);
			}
		});
		jMenuManage.add(jMenuItemDeviceState);

		jMenuBarConfig.add(jMenuManage);

		jMenuVoice.setText("����");

		jMenuItemVoiceCom.setText("�����Խ�");
		jMenuItemVoiceCom.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jMenuItemVoiceComMousePressed(evt);
			}
		});
		jMenuVoice.add(jMenuItemVoiceCom);

		jMenuBarConfig.add(jMenuVoice);

		setJMenuBar(jMenuBarConfig);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jSplitPaneHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jSplitPaneHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE));
		pack();
	}

	/**
	 * ����: ������ ��������:�½�ClientDemo���岢���ýӿڳ�ʼ��SDK
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				boolean initSuc = hCNetSDK.NET_DVR_Init();
				if (initSuc != true) {
					JOptionPane.showMessageDialog(null, "��ʼ��ʧ��");
				}
				Vision Demo = new Vision();
				centerWindow(Demo);
				Demo.setVisible(true);
			}
		});
	}
}
