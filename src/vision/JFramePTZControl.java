package vision;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import implementation.StitcherItf;
import synthesis.Stnthesis;
import vision.HCNetSDK.NET_DVR_JPEGPARA;

/**
 * �� �� JFramePTZControl ������ ����̨����
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class JFramePTZControl extends javax.swing.JFrame {

	int picNum = 10;

	private static PlayCtrl playControl = PlayCtrl.INSTANCE;
	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private NativeLong m_lRealHandle;// Ԥ�����
	private boolean m_bAutoOn;// �Զ�������̨
	private boolean m_bLightOn;// �����ƹ�
	private boolean m_bWiperOn;// ������ˢ
	private boolean m_bFanOn;// ��������
	private boolean m_bHeaterOn;// ��������
	private boolean m_bAuxOn1;// ��������1
	private boolean m_bAuxOn2;// ��������2
	private boolean m_bIsOnCruise;// �Ƿ���Ѳ��
	private int m_iBrightness;// ����
	private int m_iContrast;// �Աȶ�
	private int m_iSaturation;// ���Ͷ�
	private int m_iHue;// ɫ��
	private int m_iVolume;// ����
	private NativeLong iChannelNum = new NativeLong(-1);
	private NativeLong lUserID;
	private boolean videoFlag;

	// ��ʶ�Ƿ���ƴ�Ӳ���
	private boolean isRun = false;
	private List<String> pathlist = null;
	private NET_DVR_JPEGPARA jpegArea;

	/**
	 * ����: JFramePTZControl ��������: ���캯�� Creates new form JFramePTZControl
	 * 
	 * @param lRealHandle
	 */
	public JFramePTZControl(NativeLong lRealHandle, NativeLong iChannelNum, NativeLong lUserID) {
		this.lUserID = lUserID;
		this.iChannelNum = iChannelNum;
		jpegArea = new NET_DVR_JPEGPARA();
		jpegArea.wPicSize = 0xff;
		jpegArea.wPicQuality = 0;

		m_lRealHandle = lRealHandle;
		m_bAutoOn = false;
		m_bLightOn = false;
		m_bWiperOn = false;
		m_bFanOn = false;
		m_bHeaterOn = false;
		m_bAuxOn1 = false;
		m_bAuxOn2 = false;
		m_bIsOnCruise = false;

		m_iBrightness = 6;
		m_iContrast = 6;
		m_iSaturation = 6;
		m_iHue = 6;
		m_iVolume = 50;

		initComponents();
		int i;
		for (i = 0; i < HCNetSDK.MAX_PRESET_V30; i++) {
			jComboBoxPreset.addItem(i + 1);
		}
		jComboBoxPreset.setSelectedIndex(0);

		// Ѳ���켣
		for (i = 0; i < HCNetSDK.MAX_CRUISE_V30; i++) {
			jComboBoxSeq.addItem(i + 1);
		}
		jComboBoxSeq.setSelectedIndex(0);
	}

	/**
	 * ����¼��
	 * 
	 * @param evt
	 */
	private void jButtonVideoActionPerformed(java.awt.event.ActionEvent evt) {

		if (videoFlag == false) {
			JFrame jf = new JFrame("¼���������");
			jf.setLocationRelativeTo(null);
			jf.setSize(350, 170);
			jf.getContentPane().setLayout(new GridLayout(4, 4));
			jf.add(new JLabel("�Զ�¼��"));
			jf.add(new JLabel("�ֶ�¼��"));
			jf.add(new JLabel("����¼��ʱ��(����)"));

			JButton begin_bt = new JButton("��ʼ¼��");
			JButton end_bt = new JButton("����¼��");
			JTextField videotime = new JTextField(5);
			JButton confirmvideo = new JButton("ȷ��¼��");

			jf.add(begin_bt);
			jf.add(videotime);
			jf.add(end_bt);
			jf.add(confirmvideo);
			end_bt.setEnabled(false);
			confirmvideo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("confirm");

					String name = "../Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
					System.out.println(hCNetSDK.NET_DVR_SaveRealData(lUserID, name));

					videoFlag = true;
					Timer t = new Timer();
					t.schedule(new TimerTask() {

						@Override
						public void run() {
							System.out.println(hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
							videoFlag = false;
						}
					}, Integer.parseInt(videotime.getText()));
					jf.dispose();
					return;
				}
			});
			begin_bt.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String name = "../Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
					System.out.println(hCNetSDK.NET_DVR_SaveRealData(lUserID, name));
					videoFlag = true;
					jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					videotime.setEnabled(false);
					confirmvideo.setEnabled(false);
					begin_bt.setEnabled(false);
					end_bt.setEnabled(true);
				}
			});

			end_bt.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
					videoFlag = false;
					jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					videotime.setEnabled(true);
					confirmvideo.setEnabled(true);
					begin_bt.setEnabled(true);
					end_bt.setEnabled(false);
				}
			});

			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jf.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "����¼��");
		}
	}

	// /**
	// * ���µ���
	// *
	// * @param evt
	// */
	// private void jButtonScreenActionPerformed(java.awt.event.ActionEvent evt)
	// {
	// if (isRun)
	// return;
	// isRun = true;
	// pathlist = new ArrayList<String>();
	// String path = "../Pictures/" + String.valueOf(System.currentTimeMillis())
	// + "/";
	// File file = new File(path);
	// if (file.mkdirs()) {
	// // ¼��
	// String name = "../Videos/" + String.valueOf(System.currentTimeMillis()) +
	// ".mp4";
	// System.out.println("��ʼ¼��" + hCNetSDK.NET_DVR_SaveRealData(lUserID,
	// name));
	// // ��ת������ץͼ��ץ��ǰ���ǵ�
	// Timer t = new Timer();
	// t.schedule(new TimerTask() {
	// int n = 0;
	// int dir = 0;
	//
	// @Override
	// public void run() {
	// if (n++ == 20) {
	// // ֹͣ��ת
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1,
	// 2);
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_RIGHT,
	// 1, 2);
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_UP, 1,
	// 2);
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_DOWN,
	// 1, 2);
	// // ֹͣ¼��
	// System.out.println("ֹͣ¼�� " +
	// hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
	// // ��ʼƴͼ
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// Stnthesis.process(pathlist.toArray(new String[pathlist.size()]));
	// isRun = false;
	// }
	// }).start();
	// // ֹͣ��ʱ
	// t.cancel();
	// } else {
	// if (dir == 0) {// ����
	// // ֹͣ����
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1,
	// 2);
	// // ץͼ
	// String name = path + String.valueOf(System.currentTimeMillis()) +
	// ".jpeg";
	// System.out.println("ץͼ " + n + " :"
	// + hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea,
	// name));
	// pathlist.add(name);
	// // ����ת
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_UP, 0,
	// 2);
	// } else if (dir == 1) {// ����
	// // ֹͣ����
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_UP, 1,
	// 2);
	// // ץͼ
	// String name = path + String.valueOf(System.currentTimeMillis()) +
	// ".jpeg";
	// System.out.println("ץͼ " + n + " :"
	// + hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea,
	// name));
	// pathlist.add(name);
	// // ����ת
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0,
	// 2);
	// } else if (dir == 2) {// ����
	// // ֹͣ����
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1,
	// 2);
	// // ץͼ
	// String name = path + String.valueOf(System.currentTimeMillis()) +
	// ".jpeg";
	// System.out.println("ץͼ " + n + " :"
	// + hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea,
	// name));
	// pathlist.add(name);
	// // ����ת
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_DOWN,
	// 0, 2);
	// } else if (dir == 3) {// ����
	// // ֹͣ����
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_DOWN,
	// 1, 2);
	// // ����ת
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0,
	// 2);
	// }
	// dir++;
	// dir %= 4;
	// }
	// }
	// }, 0, 3000);
	// } else {
	// System.out.println("����·��ʧ��");
	// }
	// }

	/**
	 *
	 * ����ץͼ��ƴͼ�������򣬱��浽�ڴ�
	 *
	 * @param evt
	 */
	private void jButtonScreenToMemActionPerformed(java.awt.event.ActionEvent evt) {
		if (isRun)
			return;
		System.out.println("����ץͼ�����浽�ڴ�");
		isRun = true;
		List<byte[]> list = new ArrayList<byte[]>();
		String name = "Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";

		// ��ʼ¼��
		System.out.println("��ʼ¼��" + hCNetSDK.NET_DVR_SaveRealData(lUserID, name));
		// ��ʼ��ת
		hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0, 2);

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			int n = 0;

			public void run() {
				if (n++ == picNum) {
					// ֹͣת��
					hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1, 2);
					// ֹͣ¼��
					System.out.println("ֹͣ¼�� " + hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
					// ��ʼƴͼ
					new Thread(new Runnable() {
						public void run() {
							Stnthesis.process(list);
							isRun = false;
						}
					}).start();
					t.cancel();
				} else {
					byte[] buff = new byte[1048576];
					IntByReference ibr = new IntByReference();
					System.out.println("��ͼ �� " + n + hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(lUserID, iChannelNum,
							jpegArea, buff, 1048576, ibr));
					list.add(buff);
				}
			}
		}, 0, 2500);
	}

	/**
	 *
	 * ����ץͼ��ƴͼ�������򣬱��浽�ļ�
	 *
	 * @param evt
	 */
	private void jButtonScreenToFileActionPerformed(java.awt.event.ActionEvent evt) {
		if (isRun)
			return;
		System.out.println("����ץͼ�����浽�ļ�");
		isRun = true;
		pathlist = new ArrayList<String>();
		String path = "Pictures/" + String.valueOf(System.currentTimeMillis()) + "/";
		File file = new File(path);
		if (file.mkdirs()) { // ¼��
			String name = "Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
			System.out.println("��ʼ¼��" + hCNetSDK.NET_DVR_SaveRealData(lUserID, name)); // ��ʼ��ת
			hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0, 2); // ��ת������ץͼ��ץ��ǰ���ǵ�
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				int n = 0;

				public void run() {
					if (n++ == picNum) {
						hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1, 2);

						System.out.println("ֹͣ¼�� " + hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
						new Thread(new Runnable() {
							public void run() {
								Stnthesis.process(pathlist.toArray(new String[pathlist.size()]));
								isRun = false;
							}
						}).start();
						t.cancel(); // Run2(path);
					} else {
						String name = path + String.valueOf(System.currentTimeMillis()) + ".jpeg";
						System.out.println("ץͼ " + n + " :"
								+ hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea, name));
						pathlist.add(name);
					}
				}
			}, 0, 2500);
		} else {
			System.out.println("����·��ʧ��");
		}
	}

	/*****************************************************************************
	 * ����Ԥ�õ㣬���浽�ڴ�
	 * 
	 *****************************************************************************/
	public void enscanAllViewToMem() {
		if (isRun)
			return;
		int num = picNum;
		System.out.println("����Ԥ�õ�ץͼ�����浽�ڴ�");
		isRun = true;
		List<byte[]> list = new ArrayList<byte[]>();
		String name = "Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
		// ��ʼ¼��
		// System.out.println("��ʼ¼��" + hCNetSDK.NET_DVR_SaveRealData(lUserID,
		// name));

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "���Ԥ�õ�ʧ��");
			return;
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}
		for (int i = 1; i <= num; i++) {
			byte[] buff = new byte[1048576];
			IntByReference ibr = new IntByReference();
			boolean cResult = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(lUserID, iChannelNum, jpegArea, buff, 1048576,
					ibr);
			System.out.println("��ͼ �� " + i + " : " + cResult);
			if (!cResult) {
				System.out.println("ץͼʧ�ܣ��޷�������ǿȫ��ɨ��");
				return;
			}
			list.add(buff);
			Rotate(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1000, 4);
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}
		// �ȴ�����ص�Ԥ�õ�
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Rotate(m_lRealHandle, HCNetSDK.TILT_DOWN, 1000, 3);
		for (int i = 1; i <= num; i++) {
			byte[] buff = new byte[1048576];
			IntByReference ibr = new IntByReference();
			boolean cResult = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(lUserID, iChannelNum, jpegArea, buff, 1048576,
					ibr);
			System.out.println("��ͼ �� " + i + " : " + cResult);
			if (!cResult) {
				System.out.println("ץͼʧ�ܣ��޷�������ǿȫ��ɨ��");
				return;
			}
			list.add(buff);
			Rotate(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1000, 4);
		}
		Stnthesis.process(list);
		isRun = false;
	}

	/*****************************************************************************/

	/*************************************************************************
	 * Ԥ�õ�ɨ�裬���浽�ļ�
	 * 
	 * @param num
	 */
	public void enscanAllView() {
		if (isRun)
			return;
		int num = picNum;
		System.out.println("����Ԥ�õ�ץͼ�����浽�ļ�");
		isRun = true;
		String enscanAllViewdir_path = "Pictures\\" + System.currentTimeMillis() + "\\";
		File enscanAllViewdir_pathfile = new File(enscanAllViewdir_path);
		if (!enscanAllViewdir_pathfile.exists())
			enscanAllViewdir_pathfile.mkdirs();

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "���Ԥ�õ�ʧ��");
			return;
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}

		for (int i = 1; i <= num; i++) {
			if (CapturePicture(enscanAllViewdir_path + "up\\", String.valueOf(i)) == false) {
				System.out.println("ץͼʧ�ܣ��޷�������ǿȫ��ɨ��");
				return;
			}
			Rotate(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1000, 4);
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}
		// �ȴ�����ص�Ԥ�õ�
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Rotate(m_lRealHandle, HCNetSDK.TILT_DOWN, 1000, 3);

		for (int i = 1; i <= num; i++) {
			if (CapturePicture(enscanAllViewdir_path + "down\\", String.valueOf(i)) == false) {
				System.out.println("ץͼʧ�ܣ��޷�������ǿȫ��ɨ��");
				return;
			}
			Rotate(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1000, 4);
		}

		String[] stitfile_path = new String[num * 2];
		for (int i = 1; i <= num; i++) {
			stitfile_path[i * 2 - 2] = enscanAllViewdir_path + "up\\" + i + ".jpg";
			stitfile_path[i * 2 - 1] = enscanAllViewdir_path + "down\\" + i + ".jpg";
			System.out.println(stitfile_path[i * 2 - 2] + "------" + stitfile_path[i * 2 - 1]);
		}

		Stnthesis.process(stitfile_path);
		isRun = false;
	}

	public boolean CapturePicture(String path, String filename) {
		File path_file = new File(path);
		if (!path_file.exists())
			path_file.mkdirs();

		NET_DVR_JPEGPARA jpegArea = new NET_DVR_JPEGPARA();
		jpegArea.wPicSize = 0xff;
		jpegArea.wPicQuality = 0;

		String sPicName = path + filename + ".jpg";
		if (new File(sPicName).exists()) {
			System.out.println("ץͼʧ��,ͼƬ����");
			return false;
		}

		if (hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, new NativeLong(1), jpegArea, sPicName)) {
			System.out.println("ץͼ:" + sPicName);
		} else {
			System.out.println("ץͼʧ��");
			return false;
		}

		return true;
	}

	/***********************************************************************************/

	/**
	 * ������ת
	 * 
	 * @param lRealHandle
	 * @param iPTZCommand
	 * @param time_ms
	 */
	public void Rotate(NativeLong lRealHandle, int iPTZCommand, long time_ms) {
		PTZControlAll(lRealHandle, HCNetSDK.PAN_LEFT, 0);

		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				PTZControlAll(lRealHandle, iPTZCommand, 1);
			}
		}, time_ms);
	}

	public void Rotate(NativeLong lRealHandle, int iPTZCommand, long time_ms, int ispeed) {
		hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, 0, ispeed);
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				PTZControlAll(lRealHandle, iPTZCommand, 1);
			}
		}, time_ms);
		try {
			Thread.sleep(time_ms + 1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void format(String path) {
		File file = new File(path);
		File[] filelist = file.listFiles();
		byte[][] array = new byte[filelist.length][];
		for (int i = 0; i < filelist.length; i++) {
			Mat grayMat = Highgui.imread(filelist[i].getAbsolutePath());
			array[i] = new byte[grayMat.rows() * grayMat.cols() * (int) (grayMat.elemSize())];
			grayMat.get(0, 0, array[i]);
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void initComponents() {
		jButtonScreenshot = new javax.swing.JButton();
		jButtonScreenshot2 = new javax.swing.JButton();
		jButtonScreenshot3 = new javax.swing.JButton();
		jButtonScreenshot4 = new javax.swing.JButton();
		jButtonVideo = new javax.swing.JButton();
		jPanelPTZ = new javax.swing.JPanel();
		jButtonAux2 = new javax.swing.JButton();
		jButtonAux1 = new javax.swing.JButton();
		jButton1IrisClose = new javax.swing.JButton();
		jButtonFocusFar = new javax.swing.JButton();
		jButton1ZoomOut = new javax.swing.JButton();
		jButtonRight = new javax.swing.JButton();
		jButtonRightDown = new javax.swing.JButton();
		jButtonRightUp = new javax.swing.JButton();
		jButtonUp = new javax.swing.JButton();
		jButtonAuto = new javax.swing.JButton();
		jButtondown = new javax.swing.JButton();
		jButtonLeftUp = new javax.swing.JButton();
		jButtonLeft = new javax.swing.JButton();
		jButtonLeftDown = new javax.swing.JButton();
		jButtonZoomIn = new javax.swing.JButton();
		jButton1FocusNear = new javax.swing.JButton();
		jButton1IrisOpen = new javax.swing.JButton();
		jButtonLight = new javax.swing.JButton();
		jButtonFanPwron = new javax.swing.JButton();
		jButtonHeater = new javax.swing.JButton();
		jButtonWiperPwron = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jComboBoxSpeed = new javax.swing.JComboBox();
		jPanel1 = new javax.swing.JPanel();
		jComboBoxPreset = new javax.swing.JComboBox();
		jButtonGotoPreset = new javax.swing.JButton();
		jButtonSetPreset = new javax.swing.JButton();
		jButtonDeletePreset = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		jComboBoxSeq = new javax.swing.JComboBox();
		jButtonGotoSeq = new javax.swing.JButton();
		jButtonSetSeq = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		jButtonTrackStop = new javax.swing.JButton();
		jButtonTrackRun = new javax.swing.JButton();
		jButtonTrackStart = new javax.swing.JButton();
		jPanelVideoPara = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jButtonDefault = new javax.swing.JButton();
		jSliderVolume = new javax.swing.JSlider();
		jSliderHue = new javax.swing.JSlider();
		jSliderSaturation = new javax.swing.JSlider();
		jSliderContrast = new javax.swing.JSlider();
		jSliderBright = new javax.swing.JSlider();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("��̨����");

		jPanelPTZ.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		jButtonAux2.setText("����2");
		jButtonAux2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonAux2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAux2ActionPerformed(evt);
			}
		});

		jButtonAux1.setText("����");
		jButtonAux1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonAux1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAux1ActionPerformed(evt);
			}
		});

		jButton1IrisClose.setText("С");
		jButton1IrisClose.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButton1IrisClose.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButton1IrisCloseMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButton1IrisCloseMouseReleased(evt);
			}
		});

		jButtonFocusFar.setText("Զ");
		jButtonFocusFar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonFocusFar.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonFocusFarMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonFocusFarMouseReleased(evt);
			}
		});

		jButton1ZoomOut.setText("��");
		jButton1ZoomOut.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButton1ZoomOut.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButton1ZoomOutMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButton1ZoomOutMouseReleased(evt);
			}
		});

		jButtonRight.setText("��");
		jButtonRight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonRight.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonRightMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonRightMouseReleased(evt);
			}
		});

		jButtonRightDown.setText("����");
		jButtonRightDown.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonRightDown.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonRightDownMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonRightDownMouseReleased(evt);
			}
		});

		jButtonRightUp.setText("����");
		jButtonRightUp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonRightUp.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonRightUpMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonRightUpMouseReleased(evt);
			}
		});

		jButtonUp.setText("��");
		jButtonUp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonUp.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonUpMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonUpMouseReleased(evt);
			}
		});

		jButtonAuto.setText("�Զ�");
		jButtonAuto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonAuto.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAutoActionPerformed(evt);
			}
		});

		jButtondown.setText("��");
		jButtondown.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtondown.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtondownMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtondownMouseReleased(evt);
			}
		});

		jButtonLeftUp.setText("����");
		jButtonLeftUp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonLeftUp.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonLeftUpMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonLeftUpMouseReleased(evt);
			}
		});

		jButtonLeft.setText("��");
		jButtonLeft.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonLeft.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonLeftMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonLeftMouseReleased(evt);
			}
		});

		jButtonLeftDown.setText("����");
		jButtonLeftDown.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonLeftDown.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonLeftDownMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonLeftDownMouseReleased(evt);
			}
		});

		jButtonZoomIn.setText("��");
		jButtonZoomIn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonZoomIn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButtonZoomInMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButtonZoomInMouseReleased(evt);
			}
		});

		jButton1FocusNear.setText("��");
		jButton1FocusNear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButton1FocusNear.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButton1FocusNearMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButton1FocusNearMouseReleased(evt);
			}
		});

		jButton1IrisOpen.setText("��");
		jButton1IrisOpen.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButton1IrisOpen.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				jButton1IrisOpenMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jButton1IrisOpenMouseReleased(evt);
			}
		});

		jButtonLight.setText("�ƹ�");
		jButtonLight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonLight.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonLightActionPerformed(evt);
			}
		});

		jButtonFanPwron.setText("����");
		jButtonFanPwron.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonFanPwron.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonFanPwronActionPerformed(evt);
			}
		});

		jButtonHeater.setText("����");
		jButtonHeater.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonHeater.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonHeaterActionPerformed(evt);
			}
		});

		jButtonWiperPwron.setText("��ˢ");
		jButtonWiperPwron.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonWiperPwron.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonWiperPwronActionPerformed(evt);
			}
		});

		jLabel1.setText("��Ȧ");

		jLabel3.setText("�۽�");

		jLabel2.setText("����");

		jLabel4.setText("��̨�ٶ�");

		jComboBoxSpeed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ĭ��", "1", "2", "3", "4", "5" }));

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Ԥ�õ�"));

		jButtonGotoPreset.setText("����");
		jButtonGotoPreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonGotoPreset.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonGotoPresetActionPerformed(evt);
			}
		});

		jButtonSetPreset.setText("����");
		jButtonSetPreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonSetPreset.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetPresetActionPerformed(evt);
			}
		});

		jButtonDeletePreset.setText("ɾ��");
		jButtonDeletePreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonDeletePreset.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonDeletePresetActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel1Layout.createSequentialGroup()
								.addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 48,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jButtonGotoPreset, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jButtonSetPreset)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jButtonDeletePreset)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jComboBoxPreset, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonGotoPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonSetPreset, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonDeletePreset, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ѳ��·��"));

		jButtonGotoSeq.setText("����");
		jButtonGotoSeq.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonGotoSeq.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonGotoSeqActionPerformed(evt);
			}
		});

		jButtonSetSeq.setText("����");
		jButtonSetSeq.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonSetSeq.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetSeqActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel2Layout.createSequentialGroup()
								.addComponent(jComboBoxSeq, javax.swing.GroupLayout.PREFERRED_SIZE, 48,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jButtonGotoSeq, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jButtonSetSeq, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jComboBoxSeq, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonSetSeq, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonGotoSeq, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("�����¼"));

		jButtonTrackStop.setText("ֹͣ");
		jButtonTrackStop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonTrackStop.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonTrackStopActionPerformed(evt);
			}
		});

		jButtonTrackRun.setText("����");
		jButtonTrackRun.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonTrackRun.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonTrackRunActionPerformed(evt);
			}
		});

		jButtonTrackStart.setText("��ʼ");
		jButtonTrackStart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonTrackStart.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonTrackStartActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap(11, Short.MAX_VALUE)
						.addComponent(jButtonTrackStart, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jButtonTrackStop, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonTrackRun, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(4, 4, 4)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonTrackStop, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonTrackStart, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonTrackRun, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		javax.swing.GroupLayout jPanelPTZLayout = new javax.swing.GroupLayout(jPanelPTZ);
		jPanelPTZ.setLayout(jPanelPTZLayout);
		jPanelPTZLayout.setHorizontalGroup(jPanelPTZLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelPTZLayout.createSequentialGroup().addContainerGap().addGroup(jPanelPTZLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButtonLeftUp, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(jButtonUp, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButtonRightUp, javax.swing.GroupLayout.PREFERRED_SIZE,
										40, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButtonLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(jButtonAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButtonRight, javax.swing.GroupLayout.PREFERRED_SIZE,
										40, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButtonLeftDown, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(jButtondown, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButtonRightDown,
										javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButtonZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(20, 20, 20)
								.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButton1ZoomOut,
										javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButton1FocusNear, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(20, 20, 20)
								.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButtonFocusFar,
										javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButton1IrisOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(20, 20, 20)
								.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButton1IrisClose,
										javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButtonLight, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(jButtonWiperPwron, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButtonAux1, javax.swing.GroupLayout.PREFERRED_SIZE,
										40, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup()
								.addComponent(jButtonFanPwron, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(jButtonHeater, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10).addComponent(jButtonAux2, javax.swing.GroupLayout.PREFERRED_SIZE,
										40, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createSequentialGroup().addComponent(jLabel4).addGap(18, 18, 18)
								.addComponent(jComboBoxSpeed, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
				.addGroup(jPanelPTZLayout.createSequentialGroup()
						.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				.addGroup(jPanelPTZLayout.createSequentialGroup().addComponent(jPanel2, 0, 167, Short.MAX_VALUE)
						.addContainerGap())
				.addGroup(jPanelPTZLayout.createSequentialGroup()
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		jPanelPTZLayout.setVerticalGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelPTZLayout.createSequentialGroup()
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButtonLeftUp, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonUp, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonRightUp, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButtonLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonAuto, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonRight, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButtonLeftDown, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtondown, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonRightDown, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButtonZoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2).addComponent(jButton1ZoomOut,
										javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButton1FocusNear, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel3).addComponent(jButtonFocusFar,
										javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButton1IrisOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel1).addComponent(jButton1IrisClose,
										javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(10, 10, 10)
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButtonLight, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonWiperPwron, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonAux1, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButtonFanPwron, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonHeater, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonAux2, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanelPTZLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jComboBoxSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 18,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel4))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel3,
								javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)));

		jPanelVideoPara.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
		jPanelVideoPara.setLayout(null);

		jLabel5.setText("�Աȶ�");
		jPanelVideoPara.add(jLabel5);
		jLabel5.setBounds(10, 50, 36, 15);

		jLabel6.setText("���Ͷ�");
		jPanelVideoPara.add(jLabel6);
		jLabel6.setBounds(10, 80, 36, 15);

		jLabel7.setText("ɫ��");
		jPanelVideoPara.add(jLabel7);
		jLabel7.setBounds(10, 110, 24, 15);

		// jLabel8.setText("����");
		// jPanelVideoPara.add(jLabel8);
		// jLabel8.setBounds(10, 140, 24, 15);

		jLabel9.setText("����");
		jPanelVideoPara.add(jLabel9);
		jLabel9.setBounds(10, 20, 24, 15);

		jButtonDefault.setText("Ĭ��ֵ");
		jButtonDefault.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonDefaultActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonDefault);
		jButtonDefault.setBounds(20, 140, 70, 23);

		jButtonVideo.setText("¼��");
		jButtonVideo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonVideoActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonVideo);
		jButtonVideo.setBounds(100, 140, 70, 23);

		// ץͼ��ʽ1������ͼƬ���ļ�
		jButtonScreenshot.setText("ץͼ1");
		jButtonScreenshot.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonScreenToFileActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonScreenshot);
		jButtonScreenshot.setBounds(20, 170, 70, 23);

		// ץͼ��ʽ2��������ͼƬ
		jButtonScreenshot2.setText("ץͼ2");
		jButtonScreenshot2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonScreenToMemActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonScreenshot2);
		jButtonScreenshot2.setBounds(100, 170, 70, 23);

		// ץͼ��ʽ3������ͼƬ���ļ�
		jButtonScreenshot3.setText("ץͼ3");
		jButtonScreenshot3.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enscanAllView();
			}
		});
		jPanelVideoPara.add(jButtonScreenshot3);
		jButtonScreenshot3.setBounds(20, 200, 70, 23);

		// ץͼ��ʽ4��������ͼƬ
		jButtonScreenshot4.setText("ץͼ4");
		jButtonScreenshot4.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enscanAllViewToMem();
			}
		});
		jPanelVideoPara.add(jButtonScreenshot4);
		jButtonScreenshot4.setBounds(100, 200, 70, 23);

		// jSliderVolume.setMinimum(1);
		// jSliderVolume.addChangeListener(new
		// javax.swing.event.ChangeListener() {
		// @Override
		// public void stateChanged(javax.swing.event.ChangeEvent evt) {
		// jSliderVolumeStateChanged(evt);
		// }
		// });
		// jPanelVideoPara.add(jSliderVolume);
		// jSliderVolume.setBounds(60, 140, 110, 20);

		jSliderHue.setMaximum(10);
		jSliderHue.setMinimum(1);
		jSliderHue.setValue(6);
		jSliderHue.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				jSliderHueStateChanged(evt);
			}
		});
		jPanelVideoPara.add(jSliderHue);
		jSliderHue.setBounds(60, 110, 110, 20);

		jSliderSaturation.setMaximum(10);
		jSliderSaturation.setMinimum(1);
		jSliderSaturation.setValue(6);
		jSliderSaturation.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				jSliderSaturationStateChanged(evt);
			}
		});
		jPanelVideoPara.add(jSliderSaturation);
		jSliderSaturation.setBounds(60, 80, 110, 20);

		jSliderContrast.setMaximum(10);
		jSliderContrast.setMinimum(1);
		jSliderContrast.setValue(6);
		jSliderContrast.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				jSliderContrastStateChanged(evt);
			}
		});
		jPanelVideoPara.add(jSliderContrast);
		jSliderContrast.setBounds(60, 50, 110, 20);

		jSliderBright.setMaximum(10);
		jSliderBright.setMinimum(1);
		jSliderBright.setValue(6);
		jSliderBright.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				jSliderBrightStateChanged(evt);
			}
		});
		jPanelVideoPara.add(jSliderBright);
		jSliderBright.setBounds(60, 20, 110, 20);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jPanelPTZ, javax.swing.GroupLayout.PREFERRED_SIZE, 181,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jPanelVideoPara, javax.swing.GroupLayout.PREFERRED_SIZE, 180,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap()
						.addComponent(jPanelPTZ, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGap(18, 18, 18).addComponent(jPanelVideoPara, javax.swing.GroupLayout.PREFERRED_SIZE, 232,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));

		pack();
	}

	/**
	 * ����: ���� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonLeftUpMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.UP_LEFT, 0);
	}

	private void jButtonLeftUpMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.UP_LEFT, 1);
	}

	/**
	 * ����: ���� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonRightDownMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.DOWN_RIGHT, 0);
	}

	private void jButtonRightDownMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.DOWN_RIGHT, 1);
	}

	/**
	 * ����: �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonUpMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.TILT_UP, 0);
	}

	private void jButtonUpMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.TILT_UP, 1);
	}

	/**
	 * ����: �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtondownMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.TILT_DOWN, 0);
	}

	private void jButtondownMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.TILT_DOWN, 1);
	}

	/**
	 * ����: ���� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonRightUpMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.TILT_DOWN, 0);
	}

	private void jButtonRightUpMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.TILT_DOWN, 1);
	}

	/**
	 * ����: ���� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonLeftDownMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.DOWN_LEFT, 0);
	}

	private void jButtonLeftDownMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.DOWN_LEFT, 1);
	}

	/**
	 * ����: �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonLeftMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.PAN_LEFT, 0);
	}

	private void jButtonLeftMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.PAN_LEFT, 1);
	}

	/**
	 * ����: �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonRightMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.PAN_RIGHT, 0);
	}

	private void jButtonRightMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1);
	}

	/**
	 * ����: ���� �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonZoomInMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.ZOOM_IN, 0);
	}

	private void jButtonZoomInMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.ZOOM_IN, 1);
	}

	/**
	 * ����: ���� �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButton1ZoomOutMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.ZOOM_OUT, 0);
	}

	private void jButton1ZoomOutMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.ZOOM_OUT, 1);
	}

	/**
	 * ����: �۽� �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButton1FocusNearMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.FOCUS_NEAR, 0);
	}

	private void jButton1FocusNearMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.FOCUS_NEAR, 1);
	}

	/**
	 * ����: �۽� Զ ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButtonFocusFarMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.FOCUS_FAR, 0);
	}

	private void jButtonFocusFarMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.FOCUS_FAR, 1);
	}

	/**
	 * ����: ��Ȧ �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButton1IrisOpenMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.IRIS_OPEN, 0);
	}

	private void jButton1IrisOpenMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.IRIS_OPEN, 1);
	}

	/**
	 * ����: ��Ȧ �� ��ť��press��release��Ӧ���� ��������: ��̨���ƺ���
	 * 
	 * @param evt
	 */
	private void jButton1IrisCloseMousePressed(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.IRIS_CLOSE, 0);
	}

	private void jButton1IrisCloseMouseReleased(java.awt.event.MouseEvent evt) {
		PTZControlAll(m_lRealHandle, HCNetSDK.IRIS_CLOSE, 1);
	}

	/**
	 * ����: "�Զ�"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��̨��ʼ/ֹͣ�����Զ�ɨ��
	 * 
	 * @param evt
	 */
	private void jButtonAutoActionPerformed(java.awt.event.ActionEvent evt) {
		int iSpeed = jComboBoxSpeed.getSelectedIndex();
		if (!m_bAutoOn) {
			if (iSpeed >= 1) {
				hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_AUTO, 0, iSpeed);
			} else {
				hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.PAN_AUTO, 0);
			}
			jButtonAuto.setText("ֹͣ");
			m_bAutoOn = true;
		} else {
			if (iSpeed >= 1) {
				hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_AUTO, 1, iSpeed);
			} else {
				hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.PAN_AUTO, 1);
			}
			jButtonAuto.setText("�Զ�");
			m_bAutoOn = false;
		}
	}

	/**
	 * ����: "�ƹ�"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��/�رյƹ�
	 * 
	 * @param evt
	 */
	private void jButtonLightActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bLightOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.LIGHT_PWRON, 0);
			jButtonLight.setText("�ص�");
			m_bLightOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.LIGHT_PWRON, 1);
			jButtonLight.setText("�ƹ�");
			m_bLightOn = false;
		}
	}

	/**
	 * ����: "��ˢ"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��ʼ/ֹͣ��ˢ
	 * 
	 * @param evt
	 */
	private void jButtonWiperPwronActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bWiperOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.WIPER_PWRON, 0);
			jButtonWiperPwron.setText("��ͣ");
			m_bWiperOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.WIPER_PWRON, 1);
			jButtonWiperPwron.setText("��ˢ");
			m_bWiperOn = false;
		}
	}

	/**
	 * ����: "����"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��ʼ/ֹͣ ����
	 * 
	 * @param evt
	 */
	private void jButtonFanPwronActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bFanOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.FAN_PWRON, 0);
			jButtonFanPwron.setText("ͣ��");
			m_bFanOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.FAN_PWRON, 1);
			jButtonFanPwron.setText("����");
			m_bFanOn = false;
		}
	}

	/**
	 * ����: "����"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��ʼ/ֹͣ ����
	 * 
	 * @param evt
	 */
	private void jButtonHeaterActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bHeaterOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.HEATER_PWRON, 0);
			jButtonHeater.setText("ֹͣ");
			m_bHeaterOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.HEATER_PWRON, 1);
			jButtonHeater.setText("����");
			m_bHeaterOn = false;
		}
	}

	/**
	 * ����: "����1"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��ʼ/ֹͣ ����1
	 * 
	 * @param evt
	 */
	private void jButtonAux1ActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bAuxOn1) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 0);
			jButtonAux1.setText("ֹͣ1");
			m_bAuxOn1 = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 1);
			jButtonAux1.setText("����1");
			m_bAuxOn1 = false;
		}
	}

	/**
	 * ����: "����2"��ť ˫����Ӧ���� ��������: ��̨���ƺ��� ��ʼ/ֹͣ ����2
	 * 
	 * @param evt
	 */
	private void jButtonAux2ActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bAuxOn2) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 0);
			jButtonAux2.setText("ֹͣ2");
			m_bAuxOn2 = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 1);
			jButtonAux2.setText("����2");
			m_bAuxOn2 = false;
		}
	}

	/**
	 * ����: �����¼ "��ʼ"��ť ˫����Ӧ���� ��������: ��ʼ�����¼
	 * 
	 * @param evt
	 */
	private void jButtonTrackStartActionPerformed(java.awt.event.ActionEvent evt) {
		if (!hCNetSDK.NET_DVR_PTZTrack(m_lRealHandle, HCNetSDK.STA_MEM_CRUISE)) {
			JOptionPane.showMessageDialog(this, "��ʼ��¼�켣ʧ��");
			return;
		}
	}

	/**
	 * ����: �����¼ "ֹͣ"��ť ˫����Ӧ���� ��������: ֹͣ�����¼
	 * 
	 * @param evt
	 */
	private void jButtonTrackStopActionPerformed(java.awt.event.ActionEvent evt) {
		if (!hCNetSDK.NET_DVR_PTZTrack(m_lRealHandle, HCNetSDK.STO_MEM_CRUISE)) {
			JOptionPane.showMessageDialog(this, "ֹͣ��¼���ʧ��");
			return;
		}
	}

	/**
	 * ����: �����¼ "����"��ť ˫����Ӧ���� ��������: ���й����¼
	 * 
	 * @param evt
	 */
	private void jButtonTrackRunActionPerformed(java.awt.event.ActionEvent evt) {
		if (!hCNetSDK.NET_DVR_PTZTrack(m_lRealHandle, HCNetSDK.RUN_CRUISE)) {
			JOptionPane.showMessageDialog(this, "���й켣ʧ��");
			return;
		}
	}

	/**
	 * ����: Ԥ�õ� "����"��ť ˫����Ӧ���� ��������: ����Ԥ�õ�
	 * 
	 * @param evt
	 */
	private void jButtonGotoPresetActionPerformed(java.awt.event.ActionEvent evt) {
		int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, iPreset)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}
	}

	/**
	 * ����: Ԥ�õ� "����"��ť ˫����Ӧ���� ��������: ����Ԥ�õ�
	 * 
	 * @param evt
	 */
	private void jButtonSetPresetActionPerformed(java.awt.event.ActionEvent evt) {
		int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, iPreset)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}
	}

	/**
	 * ����: Ԥ�õ� "ɾ��"��ť ˫����Ӧ���� ��������: ɾ��Ԥ�õ�
	 * 
	 * @param evt
	 */
	private void jButtonDeletePresetActionPerformed(java.awt.event.ActionEvent evt) {
		int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, iPreset)) {
			JOptionPane.showMessageDialog(this, "����Ԥ�õ�ʧ��");
			return;
		}
	}

	/**
	 * ����: Ѳ��·�� "����"��ť ˫����Ӧ���� ��������: ����Ѳ��·��
	 * 
	 * @param evt
	 */
	private void jButtonGotoSeqActionPerformed(java.awt.event.ActionEvent evt) {
		byte iSeq = (byte) (jComboBoxSeq.getSelectedIndex() + 1);
		if (!m_bIsOnCruise) {
			if (!hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.RUN_SEQ, iSeq, (byte) 0, (short) 0)) {
				JOptionPane.showMessageDialog(this, "����Ѳ��ʧ��");
				return;
			}
		}
	}

	/**
	 * ����: Ѳ��·�� "����"��ť ˫����Ӧ���� ��������: ����Ѳ��·��
	 * 
	 * @param evt
	 */
	private void jButtonSetSeqActionPerformed(java.awt.event.ActionEvent evt) {
		JDialogPTZCruise dlgPTZCruise = new JDialogPTZCruise(this, true, m_lRealHandle);
		dlgPTZCruise.setBounds(this.getX(), this.getY(), 350, 270);
		dlgPTZCruise.setVisible(true);
	}

	private void jSliderBrightStateChanged(javax.swing.event.ChangeEvent evt) {
		m_iBrightness = jSliderBright.getValue();
		setVideoEffect();
	}

	private void jSliderContrastStateChanged(javax.swing.event.ChangeEvent evt) {
		m_iContrast = jSliderContrast.getValue();
		setVideoEffect();
	}

	private void jSliderSaturationStateChanged(javax.swing.event.ChangeEvent evt) {
		m_iSaturation = jSliderSaturation.getValue();
		setVideoEffect();
	}

	private void jSliderHueStateChanged(javax.swing.event.ChangeEvent evt) {
		m_iHue = jSliderHue.getValue();
		setVideoEffect();
	}

	private void jSliderVolumeStateChanged(javax.swing.event.ChangeEvent evt) {
		m_iVolume = jSliderVolume.getValue();

		if (Vision.g_lVoiceHandle.intValue() >= 0) {
			hCNetSDK.NET_DVR_SetVoiceComClientVolume(Vision.g_lVoiceHandle, (short) (m_iVolume * ((0xffff) / 100)));
		}
	}

	private void jButtonDefaultActionPerformed(java.awt.event.ActionEvent evt) {
		m_iBrightness = 6;
		m_iContrast = 6;
		m_iSaturation = 6;
		m_iHue = 6;
		m_iVolume = 50;

		jSliderBright.setValue(6);
		jSliderContrast.setValue(6);
		jSliderSaturation.setValue(6);
		jSliderHue.setValue(6);
		jSliderVolume.setValue(50);

		setVideoEffect();
	}

	private boolean setVideoEffect() {
		if (!hCNetSDK.NET_DVR_ClientSetVideoEffect(m_lRealHandle, m_iBrightness, m_iContrast, m_iSaturation, m_iHue)) {
			JOptionPane.showMessageDialog(this, "����Ԥ����Ƶ��ʾ����ʧ��");
			return false;
		} else {
			return true;
		}
	}

	private void MyPTZControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop, int iSpeed) {
		if (lRealHandle.intValue() >= 0) {
			boolean ret;
			if (iSpeed >= 1) {// ���ٶȵ�ptz
				ret = hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, iSpeed);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "��̨����ʧ��");
					return;
				}
			} else {// �ٶ�ΪĬ��ʱ
				ret = hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "��̨����ʧ��");
					return;
				}
			}
		}
	}

	/**
	 * ������: PTZControlAll ��������: ��̨���ƺ��� �������: lRealHandle: Ԥ����� iPTZCommand:
	 * PTZ�������� iStop: ��ʼ����ֹͣ���� �������: ����ֵ:
	 * 
	 * @param lRealHandle
	 * @param iPTZCommand
	 * @param iStop
	 */
	private void PTZControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop) {
		int iSpeed = jComboBoxSpeed.getSelectedIndex();
		if (lRealHandle.intValue() >= 0) {
			boolean ret;
			if (iSpeed >= 1) {// ���ٶȵ�ptz
				ret = hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, iSpeed);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "��̨����ʧ��");
					return;
				}
			} else {// �ٶ�ΪĬ��ʱ
				ret = hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "��̨����ʧ��");
					return;
				}
			}
		}
	}

	private javax.swing.JButton jButtonVideo;
	private javax.swing.JButton jButtonScreenshot;
	private javax.swing.JButton jButtonScreenshot2;
	private javax.swing.JButton jButtonScreenshot3;
	private javax.swing.JButton jButtonScreenshot4;
	private javax.swing.JButton jButton1FocusNear;
	private javax.swing.JButton jButton1IrisClose;
	private javax.swing.JButton jButton1IrisOpen;
	private javax.swing.JButton jButton1ZoomOut;
	private javax.swing.JButton jButtonAuto;
	private javax.swing.JButton jButtonAux1;
	private javax.swing.JButton jButtonAux2;
	private javax.swing.JButton jButtonDefault;
	private javax.swing.JButton jButtonDeletePreset;
	private javax.swing.JButton jButtonFanPwron;
	private javax.swing.JButton jButtonFocusFar;
	private javax.swing.JButton jButtonGotoPreset;
	private javax.swing.JButton jButtonGotoSeq;
	private javax.swing.JButton jButtonHeater;
	private javax.swing.JButton jButtonLeft;
	private javax.swing.JButton jButtonLeftDown;
	private javax.swing.JButton jButtonLeftUp;
	private javax.swing.JButton jButtonLight;
	private javax.swing.JButton jButtonRight;
	private javax.swing.JButton jButtonRightDown;
	private javax.swing.JButton jButtonRightUp;
	private javax.swing.JButton jButtonSetPreset;
	private javax.swing.JButton jButtonSetSeq;
	private javax.swing.JButton jButtonTrackRun;
	private javax.swing.JButton jButtonTrackStart;
	private javax.swing.JButton jButtonTrackStop;
	private javax.swing.JButton jButtonUp;
	private javax.swing.JButton jButtonWiperPwron;
	private javax.swing.JButton jButtonZoomIn;
	private javax.swing.JButton jButtondown;
	private javax.swing.JComboBox jComboBoxPreset;
	private javax.swing.JComboBox jComboBoxSeq;
	private javax.swing.JComboBox jComboBoxSpeed;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanelPTZ;
	private javax.swing.JPanel jPanelVideoPara;
	private javax.swing.JSlider jSliderBright;
	private javax.swing.JSlider jSliderContrast;
	private javax.swing.JSlider jSliderHue;
	private javax.swing.JSlider jSliderSaturation;
	private javax.swing.JSlider jSliderVolume;
}
