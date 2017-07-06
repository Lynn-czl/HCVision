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
 * 类 ： JFramePTZControl 类描述 ：云台控制
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class JFramePTZControl extends javax.swing.JFrame {

	int picNum = 10;

	private static PlayCtrl playControl = PlayCtrl.INSTANCE;
	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private NativeLong m_lRealHandle;// 预览句柄
	private boolean m_bAutoOn;// 自动左右云台
	private boolean m_bLightOn;// 开启灯光
	private boolean m_bWiperOn;// 开启雨刷
	private boolean m_bFanOn;// 开启风扇
	private boolean m_bHeaterOn;// 开启加热
	private boolean m_bAuxOn1;// 开启辅助1
	private boolean m_bAuxOn2;// 开启辅助2
	private boolean m_bIsOnCruise;// 是否在巡航
	private int m_iBrightness;// 亮度
	private int m_iContrast;// 对比度
	private int m_iSaturation;// 饱和度
	private int m_iHue;// 色度
	private int m_iVolume;// 音量
	private NativeLong iChannelNum = new NativeLong(-1);
	private NativeLong lUserID;
	private boolean videoFlag;

	// 标识是否在拼接操作
	private boolean isRun = false;
	private List<String> pathlist = null;
	private NET_DVR_JPEGPARA jpegArea;

	/**
	 * 函数: JFramePTZControl 函数描述: 构造函数 Creates new form JFramePTZControl
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

		// 巡航轨迹
		for (i = 0; i < HCNetSDK.MAX_CRUISE_V30; i++) {
			jComboBoxSeq.addItem(i + 1);
		}
		jComboBoxSeq.setSelectedIndex(0);
	}

	/**
	 * 处理录像
	 * 
	 * @param evt
	 */
	private void jButtonVideoActionPerformed(java.awt.event.ActionEvent evt) {

		if (videoFlag == false) {
			JFrame jf = new JFrame("录像参数设置");
			jf.setLocationRelativeTo(null);
			jf.setSize(350, 170);
			jf.getContentPane().setLayout(new GridLayout(4, 4));
			jf.add(new JLabel("自动录像"));
			jf.add(new JLabel("手动录像"));
			jf.add(new JLabel("输入录像时间(毫秒)"));

			JButton begin_bt = new JButton("开始录像");
			JButton end_bt = new JButton("结束录像");
			JTextField videotime = new JTextField(5);
			JButton confirmvideo = new JButton("确认录像");

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
			JOptionPane.showMessageDialog(this, "正在录像");
		}
	}

	// /**
	// * 上下叠加
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
	// // 录像
	// String name = "../Videos/" + String.valueOf(System.currentTimeMillis()) +
	// ".mp4";
	// System.out.println("开始录像" + hCNetSDK.NET_DVR_SaveRealData(lUserID,
	// name));
	// // 旋转过程中抓图，抓当前仰角的
	// Timer t = new Timer();
	// t.schedule(new TimerTask() {
	// int n = 0;
	// int dir = 0;
	//
	// @Override
	// public void run() {
	// if (n++ == 20) {
	// // 停止旋转
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1,
	// 2);
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_RIGHT,
	// 1, 2);
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_UP, 1,
	// 2);
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_DOWN,
	// 1, 2);
	// // 停止录像
	// System.out.println("停止录像： " +
	// hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
	// // 开始拼图
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// Stnthesis.process(pathlist.toArray(new String[pathlist.size()]));
	// isRun = false;
	// }
	// }).start();
	// // 停止计时
	// t.cancel();
	// } else {
	// if (dir == 0) {// 向上
	// // 停止向左
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1,
	// 2);
	// // 抓图
	// String name = path + String.valueOf(System.currentTimeMillis()) +
	// ".jpeg";
	// System.out.println("抓图 " + n + " :"
	// + hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea,
	// name));
	// pathlist.add(name);
	// // 向上转
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_UP, 0,
	// 2);
	// } else if (dir == 1) {// 向左
	// // 停止向上
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_UP, 1,
	// 2);
	// // 抓图
	// String name = path + String.valueOf(System.currentTimeMillis()) +
	// ".jpeg";
	// System.out.println("抓图 " + n + " :"
	// + hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea,
	// name));
	// pathlist.add(name);
	// // 向左转
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0,
	// 2);
	// } else if (dir == 2) {// 向下
	// // 停止向左
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1,
	// 2);
	// // 抓图
	// String name = path + String.valueOf(System.currentTimeMillis()) +
	// ".jpeg";
	// System.out.println("抓图 " + n + " :"
	// + hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea,
	// name));
	// pathlist.add(name);
	// // 向下转
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_DOWN,
	// 0, 2);
	// } else if (dir == 3) {// 向右
	// // 停止向下
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.TILT_DOWN,
	// 1, 2);
	// // 向左转
	// hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0,
	// 2);
	// }
	// dir++;
	// dir %= 4;
	// }
	// }
	// }, 0, 3000);
	// } else {
	// System.out.println("创建路径失败");
	// }
	// }

	/**
	 *
	 * 处理抓图，拼图，单方向，保存到内存
	 *
	 * @param evt
	 */
	private void jButtonScreenToMemActionPerformed(java.awt.event.ActionEvent evt) {
		if (isRun)
			return;
		System.out.println("启动抓图，保存到内存");
		isRun = true;
		List<byte[]> list = new ArrayList<byte[]>();
		String name = "Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";

		// 开始录像
		System.out.println("开始录像" + hCNetSDK.NET_DVR_SaveRealData(lUserID, name));
		// 开始旋转
		hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0, 2);

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			int n = 0;

			public void run() {
				if (n++ == picNum) {
					// 停止转动
					hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1, 2);
					// 停止录像
					System.out.println("停止录像： " + hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
					// 开始拼图
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
					System.out.println("截图 ： " + n + hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(lUserID, iChannelNum,
							jpegArea, buff, 1048576, ibr));
					list.add(buff);
				}
			}
		}, 0, 2500);
	}

	/**
	 *
	 * 处理抓图，拼图，单方向，保存到文件
	 *
	 * @param evt
	 */
	private void jButtonScreenToFileActionPerformed(java.awt.event.ActionEvent evt) {
		if (isRun)
			return;
		System.out.println("启动抓图，保存到文件");
		isRun = true;
		pathlist = new ArrayList<String>();
		String path = "Pictures/" + String.valueOf(System.currentTimeMillis()) + "/";
		File file = new File(path);
		if (file.mkdirs()) { // 录像
			String name = "Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
			System.out.println("开始录像" + hCNetSDK.NET_DVR_SaveRealData(lUserID, name)); // 开始旋转
			hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 0, 2); // 旋转过程中抓图，抓当前仰角的
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				int n = 0;

				public void run() {
					if (n++ == picNum) {
						hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_LEFT, 1, 2);

						System.out.println("停止录像： " + hCNetSDK.NET_DVR_StopSaveRealData(lUserID));
						new Thread(new Runnable() {
							public void run() {
								Stnthesis.process(pathlist.toArray(new String[pathlist.size()]));
								isRun = false;
							}
						}).start();
						t.cancel(); // Run2(path);
					} else {
						String name = path + String.valueOf(System.currentTimeMillis()) + ".jpeg";
						System.out.println("抓图 " + n + " :"
								+ hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, iChannelNum, jpegArea, name));
						pathlist.add(name);
					}
				}
			}, 0, 2500);
		} else {
			System.out.println("创建路径失败");
		}
	}

	/*****************************************************************************
	 * 设置预置点，保存到内存
	 * 
	 *****************************************************************************/
	public void enscanAllViewToMem() {
		if (isRun)
			return;
		int num = picNum;
		System.out.println("启动预置点抓图，保存到内存");
		isRun = true;
		List<byte[]> list = new ArrayList<byte[]>();
		String name = "Videos/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
		// 开始录像
		// System.out.println("开始录像" + hCNetSDK.NET_DVR_SaveRealData(lUserID,
		// name));

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "清除预置点失败");
			return;
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "设置预置点失败");
			return;
		}
		for (int i = 1; i <= num; i++) {
			byte[] buff = new byte[1048576];
			IntByReference ibr = new IntByReference();
			boolean cResult = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(lUserID, iChannelNum, jpegArea, buff, 1048576,
					ibr);
			System.out.println("截图 ： " + i + " : " + cResult);
			if (!cResult) {
				System.out.println("抓图失败，无法进行增强全景扫描");
				return;
			}
			list.add(buff);
			Rotate(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1000, 4);
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "调用预置点失败");
			return;
		}
		// 等待球机回到预置点
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
			System.out.println("截图 ： " + i + " : " + cResult);
			if (!cResult) {
				System.out.println("抓图失败，无法进行增强全景扫描");
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
	 * 预置点扫描，保存到文件
	 * 
	 * @param num
	 */
	public void enscanAllView() {
		if (isRun)
			return;
		int num = picNum;
		System.out.println("启动预置点抓图，保存到文件");
		isRun = true;
		String enscanAllViewdir_path = "Pictures\\" + System.currentTimeMillis() + "\\";
		File enscanAllViewdir_pathfile = new File(enscanAllViewdir_path);
		if (!enscanAllViewdir_pathfile.exists())
			enscanAllViewdir_pathfile.mkdirs();

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "清除预置点失败");
			return;
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "设置预置点失败");
			return;
		}

		for (int i = 1; i <= num; i++) {
			if (CapturePicture(enscanAllViewdir_path + "up\\", String.valueOf(i)) == false) {
				System.out.println("抓图失败，无法进行增强全景扫描");
				return;
			}
			Rotate(m_lRealHandle, HCNetSDK.PAN_RIGHT, 1000, 4);
		}

		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, 1)) {
			JOptionPane.showMessageDialog(this, "调用预置点失败");
			return;
		}
		// 等待球机回到预置点
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Rotate(m_lRealHandle, HCNetSDK.TILT_DOWN, 1000, 3);

		for (int i = 1; i <= num; i++) {
			if (CapturePicture(enscanAllViewdir_path + "down\\", String.valueOf(i)) == false) {
				System.out.println("抓图失败，无法进行增强全景扫描");
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
			System.out.println("抓图失败,图片重名");
			return false;
		}

		if (hCNetSDK.NET_DVR_CaptureJPEGPicture(lUserID, new NativeLong(1), jpegArea, sPicName)) {
			System.out.println("抓图:" + sPicName);
		} else {
			System.out.println("抓图失败");
			return false;
		}

		return true;
	}

	/***********************************************************************************/

	/**
	 * 控制旋转
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
	 * 初始化组件
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
		setTitle("云台控制");

		jPanelPTZ.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		jButtonAux2.setText("辅助2");
		jButtonAux2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonAux2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAux2ActionPerformed(evt);
			}
		});

		jButtonAux1.setText("辅助");
		jButtonAux1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonAux1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAux1ActionPerformed(evt);
			}
		});

		jButton1IrisClose.setText("小");
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

		jButtonFocusFar.setText("远");
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

		jButton1ZoomOut.setText("伸");
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

		jButtonRight.setText("右");
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

		jButtonRightDown.setText("右下");
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

		jButtonRightUp.setText("右上");
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

		jButtonUp.setText("上");
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

		jButtonAuto.setText("自动");
		jButtonAuto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonAuto.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAutoActionPerformed(evt);
			}
		});

		jButtondown.setText("下");
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

		jButtonLeftUp.setText("左上");
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

		jButtonLeft.setText("左");
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

		jButtonLeftDown.setText("左下");
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

		jButtonZoomIn.setText("缩");
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

		jButton1FocusNear.setText("近");
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

		jButton1IrisOpen.setText("大");
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

		jButtonLight.setText("灯光");
		jButtonLight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonLight.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonLightActionPerformed(evt);
			}
		});

		jButtonFanPwron.setText("风扇");
		jButtonFanPwron.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonFanPwron.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonFanPwronActionPerformed(evt);
			}
		});

		jButtonHeater.setText("加热");
		jButtonHeater.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonHeater.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonHeaterActionPerformed(evt);
			}
		});

		jButtonWiperPwron.setText("雨刷");
		jButtonWiperPwron.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonWiperPwron.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonWiperPwronActionPerformed(evt);
			}
		});

		jLabel1.setText("光圈");

		jLabel3.setText("聚焦");

		jLabel2.setText("调焦");

		jLabel4.setText("云台速度");

		jComboBoxSpeed.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "默认", "1", "2", "3", "4", "5" }));

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("预置点"));

		jButtonGotoPreset.setText("调用");
		jButtonGotoPreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonGotoPreset.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonGotoPresetActionPerformed(evt);
			}
		});

		jButtonSetPreset.setText("设置");
		jButtonSetPreset.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonSetPreset.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetPresetActionPerformed(evt);
			}
		});

		jButtonDeletePreset.setText("删除");
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

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("巡航路径"));

		jButtonGotoSeq.setText("调用");
		jButtonGotoSeq.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonGotoSeq.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonGotoSeqActionPerformed(evt);
			}
		});

		jButtonSetSeq.setText("设置");
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

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("轨道记录"));

		jButtonTrackStop.setText("停止");
		jButtonTrackStop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonTrackStop.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonTrackStopActionPerformed(evt);
			}
		});

		jButtonTrackRun.setText("运行");
		jButtonTrackRun.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jButtonTrackRun.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonTrackRunActionPerformed(evt);
			}
		});

		jButtonTrackStart.setText("开始");
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

		jLabel5.setText("对比度");
		jPanelVideoPara.add(jLabel5);
		jLabel5.setBounds(10, 50, 36, 15);

		jLabel6.setText("饱和度");
		jPanelVideoPara.add(jLabel6);
		jLabel6.setBounds(10, 80, 36, 15);

		jLabel7.setText("色度");
		jPanelVideoPara.add(jLabel7);
		jLabel7.setBounds(10, 110, 24, 15);

		// jLabel8.setText("音量");
		// jPanelVideoPara.add(jLabel8);
		// jLabel8.setBounds(10, 140, 24, 15);

		jLabel9.setText("亮度");
		jPanelVideoPara.add(jLabel9);
		jLabel9.setBounds(10, 20, 24, 15);

		jButtonDefault.setText("默认值");
		jButtonDefault.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonDefaultActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonDefault);
		jButtonDefault.setBounds(20, 140, 70, 23);

		jButtonVideo.setText("录像");
		jButtonVideo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonVideoActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonVideo);
		jButtonVideo.setBounds(100, 140, 70, 23);

		// 抓图方式1，保存图片到文件
		jButtonScreenshot.setText("抓图1");
		jButtonScreenshot.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonScreenToFileActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonScreenshot);
		jButtonScreenshot.setBounds(20, 170, 70, 23);

		// 抓图方式2，不保存图片
		jButtonScreenshot2.setText("抓图2");
		jButtonScreenshot2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonScreenToMemActionPerformed(evt);
			}
		});
		jPanelVideoPara.add(jButtonScreenshot2);
		jButtonScreenshot2.setBounds(100, 170, 70, 23);

		// 抓图方式3，保存图片到文件
		jButtonScreenshot3.setText("抓图3");
		jButtonScreenshot3.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enscanAllView();
			}
		});
		jPanelVideoPara.add(jButtonScreenshot3);
		jButtonScreenshot3.setBounds(20, 200, 70, 23);

		// 抓图方式4，不保存图片
		jButtonScreenshot4.setText("抓图4");
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
	 * 函数: 左上 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 右下 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 上 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 下 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 右上 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 左下 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 左 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 右 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 调焦 缩 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 调焦 伸 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 聚焦 近 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 聚焦 远 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 光圈 开 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: 光圈 关 按钮的press和release响应函数 函数描述: 云台控制函数
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
	 * 函数: "自动"按钮 双击响应函数 函数描述: 云台控制函数 云台开始/停止左右自动扫描
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
			jButtonAuto.setText("停止");
			m_bAutoOn = true;
		} else {
			if (iSpeed >= 1) {
				hCNetSDK.NET_DVR_PTZControlWithSpeed(m_lRealHandle, HCNetSDK.PAN_AUTO, 1, iSpeed);
			} else {
				hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.PAN_AUTO, 1);
			}
			jButtonAuto.setText("自动");
			m_bAutoOn = false;
		}
	}

	/**
	 * 函数: "灯光"按钮 双击响应函数 函数描述: 云台控制函数 打开/关闭灯光
	 * 
	 * @param evt
	 */
	private void jButtonLightActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bLightOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.LIGHT_PWRON, 0);
			jButtonLight.setText("关灯");
			m_bLightOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.LIGHT_PWRON, 1);
			jButtonLight.setText("灯光");
			m_bLightOn = false;
		}
	}

	/**
	 * 函数: "雨刷"按钮 双击响应函数 函数描述: 云台控制函数 开始/停止雨刷
	 * 
	 * @param evt
	 */
	private void jButtonWiperPwronActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bWiperOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.WIPER_PWRON, 0);
			jButtonWiperPwron.setText("雨停");
			m_bWiperOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.WIPER_PWRON, 1);
			jButtonWiperPwron.setText("雨刷");
			m_bWiperOn = false;
		}
	}

	/**
	 * 函数: "风扇"按钮 双击响应函数 函数描述: 云台控制函数 开始/停止 风扇
	 * 
	 * @param evt
	 */
	private void jButtonFanPwronActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bFanOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.FAN_PWRON, 0);
			jButtonFanPwron.setText("停风");
			m_bFanOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.FAN_PWRON, 1);
			jButtonFanPwron.setText("风扇");
			m_bFanOn = false;
		}
	}

	/**
	 * 函数: "加热"按钮 双击响应函数 函数描述: 云台控制函数 开始/停止 加热
	 * 
	 * @param evt
	 */
	private void jButtonHeaterActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bHeaterOn) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.HEATER_PWRON, 0);
			jButtonHeater.setText("停止");
			m_bHeaterOn = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.HEATER_PWRON, 1);
			jButtonHeater.setText("加热");
			m_bHeaterOn = false;
		}
	}

	/**
	 * 函数: "辅助1"按钮 双击响应函数 函数描述: 云台控制函数 开始/停止 辅助1
	 * 
	 * @param evt
	 */
	private void jButtonAux1ActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bAuxOn1) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 0);
			jButtonAux1.setText("停止1");
			m_bAuxOn1 = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 1);
			jButtonAux1.setText("辅助1");
			m_bAuxOn1 = false;
		}
	}

	/**
	 * 函数: "辅助2"按钮 双击响应函数 函数描述: 云台控制函数 开始/停止 辅助2
	 * 
	 * @param evt
	 */
	private void jButtonAux2ActionPerformed(java.awt.event.ActionEvent evt) {
		if (!m_bAuxOn2) {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 0);
			jButtonAux2.setText("停止2");
			m_bAuxOn2 = true;
		} else {
			hCNetSDK.NET_DVR_PTZControl(m_lRealHandle, HCNetSDK.AUX_PWRON1, 1);
			jButtonAux2.setText("辅助2");
			m_bAuxOn2 = false;
		}
	}

	/**
	 * 函数: 轨道记录 "开始"按钮 双击响应函数 函数描述: 开始轨道记录
	 * 
	 * @param evt
	 */
	private void jButtonTrackStartActionPerformed(java.awt.event.ActionEvent evt) {
		if (!hCNetSDK.NET_DVR_PTZTrack(m_lRealHandle, HCNetSDK.STA_MEM_CRUISE)) {
			JOptionPane.showMessageDialog(this, "开始记录轨迹失败");
			return;
		}
	}

	/**
	 * 函数: 轨道记录 "停止"按钮 双击响应函数 函数描述: 停止轨道记录
	 * 
	 * @param evt
	 */
	private void jButtonTrackStopActionPerformed(java.awt.event.ActionEvent evt) {
		if (!hCNetSDK.NET_DVR_PTZTrack(m_lRealHandle, HCNetSDK.STO_MEM_CRUISE)) {
			JOptionPane.showMessageDialog(this, "停止记录轨道失败");
			return;
		}
	}

	/**
	 * 函数: 轨道记录 "运行"按钮 双击响应函数 函数描述: 运行轨道记录
	 * 
	 * @param evt
	 */
	private void jButtonTrackRunActionPerformed(java.awt.event.ActionEvent evt) {
		if (!hCNetSDK.NET_DVR_PTZTrack(m_lRealHandle, HCNetSDK.RUN_CRUISE)) {
			JOptionPane.showMessageDialog(this, "运行轨迹失败");
			return;
		}
	}

	/**
	 * 函数: 预置点 "调用"按钮 双击响应函数 函数描述: 调用预置点
	 * 
	 * @param evt
	 */
	private void jButtonGotoPresetActionPerformed(java.awt.event.ActionEvent evt) {
		int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.GOTO_PRESET, iPreset)) {
			JOptionPane.showMessageDialog(this, "调用预置点失败");
			return;
		}
	}

	/**
	 * 函数: 预置点 "设置"按钮 双击响应函数 函数描述: 设置预置点
	 * 
	 * @param evt
	 */
	private void jButtonSetPresetActionPerformed(java.awt.event.ActionEvent evt) {
		int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.SET_PRESET, iPreset)) {
			JOptionPane.showMessageDialog(this, "设置预置点失败");
			return;
		}
	}

	/**
	 * 函数: 预置点 "删除"按钮 双击响应函数 函数描述: 删除预置点
	 * 
	 * @param evt
	 */
	private void jButtonDeletePresetActionPerformed(java.awt.event.ActionEvent evt) {
		int iPreset = jComboBoxPreset.getSelectedIndex() + 1;
		if (!hCNetSDK.NET_DVR_PTZPreset(m_lRealHandle, HCNetSDK.CLE_PRESET, iPreset)) {
			JOptionPane.showMessageDialog(this, "设置预置点失败");
			return;
		}
	}

	/**
	 * 函数: 巡航路径 "调用"按钮 双击响应函数 函数描述: 调用巡航路径
	 * 
	 * @param evt
	 */
	private void jButtonGotoSeqActionPerformed(java.awt.event.ActionEvent evt) {
		byte iSeq = (byte) (jComboBoxSeq.getSelectedIndex() + 1);
		if (!m_bIsOnCruise) {
			if (!hCNetSDK.NET_DVR_PTZCruise(m_lRealHandle, HCNetSDK.RUN_SEQ, iSeq, (byte) 0, (short) 0)) {
				JOptionPane.showMessageDialog(this, "调用巡航失败");
				return;
			}
		}
	}

	/**
	 * 函数: 巡航路径 "设置"按钮 双击响应函数 函数描述: 设置巡航路径
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
			JOptionPane.showMessageDialog(this, "设置预览视频显示参数失败");
			return false;
		} else {
			return true;
		}
	}

	private void MyPTZControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop, int iSpeed) {
		if (lRealHandle.intValue() >= 0) {
			boolean ret;
			if (iSpeed >= 1) {// 有速度的ptz
				ret = hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, iSpeed);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "云台控制失败");
					return;
				}
			} else {// 速度为默认时
				ret = hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "云台控制失败");
					return;
				}
			}
		}
	}

	/**
	 * 函数名: PTZControlAll 函数描述: 云台控制函数 输入参数: lRealHandle: 预览句柄 iPTZCommand:
	 * PTZ控制命令 iStop: 开始或是停止操作 输出参数: 返回值:
	 * 
	 * @param lRealHandle
	 * @param iPTZCommand
	 * @param iStop
	 */
	private void PTZControlAll(NativeLong lRealHandle, int iPTZCommand, int iStop) {
		int iSpeed = jComboBoxSpeed.getSelectedIndex();
		if (lRealHandle.intValue() >= 0) {
			boolean ret;
			if (iSpeed >= 1) {// 有速度的ptz
				ret = hCNetSDK.NET_DVR_PTZControlWithSpeed(lRealHandle, iPTZCommand, iStop, iSpeed);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "云台控制失败");
					return;
				}
			} else {// 速度为默认时
				ret = hCNetSDK.NET_DVR_PTZControl(lRealHandle, iPTZCommand, iStop);
				if (!ret) {
					JOptionPane.showMessageDialog(this, "云台控制失败");
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
