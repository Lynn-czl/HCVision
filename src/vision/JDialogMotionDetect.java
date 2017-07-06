package vision;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.GDI32.RECT;
import com.sun.jna.examples.win32.User32.POINT;
import com.sun.jna.examples.win32.W32API.HDC;
import com.sun.jna.examples.win32.W32API.HWND;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

/**
 * �ƶ�����������
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class JDialogMotionDetect extends javax.swing.JDialog {

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	static GDI32 gDi = GDI32.INSTANCE;
	static USER32 uSer = USER32.INSTANCE;

	public static final int MAX_MOTION_NUM = 4;

	private HCNetSDK.NET_DVR_PICCFG_V30 m_struPicCfg;// ͼ�����,��ʼ����ֱ��ָ��ͨ���������ͼ������ṹ��
	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;// �豸��Ϣ����
	private HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;// Ԥ������

	private NativeLong m_lUserID;// �û����
	private NativeLong m_lPlayHandle;// Ԥ�����
	private int m_iChanShowNum;// �����ڴ�����ͨ����,Ԥ����ͨ��
	private boolean m_bInitialed;// �Ƿ��Ѿ���ʼ��

	private CheckListItem m_traggerAlarmOut[] = new CheckListItem[HCNetSDK.MAX_ALARMOUT_V30];// �������ͨ��checkbox��Ӧֵ
	private CheckListItem m_traggerRecord[] = new CheckListItem[HCNetSDK.MAX_CHANNUM_V30];// ����¼��ͨ��checkbox��Ӧֵ

	private boolean m_bDrawArea;// draw motion detect area
	private boolean m_bSetMotion;// set motion detect area

	private FDrawFunGet MotionDetectGetCallBack; // ��ʾ�ƶ��������ص�����
	private FDrawFunSet MotionDetectSetCallBack;// �����ƶ��������ص�����

	private int g_iDetectIndex = 0; // motion detect zone index
	private int g_dwPrecision = 16;// the precision of the 22*11 detect unit
									// under D1(704*576) resolution is 32*32, in
									// the demothepicture is displayed in
									// cif(352*288), and precision/2
	private RECT[] g_rectMotionDetectSet = new RECT[MAX_MOTION_NUM]; // motion
																		// detect
																		// zone
																		// display
																		// rectangle
	private RECT[] g_rectMotionDetectMouse = new RECT[MAX_MOTION_NUM];// mouse
																		// drawing
																		// line

	/*************************************************
	 * ����: JDialogMotionDetect ��������: ���캯�� Creates new form JDialogMotionDetect
	 *************************************************/
	public JDialogMotionDetect(javax.swing.JDialog parent, boolean modal, NativeLong lUserID, int iChannelNum,
			HCNetSDK.NET_DVR_PICCFG_V30 struPicCfg, HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo) {
		super(parent, modal);
		initComponents();

		m_lUserID = lUserID;
		m_iChanShowNum = iChannelNum;
		m_lPlayHandle = new NativeLong(-1);
		m_struPicCfg = struPicCfg;
		m_strDeviceInfo = strDeviceInfo;

		MotionDetectSetCallBack = new FDrawFunSet();
		MotionDetectGetCallBack = new FDrawFunGet();

		for (int i = 0; i < MAX_MOTION_NUM; i++) {
			g_rectMotionDetectSet[i] = new RECT();
			g_rectMotionDetectMouse[i] = new RECT();
		}

		for (int i = 0; i < HCNetSDK.MAX_ALARMOUT_V30; i++) {
			m_traggerAlarmOut[i] = new CheckListItem(false, null);
		}

		for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V30; i++) {
			m_traggerRecord[i] = new CheckListItem(false, null);
		}

		// ��ʼ���Ի���,Ԥ��,list��ʼ���Ȳ���
		initialDialog();
	}

	/*************************************************
	 * ����: initialDialog ��������: ��ʼ���Ի���
	 *************************************************/
	private void initialDialog() {
		// �ƶ�������� Ԥ��
		HWND hwnd = new HWND(Native.getComponentPointer(panelPlay));
		m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		m_strClientInfo.lChannel = new NativeLong(m_iChanShowNum);
		m_strClientInfo.hPlayWnd = hwnd;
		m_lPlayHandle = hCNetSDK.NET_DVR_RealPlay_V30(m_lUserID, m_strClientInfo, null, null, true);
		if (m_lPlayHandle.intValue() == -1) {
			JOptionPane.showMessageDialog(this, "Ԥ��ʧ��,����ֵ:" + hCNetSDK.NET_DVR_GetLastError());
		}

		// �����������ͨ��list
		jListTraggerAlarmOut.setCellRenderer(new CheckListItemRenderer());
		DefaultListModel listModelTraggerAlarmOut = new DefaultListModel();
		jListTraggerAlarmOut.setModel(listModelTraggerAlarmOut);
		jListTraggerAlarmOut.addMouseListener(new CheckListMouseListener());
		for (int i = 0; i < m_strDeviceInfo.byAlarmOutPortNum; i++) {
			m_traggerAlarmOut[i] = new CheckListItem(false, "AlarmOut" + (i + 1));
			listModelTraggerAlarmOut.addElement(m_traggerAlarmOut[i]); // Ϊ�����������List���ӱ������
		}
		// ����¼��ͨ��list
		jListTraggerRecord.setCellRenderer(new CheckListItemRenderer());
		DefaultListModel listModelTraggerRecord = new DefaultListModel();
		jListTraggerRecord.setModel(listModelTraggerRecord);
		jListTraggerRecord.addMouseListener(new CheckListMouseListener());
		for (int i = 0; i < m_strDeviceInfo.byChanNum; i++) {
			m_traggerRecord[i] = new CheckListItem(false, "Camara" + (i + 1));
			listModelTraggerRecord.addElement(m_traggerRecord[i]); // Ϊ����¼��List���ӱ������
		}

		// �����ݷ�ӳ���Ի���
		initialData();

		m_bInitialed = true;
	}

	/*************************************************
	 * ����: initialData ��������: ��ʾ��ʼ����
	 *************************************************/
	private void initialData() {
		if (m_struPicCfg.struMotion.byMotionSensitive == -1) {
			jComboBoxSensitive.setSelectedIndex(0);
		} else {
			jComboBoxSensitive.setSelectedIndex(m_struPicCfg.struMotion.byMotionSensitive);
		}

		jCheckBoxMonitorAlarm.setSelected((m_struPicCfg.struMotion.struMotionHandleType.dwHandleType & 0x01) == 1);
		jCheckBoxAudioAlarm.setSelected(((m_struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 1) & 0x01) == 1);
		jCheckBoxCenter.setSelected(((m_struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 2) & 0x01) == 1);
		jCheckBoxAlarmout.setSelected(((m_struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 3) & 0x01) == 1);
		jCheckBoxJPEG.setSelected(((m_struPicCfg.struMotion.struMotionHandleType.dwHandleType >> 4) & 0x01) == 1);

		for (int i = 0; i < HCNetSDK.MAX_ALARMOUT_V30; i++) {
			m_traggerAlarmOut[i].setCheck(m_struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[i] == 1);
		}

		for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V30; i++) {
			m_traggerRecord[i].setCheck(m_struPicCfg.struMotion.byRelRecordChan[i] == 1);
		}

		// ��ʾ����ʱ�����
		showAlarmTime();
	}

	/*************************************************
	 * ����: showAlarmTime ��������: ��ʾ����ʱ��
	 *************************************************/
	private void showAlarmTime() {
		int iWeekDay = jComboBoxDate.getSelectedIndex();
		jTextFieldInBeginHour1
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStartHour + "");
		jTextFieldInBeginMin1.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStartMin + "");
		jTextFieldInEndHour1.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStopHour + "");
		jTextFieldInEndMin1.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStopMin + "");

		jTextFieldInBeginHour2
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStartHour + "");
		jTextFieldInBeginMin2.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStartMin + "");
		jTextFieldInEndHour2.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStopHour + "");
		jTextFieldInEndMin2.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStopMin + "");

		jTextFieldInBeginHour3
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStartHour + "");
		jTextFieldInBeginMin3.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStartMin + "");
		jTextFieldInEndHour3.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStopHour + "");
		jTextFieldInEndMin3.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStopMin + "");

		jTextFieldInBeginHour4
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStartHour + "");
		jTextFieldInBeginMin4.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStartMin + "");
		jTextFieldInEndHour4.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStopHour + "");
		jTextFieldInEndMin4.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStopMin + "");

		jTextFieldInBeginHour5
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStartHour + "");
		jTextFieldInBeginMin5.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStartMin + "");
		jTextFieldInEndHour5.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStopHour + "");
		jTextFieldInEndMin5.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStopMin + "");

		jTextFieldInBeginHour6
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStartHour + "");
		jTextFieldInBeginMin6.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStartMin + "");
		jTextFieldInEndHour6.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStopHour + "");
		jTextFieldInEndMin6.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStopMin + "");

		jTextFieldInBeginHour7
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStartHour + "");
		jTextFieldInBeginMin7.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStartMin + "");
		jTextFieldInEndHour7.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStopHour + "");
		jTextFieldInEndMin7.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStopMin + "");

		jTextFieldInBeginHour8
				.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStartHour + "");
		jTextFieldInBeginMin8.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStartMin + "");
		jTextFieldInEndHour8.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStopHour + "");
		jTextFieldInEndMin8.setText(m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStopMin + "");
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		panelPlay = new java.awt.Panel();
		jPanel1 = new javax.swing.JPanel();
		jCheckBoxGetMotionArea = new javax.swing.JCheckBox();
		jCheckBoxSetMotionArea = new javax.swing.JCheckBox();
		jLabel1 = new javax.swing.JLabel();
		jComboBoxSensitive = new javax.swing.JComboBox();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jListTraggerRecord = new javax.swing.JList();
		jPanel3 = new javax.swing.JPanel();
		jCheckBoxMonitorAlarm = new javax.swing.JCheckBox();
		jCheckBoxCenter = new javax.swing.JCheckBox();
		jCheckBoxAudioAlarm = new javax.swing.JCheckBox();
		jCheckBoxJPEG = new javax.swing.JCheckBox();
		jCheckBoxAlarmout = new javax.swing.JCheckBox();
		jScrollPane2 = new javax.swing.JScrollPane();
		jListTraggerAlarmOut = new javax.swing.JList();
		jPanel4 = new javax.swing.JPanel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		jLabel14 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		jTextFieldInEndMin1 = new javax.swing.JTextField();
		jTextFieldInBeginHour1 = new javax.swing.JTextField();
		jTextFieldInBeginMin1 = new javax.swing.JTextField();
		jTextFieldInEndHour1 = new javax.swing.JTextField();
		jLabel15 = new javax.swing.JLabel();
		jLabel17 = new javax.swing.JLabel();
		jLabel18 = new javax.swing.JLabel();
		jLabel19 = new javax.swing.JLabel();
		jLabel33 = new javax.swing.JLabel();
		jTextFieldInEndMin2 = new javax.swing.JTextField();
		jLabel34 = new javax.swing.JLabel();
		jTextFieldInEndHour2 = new javax.swing.JTextField();
		jLabel35 = new javax.swing.JLabel();
		jTextFieldInBeginMin2 = new javax.swing.JTextField();
		jLabel36 = new javax.swing.JLabel();
		jTextFieldInBeginHour2 = new javax.swing.JTextField();
		jLabel37 = new javax.swing.JLabel();
		jTextFieldInEndMin3 = new javax.swing.JTextField();
		jLabel38 = new javax.swing.JLabel();
		jTextFieldInEndHour3 = new javax.swing.JTextField();
		jLabel39 = new javax.swing.JLabel();
		jTextFieldInBeginMin3 = new javax.swing.JTextField();
		jLabel40 = new javax.swing.JLabel();
		jTextFieldInBeginHour3 = new javax.swing.JTextField();
		jLabel41 = new javax.swing.JLabel();
		jTextFieldInEndMin4 = new javax.swing.JTextField();
		jLabel42 = new javax.swing.JLabel();
		jTextFieldInEndHour4 = new javax.swing.JTextField();
		jLabel43 = new javax.swing.JLabel();
		jTextFieldInBeginMin4 = new javax.swing.JTextField();
		jLabel44 = new javax.swing.JLabel();
		jTextFieldInBeginHour4 = new javax.swing.JTextField();
		jLabel45 = new javax.swing.JLabel();
		jTextFieldInEndMin5 = new javax.swing.JTextField();
		jLabel46 = new javax.swing.JLabel();
		jTextFieldInEndHour5 = new javax.swing.JTextField();
		jLabel47 = new javax.swing.JLabel();
		jTextFieldInBeginMin5 = new javax.swing.JTextField();
		jLabel48 = new javax.swing.JLabel();
		jTextFieldInBeginHour5 = new javax.swing.JTextField();
		jLabel49 = new javax.swing.JLabel();
		jTextFieldInEndMin6 = new javax.swing.JTextField();
		jLabel50 = new javax.swing.JLabel();
		jTextFieldInEndHour6 = new javax.swing.JTextField();
		jLabel51 = new javax.swing.JLabel();
		jTextFieldInBeginMin6 = new javax.swing.JTextField();
		jLabel52 = new javax.swing.JLabel();
		jTextFieldInBeginHour6 = new javax.swing.JTextField();
		jButtonConfirm = new javax.swing.JButton();
		jLabel53 = new javax.swing.JLabel();
		jTextFieldInBeginHour7 = new javax.swing.JTextField();
		jLabel54 = new javax.swing.JLabel();
		jTextFieldInBeginMin7 = new javax.swing.JTextField();
		jTextFieldInEndMin7 = new javax.swing.JTextField();
		jLabel55 = new javax.swing.JLabel();
		jTextFieldInEndHour7 = new javax.swing.JTextField();
		jLabel56 = new javax.swing.JLabel();
		jTextFieldInEndHour8 = new javax.swing.JTextField();
		jLabel57 = new javax.swing.JLabel();
		jLabel58 = new javax.swing.JLabel();
		jTextFieldInEndMin8 = new javax.swing.JTextField();
		jLabel59 = new javax.swing.JLabel();
		jTextFieldInBeginHour8 = new javax.swing.JTextField();
		jLabel60 = new javax.swing.JLabel();
		jTextFieldInBeginMin8 = new javax.swing.JTextField();
		jComboBoxDate = new javax.swing.JComboBox();
		jLabel7 = new javax.swing.JLabel();
		jButtonSave = new javax.swing.JButton();
		jButtonExit = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("�ƶ��������");

		panelPlay.setBackground(new java.awt.Color(204, 255, 255));
		panelPlay.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				panelPlayMousePressed(evt);
			}
		});
		panelPlay.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				panelPlayMouseDragged(evt);
			}
		});
		panelPlay.setLayout(null);

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("��������"));

		jCheckBoxGetMotionArea.setText("��ʾ�ƶ��������");
		jCheckBoxGetMotionArea.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxGetMotionAreaActionPerformed(evt);
			}
		});

		jCheckBoxSetMotionArea.setText("�����ƶ��������");
		jCheckBoxSetMotionArea.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxSetMotionAreaActionPerformed(evt);
			}
		});

		jLabel1.setText("������");

		jComboBoxSensitive.setModel(
				new javax.swing.DefaultComboBoxModel(new String[] { "�ر�", "0--���", "1", "2", "3", "4", "5--���" }));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jCheckBoxGetMotionArea).addGroup(
										jPanel1Layout.createSequentialGroup().addGap(21, 21, 21).addComponent(jLabel1)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jCheckBoxSetMotionArea).addComponent(jComboBoxSensitive,
										javax.swing.GroupLayout.PREFERRED_SIZE, 62,
										javax.swing.GroupLayout.PREFERRED_SIZE))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jCheckBoxGetMotionArea).addComponent(jCheckBoxSetMotionArea))
						.addGap(8, 8, 8)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1).addComponent(jComboBoxSensitive,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("����¼��ͨ��"));
		jPanel2.setPreferredSize(new java.awt.Dimension(304, 97));

		jScrollPane1.setViewportView(jListTraggerRecord);

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("��������ʽ"));

		jCheckBoxMonitorAlarm.setText("����������");

		jCheckBoxCenter.setText("�ϴ�����");

		jCheckBoxAudioAlarm.setText("��������");

		jCheckBoxJPEG.setText("Email JPEG");

		jCheckBoxAlarmout.setText("�����������");

		jScrollPane2.setViewportView(jListTraggerAlarmOut);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jCheckBoxMonitorAlarm).addComponent(jCheckBoxCenter)
								.addComponent(jCheckBoxJPEG).addComponent(jCheckBoxAudioAlarm))
						.addGap(28, 28, 28)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jCheckBoxAlarmout).addComponent(jScrollPane2,
										javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(12, Short.MAX_VALUE)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jCheckBoxMonitorAlarm).addComponent(jCheckBoxAlarmout))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel3Layout.createSequentialGroup().addComponent(jCheckBoxAudioAlarm)
										.addGap(2, 2, 2).addComponent(jCheckBoxCenter)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jCheckBoxJPEG))
								.addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE))));

		jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("����ʱ��"));

		jLabel8.setText("ʱ���4");

		jLabel9.setText("ʱ���3");

		jLabel10.setText("ʱ���2");

		jLabel11.setText("ʱ���5");

		jLabel12.setText("ʱ���6");

		jLabel13.setText("ʱ���7");

		jLabel14.setText("ʱ���8");

		jLabel16.setText("ʱ���1");

		jLabel15.setText("ʱ");

		jLabel17.setText("�� --");

		jLabel18.setText("ʱ");

		jLabel19.setText("��");

		jLabel33.setText("��");

		jLabel34.setText("ʱ");

		jLabel35.setText("�� --");

		jLabel36.setText("ʱ");

		jLabel37.setText("��");

		jLabel38.setText("ʱ");

		jLabel39.setText("�� --");

		jLabel40.setText("ʱ");

		jLabel41.setText("��");

		jLabel42.setText("ʱ");

		jLabel43.setText("�� --");

		jLabel44.setText("ʱ");

		jLabel45.setText("��");

		jLabel46.setText("ʱ");

		jLabel47.setText("�� --");

		jLabel48.setText("ʱ");

		jLabel49.setText("��");

		jLabel50.setText("ʱ");

		jLabel51.setText("�� --");

		jLabel52.setText("ʱ");

		jButtonConfirm.setText("ȷ��");
		jButtonConfirm.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonConfirmActionPerformed(evt);
			}
		});

		jLabel53.setText("ʱ");

		jLabel54.setText("�� --");

		jLabel55.setText("��");

		jLabel56.setText("ʱ");

		jLabel57.setText("��");

		jLabel58.setText("ʱ");

		jLabel59.setText("ʱ");

		jLabel60.setText("�� --");

		jComboBoxDate.setModel(
				new javax.swing.DefaultComboBoxModel(new String[] { "����һ", "���ڶ�", "������", "������", "������", "������", "������" }));
		jComboBoxDate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxDateActionPerformed(evt);
			}
		});

		jLabel7.setText("����");

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(
				jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						jPanel4Layout
								.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(
												jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 54,
																Short.MAX_VALUE)
														.addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(jLabel16))
														.addGap(18, 18, 18)
														.addGroup(
																jPanel4Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addGroup(jPanel4Layout.createSequentialGroup()
																				.addComponent(jTextFieldInBeginHour1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jLabel18,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jTextFieldInBeginMin1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGroup(jPanel4Layout.createSequentialGroup()
																				.addComponent(jTextFieldInBeginHour2,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jLabel36,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jTextFieldInBeginMin2,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGroup(jPanel4Layout.createSequentialGroup()
																				.addComponent(jTextFieldInBeginHour3,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jLabel40,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jTextFieldInBeginMin3,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGroup(jPanel4Layout.createSequentialGroup()
																				.addComponent(jTextFieldInBeginHour4,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(jLabel44,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						jTextFieldInBeginMin4,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						20,
																						javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(jPanel4Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(jLabel17,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel35,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel39,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel43,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jComboBoxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jTextFieldInEndHour1,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jTextFieldInEndMin1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jTextFieldInEndHour2,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jTextFieldInEndMin2,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jTextFieldInEndHour3,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jTextFieldInEndMin3,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jTextFieldInEndHour4,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jTextFieldInEndMin4,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGap(60, 60, 60)
								.addGroup(jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 52,
												Short.MAX_VALUE)
										.addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jLabel14))
								.addGap(18, 18, 18)
								.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel4Layout.createSequentialGroup()
														.addComponent(jTextFieldInBeginHour5,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jTextFieldInBeginMin5,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE,
																30, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(jPanel4Layout.createSequentialGroup()
														.addComponent(jTextFieldInBeginHour6,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jTextFieldInBeginMin6,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE,
																30, javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addGap(18, 18, 18)
												.addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(jPanel4Layout.createSequentialGroup()
																.addComponent(jTextFieldInEndHour5,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel46,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(jPanel4Layout.createSequentialGroup()
																.addComponent(jTextFieldInEndHour6,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel50,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE))))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jTextFieldInBeginHour7,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jTextFieldInBeginMin7,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(18, 18, 18)
												.addComponent(jTextFieldInEndHour7,
														javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout
														.createSequentialGroup()
														.addComponent(jTextFieldInBeginHour8,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jTextFieldInBeginMin8,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE,
																30, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(jTextFieldInEndHour8,
																javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(6, 6, 6)
														.addGroup(jPanel4Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(jTextFieldInEndMin5,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jTextFieldInEndMin6,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jTextFieldInEndMin7,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jTextFieldInEndMin8,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 20,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addComponent(jButtonConfirm))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE,
																20, javax.swing.GroupLayout.PREFERRED_SIZE))))
								.addContainerGap(19, Short.MAX_VALUE)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel7).addComponent(jComboBoxDate,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jTextFieldInEndHour1, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel15)
										.addComponent(jTextFieldInEndMin1, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel19))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInEndHour2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel34)
												.addComponent(jTextFieldInEndMin2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel33))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInEndHour3,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel38)
												.addComponent(jTextFieldInEndMin3,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel37))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInEndHour4,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel42)
												.addComponent(jTextFieldInEndMin4,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel41)))
								.addGroup(jPanel4Layout.createSequentialGroup().addComponent(jLabel17)
										.addGap(15, 15, 15).addComponent(jLabel35).addGap(15, 15, 15)
										.addComponent(jLabel39).addGap(15, 15, 15).addComponent(jLabel43))
								.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jTextFieldInBeginHour1, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel18).addComponent(jTextFieldInBeginMin1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInBeginHour2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel36).addComponent(jTextFieldInBeginMin2,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInBeginHour3,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel40).addComponent(jTextFieldInBeginMin3,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInBeginHour4,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel44).addComponent(jTextFieldInBeginMin4,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addGroup(jPanel4Layout.createSequentialGroup().addComponent(jLabel16)
										.addGap(15, 15, 15).addComponent(jLabel10).addGap(15, 15, 15)
										.addComponent(jLabel9).addGap(15, 15, 15).addComponent(jLabel8))
								.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(jPanel4Layout.createSequentialGroup().addComponent(jLabel11)
												.addGap(15, 15, 15).addComponent(jLabel12))
										.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInEndHour5,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel46))
												.addGap(9, 9, 9)
												.addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jTextFieldInEndHour6,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel50)))
										.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTextFieldInBeginHour5,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel48)
												.addComponent(jTextFieldInBeginMin5,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel47))
												.addGap(9, 9, 9)
												.addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jTextFieldInBeginHour6,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel52)
														.addComponent(jTextFieldInBeginMin6,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel51))))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel4Layout.createSequentialGroup().addComponent(jLabel13)
														.addGap(15, 15, 15).addComponent(jLabel14))
												.addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jTextFieldInEndHour7,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel56)
														.addComponent(jTextFieldInBeginHour7,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel53)
														.addComponent(jTextFieldInBeginMin7,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel54))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(jPanel4Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(jTextFieldInEndHour8,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel58)
																.addComponent(jTextFieldInBeginHour8,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel59)
																.addComponent(jTextFieldInBeginMin8,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(jLabel60)))))
								.addComponent(jLabel45)
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jTextFieldInEndMin5, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jTextFieldInEndMin6,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel49))
										.addGap(9, 9, 9)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jTextFieldInEndMin7,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel55))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(jPanel4Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jTextFieldInEndMin8,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel57))))
						.addGap(10, 10, 10).addComponent(jButtonConfirm)));

		jButtonSave.setText("ȷ��");
		jButtonSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSaveActionPerformed(evt);
			}
		});

		jButtonExit.setText("�˳�");
		jButtonExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(panelPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 352,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jPanel1,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))))
				.addGroup(layout.createSequentialGroup().addGap(9, 9, 9).addComponent(jPanel4,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addContainerGap(449, Short.MAX_VALUE).addComponent(jButtonSave)
								.addGap(26, 26, 26).addComponent(jButtonExit).addGap(49, 49, 49)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
						.addGroup(layout.createSequentialGroup().addContainerGap()
								.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(1, 1, 1).addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								layout.createSequentialGroup()
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(panelPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 288,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(19, 19, 19)))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(jButtonSave).addComponent(jButtonExit))
				.addContainerGap(14, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/*************************************************
	 * ����: "����" ��Ͽ��¼���Ӧ���� ��������: ��ʾ��Ӧ���ڲ���ʱ��
	 *************************************************/
	private void jComboBoxDateActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jComboBoxDateActionPerformed
	{// GEN-HEADEREND:event_jComboBoxDateActionPerformed
		if (m_bInitialed) {
			showAlarmTime();
		}
	}// GEN-LAST:event_jComboBoxDateActionPerformed

	/*************************************************
	 * ����: "��ʾ�ƶ��������" ��ѡ���¼���Ӧ���� ��������: ��ʾ�ƶ��������
	 *************************************************/
	private void jCheckBoxGetMotionAreaActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jCheckBoxGetMotionAreaActionPerformed
	{// GEN-HEADEREND:event_jCheckBoxGetMotionAreaActionPerformed
		if (m_lPlayHandle.intValue() < 0) {
			return;
		}
		if (jCheckBoxSetMotionArea.isSelected()) {
			jCheckBoxSetMotionArea.setSelected(false);
			m_bDrawArea = false;
		}
		if (jCheckBoxGetMotionArea.isSelected()) {
			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, null, 0);

			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
				Logger.getLogger(JDialogMotionDetect.class.getName()).log(Level.SEVERE, null, ex);
			}

			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, MotionDetectGetCallBack, 0);
		} else {
			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, null, 0);
		}
	}// GEN-LAST:event_jCheckBoxGetMotionAreaActionPerformed

	/*************************************************
	 * ����: "�����ƶ��������" ��ѡ���¼���Ӧ���� ��������: �����ƶ��������
	 *************************************************/
	private void jCheckBoxSetMotionAreaActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jCheckBoxSetMotionAreaActionPerformed
	{// GEN-HEADEREND:event_jCheckBoxSetMotionAreaActionPerformed
		// Set motion detect zone
		if (m_lPlayHandle.intValue() < 0) {
			return;
		}
		if (jCheckBoxGetMotionArea.isSelected()) {
			jCheckBoxGetMotionArea.setSelected(false);
		}
		if (jCheckBoxSetMotionArea.isSelected()) {
			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, null, 0);
			g_iDetectIndex = 0;
			m_bDrawArea = true;
			m_bSetMotion = true;
		} else {
			m_bDrawArea = false;
			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, null, 0);
		}
	}// GEN-LAST:event_jCheckBoxSetMotionAreaActionPerformed

	/*************************************************
	 * ����: Ԥ�����ڵ��� �¼���Ӧ���� ��������: ע��ص�������ʼ��ͼ
	 *************************************************/
	private void panelPlayMousePressed(java.awt.event.MouseEvent evt)// GEN-FIRST:event_panelPlayMousePressed
	{// GEN-HEADEREND:event_panelPlayMousePressed
		if (m_bDrawArea) {
			POINT point = new POINT(evt.getX(), evt.getY());
			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, MotionDetectSetCallBack, 0);
			if (g_iDetectIndex >= MAX_MOTION_NUM) {
				g_iDetectIndex = 0;
			}

			g_rectMotionDetectMouse[g_iDetectIndex].left = point.x / g_dwPrecision * g_dwPrecision;
			g_rectMotionDetectMouse[g_iDetectIndex].top = point.y / g_dwPrecision * g_dwPrecision;
			g_rectMotionDetectMouse[g_iDetectIndex].right = g_rectMotionDetectMouse[g_iDetectIndex].left;
			g_rectMotionDetectMouse[g_iDetectIndex].bottom = g_rectMotionDetectMouse[g_iDetectIndex].top;

			g_rectMotionDetectSet[g_iDetectIndex].left = point.x / g_dwPrecision * g_dwPrecision;
			g_rectMotionDetectSet[g_iDetectIndex].top = point.y / g_dwPrecision * g_dwPrecision;

			g_rectMotionDetectSet[g_iDetectIndex].right = point.x / g_dwPrecision * g_dwPrecision + 1;
			g_rectMotionDetectSet[g_iDetectIndex].bottom = point.y / g_dwPrecision * g_dwPrecision + 1;
			g_iDetectIndex++;
		}
	}// GEN-LAST:event_panelPlayMousePressed

	/*************************************************
	 * ����: Ԥ��������갴���ƶ� �¼���Ӧ���� ��������: ����
	 *************************************************/
	private void panelPlayMouseDragged(java.awt.event.MouseEvent evt)// GEN-FIRST:event_panelPlayMouseDragged
	{// GEN-HEADEREND:event_panelPlayMouseDragged
		if (m_bDrawArea) {
			POINT point = new POINT(evt.getX(), evt.getY());
			g_rectMotionDetectMouse[g_iDetectIndex - 1].right = point.x / g_dwPrecision * g_dwPrecision;
			g_rectMotionDetectMouse[g_iDetectIndex - 1].bottom = point.y / g_dwPrecision * g_dwPrecision;

			g_rectMotionDetectSet[g_iDetectIndex - 1].right = point.x / g_dwPrecision * g_dwPrecision;
			g_rectMotionDetectSet[g_iDetectIndex - 1].bottom = point.y / g_dwPrecision * g_dwPrecision;
		}
	}// GEN-LAST:event_panelPlayMouseDragged

	/*************************************************
	 * ����: ����ʱ�� "ȷ��" ��ť ���� �¼���Ӧ���� ��������: �����Ӧ���ڲ���ʱ��
	 *************************************************/
	private void jButtonConfirmActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jButtonConfirmActionPerformed
	{// GEN-HEADEREND:event_jButtonConfirmActionPerformed
		int iWeekDay = jComboBoxDate.getSelectedIndex();
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour1.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin1.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour1.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[0].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin1.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour2.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin2.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour2.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[1].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin2.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour3.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin3.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour3.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[2].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin3.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour4.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin4.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour4.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[3].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin4.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour5.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin5.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour5.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[4].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin5.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour6.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin6.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour6.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[5].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin6.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour7.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin7.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour7.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[6].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin7.getText());

		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStartHour = (byte) Integer
				.parseInt(jTextFieldInBeginHour8.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStartMin = (byte) Integer
				.parseInt(jTextFieldInBeginMin8.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStopHour = (byte) Integer
				.parseInt(jTextFieldInEndHour8.getText());
		m_struPicCfg.struMotion.struAlarmTime[iWeekDay].struAlarmTime[7].byStopMin = (byte) Integer
				.parseInt(jTextFieldInEndMin8.getText());
	}// GEN-LAST:event_jButtonConfirmActionPerformed

	/*************************************************
	 * ����: "�˳�" ��ť �����¼���Ӧ���� ��������: ���ٶԻ���
	 *************************************************/
	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jButtonExitActionPerformed
	{// GEN-HEADEREND:event_jButtonExitActionPerformed
		if (m_lPlayHandle.intValue() >= 0) {
			hCNetSDK.NET_DVR_RigisterDrawFun(m_lPlayHandle, null, 0);
			hCNetSDK.NET_DVR_StopRealPlay(m_lPlayHandle);
			m_lPlayHandle.setValue(-1);
		}
		dispose();
	}// GEN-LAST:event_jButtonExitActionPerformed

	/*************************************************
	 * ����: "ȷ��" ��ť �����¼���Ӧ���� ��������: �������õ��ṹ��
	 *************************************************/
	private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jButtonSaveActionPerformed
	{// GEN-HEADEREND:event_jButtonSaveActionPerformed
		int i = 0, j = 0, k = 0;
		if (m_bSetMotion) {
			// clear
			for (i = 0; i < 64; i++) {
				for (j = 0; j < 96; j++) {
					m_struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 0;
				}
			}
			// save zone settings on the device
			for (k = 0; k < g_iDetectIndex; k++) {
				if (g_rectMotionDetectMouse[k].top <= g_rectMotionDetectMouse[k].bottom) {
					if (g_rectMotionDetectMouse[k].left <= g_rectMotionDetectMouse[k].right) {// draw
																								// from
																								// top-left
																								// to
																								// bottom-right
						for (i = g_rectMotionDetectMouse[k].top / g_dwPrecision; i < 64
								&& i < g_rectMotionDetectMouse[k].bottom / g_dwPrecision; i++) {
							for (j = g_rectMotionDetectMouse[k].left
									/ g_dwPrecision; j < g_rectMotionDetectMouse[k].right / g_dwPrecision; j++) {
								m_struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 1;
							}
						}
					} else {// draw from top-right to bottom-left
						for (i = g_rectMotionDetectMouse[k].top / g_dwPrecision; i < 64
								&& i < g_rectMotionDetectMouse[k].bottom / g_dwPrecision; i++) {
							for (j = g_rectMotionDetectMouse[k].right
									/ g_dwPrecision; j < g_rectMotionDetectMouse[k].left / g_dwPrecision; j++) {
								m_struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 1;
							}
						}
					}
				} else {
					if (g_rectMotionDetectMouse[k].left <= g_rectMotionDetectMouse[k].right) {// draw
																								// from
																								// bottom-left
																								// to
																								// top-right
						for (i = g_rectMotionDetectMouse[k].bottom / g_dwPrecision; i < g_rectMotionDetectMouse[k].top
								/ g_dwPrecision; i++) {
							for (j = g_rectMotionDetectMouse[k].left
									/ g_dwPrecision; j < g_rectMotionDetectMouse[k].right / g_dwPrecision; j++) {
								m_struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 1;
							}
						}
					} else {// draw from bottom-right to top-left
						for (i = g_rectMotionDetectMouse[k].bottom / g_dwPrecision; i < g_rectMotionDetectMouse[k].top
								/ g_dwPrecision; i++) {
							for (j = g_rectMotionDetectMouse[k].right
									/ g_dwPrecision; j < g_rectMotionDetectMouse[k].left / g_dwPrecision; j++) {
								m_struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] = 1;
							}
						}
					}
				}
			}
		}

		if (jComboBoxSensitive.getSelectedIndex() == 0) {
			m_struPicCfg.struMotion.byMotionSensitive = -1;
		} else {
			m_struPicCfg.struMotion.byMotionSensitive = (byte) (jComboBoxSensitive.getSelectedIndex() - 1);
		}

		m_struPicCfg.struMotion.struMotionHandleType.dwHandleType = 0;
		m_struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxMonitorAlarm.isSelected() ? 1
				: 0) << 0);
		m_struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxAudioAlarm.isSelected() ? 1 : 0) << 1);
		m_struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxCenter.isSelected() ? 1 : 0) << 2);
		m_struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxAlarmout.isSelected() ? 1 : 0) << 3);
		m_struPicCfg.struMotion.struMotionHandleType.dwHandleType |= ((jCheckBoxJPEG.isSelected() ? 1 : 0) << 4);

		for (i = 0; i < HCNetSDK.MAX_ALARMOUT_V30; i++) {
			m_struPicCfg.struMotion.struMotionHandleType.byRelAlarmOut[i] = (byte) (m_traggerAlarmOut[i].getCheck() ? 1
					: 0);
		}

		for (i = 0; i < HCNetSDK.MAX_CHANNUM_V30; i++) {
			m_struPicCfg.struMotion.byRelRecordChan[i] = (byte) (m_traggerRecord[i].getCheck() ? 1 : 0);
		}
	}// GEN-LAST:event_jButtonSaveActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButtonConfirm;
	private javax.swing.JButton jButtonExit;
	private javax.swing.JButton jButtonSave;
	private javax.swing.JCheckBox jCheckBoxAlarmout;
	private javax.swing.JCheckBox jCheckBoxAudioAlarm;
	private javax.swing.JCheckBox jCheckBoxCenter;
	private javax.swing.JCheckBox jCheckBoxGetMotionArea;
	private javax.swing.JCheckBox jCheckBoxJPEG;
	private javax.swing.JCheckBox jCheckBoxMonitorAlarm;
	private javax.swing.JCheckBox jCheckBoxSetMotionArea;
	private javax.swing.JComboBox jComboBoxDate;
	private javax.swing.JComboBox jComboBoxSensitive;
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
	private javax.swing.JLabel jLabel33;
	private javax.swing.JLabel jLabel34;
	private javax.swing.JLabel jLabel35;
	private javax.swing.JLabel jLabel36;
	private javax.swing.JLabel jLabel37;
	private javax.swing.JLabel jLabel38;
	private javax.swing.JLabel jLabel39;
	private javax.swing.JLabel jLabel40;
	private javax.swing.JLabel jLabel41;
	private javax.swing.JLabel jLabel42;
	private javax.swing.JLabel jLabel43;
	private javax.swing.JLabel jLabel44;
	private javax.swing.JLabel jLabel45;
	private javax.swing.JLabel jLabel46;
	private javax.swing.JLabel jLabel47;
	private javax.swing.JLabel jLabel48;
	private javax.swing.JLabel jLabel49;
	private javax.swing.JLabel jLabel50;
	private javax.swing.JLabel jLabel51;
	private javax.swing.JLabel jLabel52;
	private javax.swing.JLabel jLabel53;
	private javax.swing.JLabel jLabel54;
	private javax.swing.JLabel jLabel55;
	private javax.swing.JLabel jLabel56;
	private javax.swing.JLabel jLabel57;
	private javax.swing.JLabel jLabel58;
	private javax.swing.JLabel jLabel59;
	private javax.swing.JLabel jLabel60;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JList jListTraggerAlarmOut;
	private javax.swing.JList jListTraggerRecord;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextField jTextFieldInBeginHour1;
	private javax.swing.JTextField jTextFieldInBeginHour2;
	private javax.swing.JTextField jTextFieldInBeginHour3;
	private javax.swing.JTextField jTextFieldInBeginHour4;
	private javax.swing.JTextField jTextFieldInBeginHour5;
	private javax.swing.JTextField jTextFieldInBeginHour6;
	private javax.swing.JTextField jTextFieldInBeginHour7;
	private javax.swing.JTextField jTextFieldInBeginHour8;
	private javax.swing.JTextField jTextFieldInBeginMin1;
	private javax.swing.JTextField jTextFieldInBeginMin2;
	private javax.swing.JTextField jTextFieldInBeginMin3;
	private javax.swing.JTextField jTextFieldInBeginMin4;
	private javax.swing.JTextField jTextFieldInBeginMin5;
	private javax.swing.JTextField jTextFieldInBeginMin6;
	private javax.swing.JTextField jTextFieldInBeginMin7;
	private javax.swing.JTextField jTextFieldInBeginMin8;
	private javax.swing.JTextField jTextFieldInEndHour1;
	private javax.swing.JTextField jTextFieldInEndHour2;
	private javax.swing.JTextField jTextFieldInEndHour3;
	private javax.swing.JTextField jTextFieldInEndHour4;
	private javax.swing.JTextField jTextFieldInEndHour5;
	private javax.swing.JTextField jTextFieldInEndHour6;
	private javax.swing.JTextField jTextFieldInEndHour7;
	private javax.swing.JTextField jTextFieldInEndHour8;
	private javax.swing.JTextField jTextFieldInEndMin1;
	private javax.swing.JTextField jTextFieldInEndMin2;
	private javax.swing.JTextField jTextFieldInEndMin3;
	private javax.swing.JTextField jTextFieldInEndMin4;
	private javax.swing.JTextField jTextFieldInEndMin5;
	private javax.swing.JTextField jTextFieldInEndMin6;
	private javax.swing.JTextField jTextFieldInEndMin7;
	private javax.swing.JTextField jTextFieldInEndMin8;
	private java.awt.Panel panelPlay;
	// End of variables declaration//GEN-END:variables

	/*************************************************
	 * ��: FDrawFunSet ��������: �����ƶ��������ص�����
	 *************************************************/
	class FDrawFunSet implements HCNetSDK.FDrawFun {
		public void invoke(NativeLong lRealHandle, HDC hDc, int dwUser) {
			int i = 0;
			for (i = 0; i < g_iDetectIndex; i++) {
				uSer.DrawEdge(hDc, g_rectMotionDetectSet[i], USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
			}
			gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
		}
	}

	/*************************************************
	 * ��: FDrawFunGet ��������: ��ʾ�ƶ��������ص�����
	 *************************************************/
	class FDrawFunGet implements HCNetSDK.FDrawFun {
		public void invoke(NativeLong lRealHandle, HDC hDc, int dwUser) {
			RECT rect = new RECT();
			int i = 0, j = 0;
			POINT point = new POINT();
			for (i = 0; i < 64; i++) {
				for (j = 0; j < 96; j++) {
					if (m_struPicCfg.struMotion.byMotionScope[i].byMotionScope[j] == 1) {
						point.x = j * g_dwPrecision;
						point.y = i * g_dwPrecision;
						rect.left = point.x;
						rect.top = point.y;
						rect.right = point.x + g_dwPrecision;
						rect.bottom = point.y + g_dwPrecision;
						uSer.DrawEdge(hDc, rect, USER32.BDR_SUNKENOUTER, USER32.BF_RECT);
					}
				}
			}
			gDi.SetBkMode(hDc, GDI32.TRANSPARENT);
		}
	}

	/******************************************************************************
	 * ��: CheckListItemRenderer JCheckBox ListCellRenderer
	 ******************************************************************************/
	public class CheckListItemRenderer extends JCheckBox implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			CheckListItem item = (CheckListItem) value;
			this.setSelected(item.getCheck());
			this.setText(item.getText());
			this.setFont(list.getFont());
			this.setEnabled(list.isEnabled());
			return this;
		}
	}

	/******************************************************************************
	 * ��: CheckListItem
	 *
	 ******************************************************************************/
	public class CheckListItem {
		boolean check;
		String text;

		public CheckListItem(boolean check, String text) {
			this.check = check;
			this.text = text;
		}

		public boolean getCheck() {
			return check;
		}

		public void setCheck(boolean _check) {
			check = _check;
		}

		public String getText() {
			return text;
		}

		public void setText(String _text) {
			text = _text;
		}
	}

	/******************************************************************************
	 * ��: CheckListMouseListener
	 *
	 ******************************************************************************/
	class CheckListMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			JList list = (JList) e.getSource();
			int index = list.locationToIndex(e.getPoint());
			CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
			item.setCheck(!item.getCheck());
			Rectangle rect = list.getCellBounds(index, index);
			list.repaint(rect);
		}
	}

}
