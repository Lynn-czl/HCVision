
package vision;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import javax.swing.JOptionPane;

/**
 * ������ ��ͨ����������
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class JDialogChannelConfig extends javax.swing.JDialog {

	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private HCNetSDK.NET_DVR_PICCFG_V30 m_struPicCfg;// ͼƬ����
	private HCNetSDK.NET_DVR_COMPRESSIONCFG_V30 m_struCompressionCfg;// ѹ������
	private HCNetSDK.NET_DVR_RECORD_V30 m_struRemoteRecCfg;// ¼�����
	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;// �豸��Ϣ
	private HCNetSDK.NET_DVR_SHOWSTRING_V30 m_strShowString;// �����ַ�����
	private NativeLong lUserID;// �û�ID

	/**
	 * ����: JDialogChannelConfig ��������: ���캯��
	 * 
	 * @param parent
	 * @param modal
	 * @param lUserId
	 * @param strDeviceInfo
	 */
	public JDialogChannelConfig(java.awt.Frame parent, boolean modal, NativeLong lUserId,
			HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo) {
		super(parent, modal);

		lUserID = lUserId;
		m_strDeviceInfo = strDeviceInfo;
		initComponents();
	}

	private void initComponents() {
		jPanelChannelConfig = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabelChannelName = new javax.swing.JLabel();
		jComboBoxChannelNumber = new javax.swing.JComboBox();
		jTextFieldChannelName = new javax.swing.JTextField();
		jPanelCompressPara = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jComboBoxCompressType = new javax.swing.JComboBox();
		jComboBoxImageQulity = new javax.swing.JComboBox();
		jComboBoxFrameRate = new javax.swing.JComboBox();
		jComboBoxStreamType = new javax.swing.JComboBox();
		jComboBoxMaxBitRate = new javax.swing.JComboBox();
		jLabel17 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jComboBoxResolution = new javax.swing.JComboBox();
		jLabel7 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jComboBoxBitRateType = new javax.swing.JComboBox();
		jComboBoxBpInterval = new javax.swing.JComboBox();
		jTextFieldBitRate = new javax.swing.JTextField();
		jLabel10 = new javax.swing.JLabel();
		jButtonCompressOK = new javax.swing.JButton();
		jTextFieldIInterval = new javax.swing.JTextField();
		jPanel1 = new javax.swing.JPanel();
		jCheckBoxRecord = new javax.swing.JCheckBox();
		jButtonSetRecord = new javax.swing.JButton();
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		jComboBoxPreRecordTime = new javax.swing.JComboBox();
		jComboBoxRecordDelay = new javax.swing.JComboBox();
		jLabel13 = new javax.swing.JLabel();
		jLabel14 = new javax.swing.JLabel();
		jTextFieldSaveDays = new javax.swing.JTextField();
		jCheckBoxRebundancy = new javax.swing.JCheckBox();
		jCheckBoxAudioRec = new javax.swing.JCheckBox();
		jPanel2 = new javax.swing.JPanel();
		jCheckBoxHideArea = new javax.swing.JCheckBox();
		jCheckBoxSignalLost = new javax.swing.JCheckBox();
		jCheckBoxMotion = new javax.swing.JCheckBox();
		jCheckBoxHideAlarm = new javax.swing.JCheckBox();
		jButtonSetHideArea = new javax.swing.JButton();
		jButtonSetSignalLost = new javax.swing.JButton();
		jButtonSetMotion = new javax.swing.JButton();
		jButtonSetHideAlarm = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();
		jCheckBoxOSD = new javax.swing.JCheckBox();
		jLabel15 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		jTextFieldOSDX = new javax.swing.JTextField();
		jTextFieldOSDY = new javax.swing.JTextField();
		jCheckBoxOSDDate = new javax.swing.JCheckBox();
		jLabel18 = new javax.swing.JLabel();
		jLabel19 = new javax.swing.JLabel();
		jLabel20 = new javax.swing.JLabel();
		jComboBoxOSDFormat = new javax.swing.JComboBox();
		jComboBoxDateFormat = new javax.swing.JComboBox();
		jComboBoxTimeFormat = new javax.swing.JComboBox();
		jSeparator2 = new javax.swing.JSeparator();
		jCheckBoxChannelName = new javax.swing.JCheckBox();
		jLabel21 = new javax.swing.JLabel();
		jLabel22 = new javax.swing.JLabel();
		jTextFieldChannekNameX = new javax.swing.JTextField();
		jTextFieldChannelNameY = new javax.swing.JTextField();
		jButtonString = new javax.swing.JButton();
		jButtonSetChanConfig = new javax.swing.JButton();
		jButtonExit = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("ͨ����������");
		setBounds(new java.awt.Rectangle(0, 0, 600, 800));
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setFont(new java.awt.Font("����", 0, 10));
		getContentPane().setLayout(null);

		jPanelChannelConfig.setBorder(javax.swing.BorderFactory.createTitledBorder("ͨ��������Ϣ"));

		jLabel1.setText("ͨ����");

		jLabelChannelName.setText("ͨ������");

		jComboBoxChannelNumber.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxChannelNumberActionPerformed(evt);
			}
		});

		jTextFieldChannelName.setText("0");

		jPanelCompressPara.setBorder(javax.swing.BorderFactory.createTitledBorder("ѹ����������"));

		jLabel2.setText("ѹ����������");

		jLabel3.setText("ͼ������");

		jLabel4.setText("��Ƶ֡��");

		jLabel5.setText("��������");

		jLabel6.setText("λ������");

		jComboBoxCompressType
				.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "¼��(������)", "����(������)", "�¼�����" }));
		jComboBoxCompressType.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxCompressTypeActionPerformed(evt);
			}
		});

		jComboBoxImageQulity
				.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "���", "�κ�", "�Ϻ�", "һ��", "�ϲ�", "��" }));

		jComboBoxFrameRate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ȫ��", "1/16", "1/8", "1/4",
				"1/2", "1", "2", "4", "6", "8", "10", "12", "16", "20", "15", "18", "22" }));

		jComboBoxStreamType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "��Ƶ��", "������" }));

		jComboBoxMaxBitRate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "32K", "48K", "64K", "80K",
				"96K", "128K", "160K", "192K", "224k", "256K", "320K", "384K", "448K", "512K", "640K", "768K", "896K",
				"1024K", "1280K", "1536K", "1792K", "2048K", "�Զ���" }));

		jLabel17.setText("�ֱ���");

		jLabel8.setText("I֡���");

		jComboBoxResolution.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "DCIF", "CIF", "QCIF", "4CIF", "2CIF", "VGA", "UXGA", "SVGA", "HD720p", "SXGA" }));

		jLabel7.setText("λ������");

		jLabel9.setText("BP֡���");

		jComboBoxBitRateType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "������", "������" }));

		jComboBoxBpInterval.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BBP֡", "BP֡", "��P֡" }));

		jTextFieldBitRate.setText("0");

		jLabel10.setText("kbps");

		jButtonCompressOK.setText("����ѹ������");
		jButtonCompressOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonCompressOKActionPerformed(evt);
			}
		});

		jTextFieldIInterval.setText("0");

		javax.swing.GroupLayout jPanelCompressParaLayout = new javax.swing.GroupLayout(jPanelCompressPara);
		jPanelCompressPara.setLayout(jPanelCompressParaLayout);
		jPanelCompressParaLayout.setHorizontalGroup(jPanelCompressParaLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelCompressParaLayout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(jPanelCompressParaLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanelCompressParaLayout.createSequentialGroup().addComponent(jLabel2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jComboBoxCompressType, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(jPanelCompressParaLayout.createSequentialGroup()
										.addGroup(
												jPanelCompressParaLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel3).addComponent(jLabel4))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(jPanelCompressParaLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(jComboBoxFrameRate, 0,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jComboBoxImageQulity, 0, 64, Short.MAX_VALUE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(jPanelCompressParaLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanelCompressParaLayout.createSequentialGroup()
														.addComponent(jLabel5).addGap(18, 18, 18)
														.addComponent(jComboBoxStreamType,
																javax.swing.GroupLayout.PREFERRED_SIZE, 97,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(jPanelCompressParaLayout.createSequentialGroup()
														.addComponent(jLabel6).addGap(18, 18, 18)
														.addGroup(jPanelCompressParaLayout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING,
																		false)
																.addComponent(jComboBoxMaxBitRate, 0,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(jTextFieldBitRate))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(jLabel10)))
										.addGap(24, 24, 24)
										.addGroup(jPanelCompressParaLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel17).addComponent(jLabel8))
										.addGroup(jPanelCompressParaLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanelCompressParaLayout.createSequentialGroup()
														.addGap(21, 21, 21).addComponent(jComboBoxResolution,
																javax.swing.GroupLayout.PREFERRED_SIZE, 70,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(jPanelCompressParaLayout.createSequentialGroup()
														.addGap(18, 18, 18).addComponent(jTextFieldIInterval,
																javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGap(25, 25, 25).addGroup(
												jPanelCompressParaLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addGroup(jPanelCompressParaLayout.createSequentialGroup()
																.addGroup(jPanelCompressParaLayout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																		.addComponent(jLabel7).addComponent(jLabel9))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(jPanelCompressParaLayout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING,
																				false)
																		.addComponent(jComboBoxBitRateType,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jComboBoxBpInterval, 0, 67,
																				Short.MAX_VALUE)))
														.addComponent(jButtonCompressOK,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))))));
		jPanelCompressParaLayout.setVerticalGroup(jPanelCompressParaLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelCompressParaLayout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(jPanelCompressParaLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2)
								.addComponent(jComboBoxCompressType, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanelCompressParaLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3)
								.addComponent(jLabel5)
								.addComponent(jComboBoxImageQulity, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxStreamType, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxBitRateType, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel17)
								.addComponent(jComboBoxResolution, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel7))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanelCompressParaLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4)
								.addComponent(jLabel6)
								.addComponent(jComboBoxFrameRate, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxMaxBitRate, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxBpInterval, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel8)
								.addComponent(jTextFieldIInterval, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel9))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanelCompressParaLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldBitRate, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonCompressOK).addComponent(jLabel10))));

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("¼�����"));

		jCheckBoxRecord.setText("��ʱ¼��");

		jButtonSetRecord.setText("����");
		jButtonSetRecord.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetRecordActionPerformed(evt);
			}
		});

		jLabel11.setText("Ԥ¼ʱ��");

		jLabel12.setText("¼����ʱ");

		jComboBoxPreRecordTime.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "��Ԥ¼", "5��", "10��", "15��", "20��", "25��", "30��", "��������" }));

		jComboBoxRecordDelay.setModel(
				new javax.swing.DefaultComboBoxModel(new String[] { "5��", "10��", "30��", "1��", "2��", "5��", "10��" }));

		jLabel13.setText("����ʱ��");

		jLabel14.setText("��");

		jTextFieldSaveDays.setText("0");

		jCheckBoxRebundancy.setText("����¼��");

		jCheckBoxAudioRec.setText("��¼��Ƶ");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout
						.createSequentialGroup()
						.addContainerGap().addGroup(jPanel1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout
										.createSequentialGroup().addComponent(jLabel13)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jTextFieldSaveDays, javax.swing.GroupLayout.PREFERRED_SIZE, 29,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18).addComponent(jLabel14).addGap(54, 54, 54)
										.addComponent(jCheckBoxRebundancy).addGap(109, 109, 109)
										.addComponent(jCheckBoxAudioRec))
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(jCheckBoxRecord)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jButtonSetRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 69,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(79, 79, 79).addComponent(jLabel11)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jComboBoxPreRecordTime, javax.swing.GroupLayout.PREFERRED_SIZE,
												95, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(29, 29, 29).addComponent(jLabel12).addGap(18, 18, 18)
										.addComponent(jComboBoxRecordDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 67,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(29, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jCheckBoxRecord).addComponent(jButtonSetRecord).addComponent(jLabel11)
								.addComponent(jLabel12)
								.addComponent(jComboBoxPreRecordTime, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxRecordDelay, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel13)
								.addComponent(jTextFieldSaveDays, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxRebundancy).addComponent(jCheckBoxAudioRec)
								.addComponent(jLabel14))));

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("ͼ���������"));

		jCheckBoxHideArea.setText("�ڸ�");
		jCheckBoxHideArea.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxHideAreaActionPerformed(evt);
			}
		});

		jCheckBoxSignalLost.setText("��Ƶ�źŶ�ʧ����");
		jCheckBoxSignalLost.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxSignalLostActionPerformed(evt);
			}
		});

		jCheckBoxMotion.setText("�ƶ����");
		jCheckBoxMotion.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxMotionActionPerformed(evt);
			}
		});

		jCheckBoxHideAlarm.setText("�ڵ�����");
		jCheckBoxHideAlarm.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBoxHideAlarmActionPerformed(evt);
			}
		});

		jButtonSetHideArea.setText("����");
		jButtonSetHideArea.setEnabled(false);
		jButtonSetHideArea.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetHideAreaActionPerformed(evt);
			}
		});

		jButtonSetSignalLost.setText("����");
		jButtonSetSignalLost.setEnabled(false);
		jButtonSetSignalLost.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetSignalLostActionPerformed(evt);
			}
		});

		jButtonSetMotion.setText("����");
		jButtonSetMotion.setEnabled(false);
		jButtonSetMotion.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetMotionActionPerformed(evt);
			}
		});

		jButtonSetHideAlarm.setText("����");
		jButtonSetHideAlarm.setEnabled(false);
		jButtonSetHideAlarm.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetHideAlarmActionPerformed(evt);
			}
		});

		jCheckBoxOSD.setText("��ʾosd");

		jLabel15.setText("Y����");

		jLabel16.setText("X����");

		jTextFieldOSDX.setText("0");

		jTextFieldOSDY.setText("0");

		jCheckBoxOSDDate.setText("��ʾ����");

		jLabel18.setText("ʱ���ʽ");

		jLabel19.setText("OSD����");

		jLabel20.setText("���ڸ�ʽ");

		jComboBoxOSDFormat
				.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "͸�� ����˸", "��͸�� ��˸", "��͸�� ����˸" }));

		jComboBoxDateFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "XXXX-XX-XX (������)",
				"XX-XX-XXXX (������)", "XXXX��XX��XX��", "XX��XX��XXXX��", "XX-XX-XXXX ������", "XX��XX��XXXX��" }));

		jComboBoxTimeFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24Сʱ��", "12Сʱ��" }));

		jCheckBoxChannelName.setText("ͨ����");

		jLabel21.setText("X����");

		jLabel22.setText("Y����");

		jTextFieldChannekNameX.setText("0");

		jTextFieldChannelNameY.setText("0");

		jButtonString.setText("�����ַ�");
		jButtonString.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonStringActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(
										javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel2Layout.createSequentialGroup().addContainerGap()
												.addComponent(jSeparator1,
														javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
												.addGap(97, 97, 97))
								.addGroup(jPanel2Layout.createSequentialGroup()
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jCheckBoxHideArea).addComponent(jCheckBoxSignalLost))
										.addGap(44, 44, 44)
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jButtonSetSignalLost).addComponent(jButtonSetHideArea))
										.addGap(52, 52, 52)
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(jCheckBoxMotion).addComponent(jCheckBoxHideAlarm))
										.addGap(30, 30, 30)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(jButtonSetMotion)
														.addComponent(jButtonSetHideAlarm)))
								.addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(jLabel15).addGroup(jPanel2Layout.createSequentialGroup()
												.addComponent(jCheckBoxOSD).addGap(18, 18, 18).addComponent(jLabel16)))
										.addGap(18, 18, 18)
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(jTextFieldOSDY, 0, 0, Short.MAX_VALUE)
												.addComponent(jTextFieldOSDX, javax.swing.GroupLayout.DEFAULT_SIZE, 57,
														Short.MAX_VALUE))
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel2Layout.createSequentialGroup().addGap(27, 27, 27)
														.addComponent(jCheckBoxOSDDate))
												.addGroup(jPanel2Layout.createSequentialGroup().addGap(28, 28, 28)
														.addComponent(jLabel18).addGap(18, 18, 18)
														.addComponent(jComboBoxTimeFormat,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGap(26, 26, 26)
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 54,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(jComboBoxOSDFormat, 0,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jComboBoxDateFormat, 0,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGap(87, 87, 87))
								.addGroup(jPanel2Layout.createSequentialGroup().addComponent(jCheckBoxChannelName)
										.addGap(24, 24, 24).addComponent(jLabel21).addGap(15, 15, 15)
										.addComponent(jTextFieldChannekNameX, javax.swing.GroupLayout.PREFERRED_SIZE,
												58, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(24, 24, 24).addComponent(jLabel22).addGap(31, 31, 31)
										.addComponent(jTextFieldChannelNameY, javax.swing.GroupLayout.PREFERRED_SIZE,
												71, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jButtonString)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157,
												Short.MAX_VALUE))
								.addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 574,
										javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap()
						.addGroup(
								jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jCheckBoxMotion).addComponent(jCheckBoxHideArea)
										.addComponent(jButtonSetMotion).addComponent(jButtonSetHideArea))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jCheckBoxHideAlarm).addComponent(jButtonSetHideAlarm)
										.addComponent(jButtonSetSignalLost))
								.addComponent(jCheckBoxSignalLost))
						.addGap(8, 8, 8)
						.addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jCheckBoxOSD)
								.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel16)
										.addComponent(jTextFieldOSDX, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jCheckBoxOSDDate)
										.addComponent(jComboBoxOSDFormat, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel19)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel15)
								.addComponent(jTextFieldOSDY, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxDateFormat, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jComboBoxTimeFormat, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel18).addComponent(jLabel20))
						.addGap(12, 12, 12)
						.addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel21)
								.addComponent(jTextFieldChannekNameX, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonString).addComponent(jLabel22)
								.addComponent(jTextFieldChannelNameY, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxChannelName))
						.addGap(49, 49, 49)));

		javax.swing.GroupLayout jPanelChannelConfigLayout = new javax.swing.GroupLayout(jPanelChannelConfig);
		jPanelChannelConfig.setLayout(jPanelChannelConfigLayout);
		jPanelChannelConfigLayout.setHorizontalGroup(jPanelChannelConfigLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelChannelConfigLayout.createSequentialGroup().addGroup(jPanelChannelConfigLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanelChannelConfigLayout.createSequentialGroup().addGap(19, 19, 19)
								.addComponent(jLabel1).addGap(44, 44, 44)
								.addComponent(jComboBoxChannelNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 105,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(46, 46, 46).addComponent(jLabelChannelName).addGap(18, 18, 18)
								.addComponent(jTextFieldChannelName, javax.swing.GroupLayout.PREFERRED_SIZE, 128,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanelChannelConfigLayout.createSequentialGroup().addContainerGap()
								.addGroup(jPanelChannelConfigLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(jPanelCompressPara, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, 0, 610,
												Short.MAX_VALUE))))
						.addGap(114, 114, 114)));
		jPanelChannelConfigLayout.setVerticalGroup(jPanelChannelConfigLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelChannelConfigLayout.createSequentialGroup().addContainerGap()
						.addGroup(jPanelChannelConfigLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jComboBoxChannelNumber, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel1)
								.addComponent(jTextFieldChannelName, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabelChannelName))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanelCompressPara, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 231,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		getContentPane().add(jPanelChannelConfig);
		jPanelChannelConfig.setBounds(10, 10, 640, 550);

		jButtonSetChanConfig.setText("���õ�ǰͨ������");
		jButtonSetChanConfig.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetChanConfigActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonSetChanConfig);
		jButtonSetChanConfig.setBounds(30, 570, 160, 23);

		jButtonExit.setText("�˳�");
		jButtonExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonExit);
		jButtonExit.setBounds(470, 570, 90, 23);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/*************************************************
	 * ����: " ����ѹ������" ��ť��Ӧ���� ��������: ����ѹ���������ṹ��
	 *************************************************/
	private void jButtonCompressOKActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCompressOKActionPerformed
		int iCompressType = jComboBoxCompressType.getSelectedIndex();
		int dwDefBitRate = 0;
		switch (iCompressType) {
		case 0:
			m_struCompressionCfg.struNormHighRecordPara.byPicQuality = (byte) jComboBoxImageQulity.getSelectedIndex();
			m_struCompressionCfg.struNormHighRecordPara.byStreamType = (byte) jComboBoxStreamType.getSelectedIndex();
			m_struCompressionCfg.struNormHighRecordPara.byResolution = (byte) jComboBoxResolution.getSelectedIndex();
			m_struCompressionCfg.struNormHighRecordPara.byBitrateType = (byte) jComboBoxBitRateType.getSelectedIndex();
			m_struCompressionCfg.struNormHighRecordPara.dwVideoFrameRate = (byte) jComboBoxFrameRate.getSelectedIndex();
			if (jComboBoxMaxBitRate.getSelectedIndex() == 22) {// λ������

				dwDefBitRate = Integer.parseInt(jTextFieldBitRate.getText()) * 1024;
				if (dwDefBitRate < 32 * 1024) {
					dwDefBitRate = 32 * 1024;
				}
				if (dwDefBitRate > 8192 * 1024) {
					dwDefBitRate = 8192 * 1024;
				}
				dwDefBitRate |= 0x80000000;
				m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate = dwDefBitRate;
			} else {
				m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate = jComboBoxMaxBitRate.getSelectedIndex() + 2;
			}
			m_struCompressionCfg.struNormHighRecordPara.wIntervalFrameI = (short) Integer
					.parseInt(jTextFieldIInterval.getText());
			m_struCompressionCfg.struNormHighRecordPara.byIntervalBPFrame = (byte) jComboBoxBpInterval
					.getSelectedIndex();
			break;

		case 1:
			m_struCompressionCfg.struNetPara.byPicQuality = (byte) jComboBoxImageQulity.getSelectedIndex();
			m_struCompressionCfg.struNetPara.byStreamType = (byte) jComboBoxStreamType.getSelectedIndex();
			m_struCompressionCfg.struNetPara.byResolution = (byte) jComboBoxResolution.getSelectedIndex();
			m_struCompressionCfg.struNetPara.byBitrateType = (byte) jComboBoxBitRateType.getSelectedIndex();
			m_struCompressionCfg.struNetPara.dwVideoFrameRate = (byte) jComboBoxFrameRate.getSelectedIndex();
			if (jComboBoxMaxBitRate.getSelectedIndex() == 22)// λ������
			{
				dwDefBitRate = Integer.parseInt(jTextFieldBitRate.getText()) * 1024;
				if (dwDefBitRate < 32 * 1024) {
					dwDefBitRate = 32 * 1024;
				}
				if (dwDefBitRate > 8192 * 1024) {
					dwDefBitRate = 8192 * 1024;
				}
				dwDefBitRate |= 0x80000000;
				m_struCompressionCfg.struNetPara.dwVideoBitrate = dwDefBitRate;
			} else {
				m_struCompressionCfg.struNetPara.dwVideoBitrate = jComboBoxMaxBitRate.getSelectedIndex() + 2;
			}
			m_struCompressionCfg.struNetPara.wIntervalFrameI = (short) Integer.parseInt(jTextFieldIInterval.getText());
			m_struCompressionCfg.struNetPara.byIntervalBPFrame = (byte) jComboBoxBpInterval.getSelectedIndex();
			break;

		case 2:
			m_struCompressionCfg.struEventRecordPara.byPicQuality = (byte) jComboBoxImageQulity.getSelectedIndex();
			m_struCompressionCfg.struEventRecordPara.byStreamType = (byte) jComboBoxStreamType.getSelectedIndex();
			m_struCompressionCfg.struEventRecordPara.byResolution = (byte) jComboBoxResolution.getSelectedIndex();
			m_struCompressionCfg.struEventRecordPara.byBitrateType = (byte) jComboBoxBitRateType.getSelectedIndex();
			m_struCompressionCfg.struEventRecordPara.dwVideoFrameRate = (byte) jComboBoxFrameRate.getSelectedIndex();
			if (jComboBoxMaxBitRate.getSelectedIndex() == 22)// λ������
			{
				dwDefBitRate = Integer.parseInt(jTextFieldBitRate.getText()) * 1024;
				if (dwDefBitRate < 32 * 1024) {
					dwDefBitRate = 32 * 1024;
				}
				if (dwDefBitRate > 8192 * 1024) {
					dwDefBitRate = 8192 * 1024;
				}
				dwDefBitRate |= 0x80000000;
				m_struCompressionCfg.struEventRecordPara.dwVideoBitrate = dwDefBitRate;
			} else {
				m_struCompressionCfg.struEventRecordPara.dwVideoBitrate = jComboBoxMaxBitRate.getSelectedIndex() + 2;
			}
			m_struCompressionCfg.struEventRecordPara.wIntervalFrameI = (short) Integer
					.parseInt(jTextFieldIInterval.getText());
			m_struCompressionCfg.struEventRecordPara.byIntervalBPFrame = (byte) jComboBoxBpInterval.getSelectedIndex();
			break;
		}
	}// GEN-LAST:event_jButtonCompressOKActionPerformed

	/*************************************************
	 * ����: �ڸ� "���� " ��ť��Ӧ���� ��������: �����ڸǱ�������
	 *************************************************/
	private void jButtonSetHideAreaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetHideAreaActionPerformed
		int iChannelNumber = -1;
		int iIndex = jComboBoxChannelNumber.getSelectedIndex();
		if ((iIndex < m_strDeviceInfo.byChanNum) && (iIndex >= 0)) {
			iChannelNumber = iIndex + m_strDeviceInfo.byStartChan;
		} else {
			iChannelNumber = 32 + (iIndex - m_strDeviceInfo.byChanNum) + m_strDeviceInfo.byStartChan;
		}

		JDialogHideArea dlg = new JDialogHideArea(this, false, lUserID, iChannelNumber, m_struPicCfg);
		Vision.centerWindow(dlg);
		dlg.setVisible(true);
	}// GEN-LAST:event_jButtonSetHideAreaActionPerformed

	/*************************************************
	 * ����: �źŶ�ʧ "���� " ��ť��Ӧ���� ��������: �����źŶ�ʧ��������
	 *************************************************/
	private void jButtonSetSignalLostActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetSignalLostActionPerformed
		JDialogVideoInLost dlg = new JDialogVideoInLost(this, false, m_struPicCfg, m_strDeviceInfo);
		Vision.centerWindow(dlg);
		dlg.setVisible(true);
	}// GEN-LAST:event_jButtonSetSignalLostActionPerformed

	/*************************************************
	 * ����: �ƶ���� "���� " ��ť��Ӧ���� ��������: �����ƶ���ⱨ������
	 *************************************************/
	private void jButtonSetMotionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetMotionActionPerformed
		int iChannelNumber = -1;
		int iIndex = jComboBoxChannelNumber.getSelectedIndex();
		if ((iIndex < m_strDeviceInfo.byChanNum) && (iIndex >= 0)) {
			iChannelNumber = iIndex + m_strDeviceInfo.byStartChan;
		} else {
			iChannelNumber = 32 + (iIndex - m_strDeviceInfo.byChanNum) + m_strDeviceInfo.byStartChan;
		}

		JDialogMotionDetect dlg = new JDialogMotionDetect(this, false, lUserID, iChannelNumber, m_struPicCfg,
				m_strDeviceInfo);
		Vision.centerWindow(dlg);
		dlg.setVisible(true);
	}// GEN-LAST:event_jButtonSetMotionActionPerformed

	/*************************************************
	 * ����: �ڵ� "���� " ��ť��Ӧ���� ��������: �����ڵ���������
	 *************************************************/
	private void jButtonSetHideAlarmActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetHideAlarmActionPerformed
		int iChannelNumber = -1;
		int iIndex = jComboBoxChannelNumber.getSelectedIndex();
		if ((iIndex < m_strDeviceInfo.byChanNum) && (iIndex >= 0)) {
			iChannelNumber = iIndex + m_strDeviceInfo.byStartChan;
		} else {
			iChannelNumber = 32 + (iIndex - m_strDeviceInfo.byChanNum) + m_strDeviceInfo.byStartChan;
		}

		JDialogHideAlarm dlg = new JDialogHideAlarm(this, false, lUserID, iChannelNumber, m_struPicCfg,
				m_strDeviceInfo);
		Vision.centerWindow(dlg);
		dlg.setVisible(true);
	}// GEN-LAST:event_jButtonSetHideAlarmActionPerformed

	/*************************************************
	 * ����: "�����ַ� " ��ť��Ӧ���� ��������: �򿪵����ַ����öԻ���,���õ����ַ�����
	 *************************************************/
	private void jButtonStringActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonStringActionPerformed
		int iChannelNumber = this.jComboBoxChannelNumber.getSelectedIndex() + m_strDeviceInfo.byStartChan;
		m_strShowString = new HCNetSDK.NET_DVR_SHOWSTRING_V30();
		IntByReference ibrBytesReturned = new IntByReference(0);// ��ȡ��ʾ�ַ�����
		m_strShowString.write();
		Pointer lpStringConfig = m_strShowString.getPointer();
		boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_SHOWSTRING_V30,
				new NativeLong(iChannelNumber), lpStringConfig, m_strShowString.size(), ibrBytesReturned);
		m_strShowString.read();
		if (getDVRConfigSuc != true) {
			JOptionPane.showMessageDialog(this, "��ȡ��ʾ�ַ�����ʧ��");
		}
		JDialogShowString dialogShowString = new JDialogShowString(this, true, m_strShowString, lUserID,
				iChannelNumber);// ��ģʽ�Ի���
		dialogShowString.setSize(330, 300);
		Vision.centerWindow(dialogShowString);
		dialogShowString.jComboBoxShowArea.setSelectedIndex(0);
		dialogShowString.showStringConfig();
		dialogShowString.setVisible(true);
	}// GEN-LAST:event_jButtonStringActionPerformed

	/*************************************************
	 * ����: "���õ�ǰͨ������ " ��ť��Ӧ���� ��������: ���õ�ǰͨ������
	 *************************************************/
	private void jButtonSetChanConfigActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetChanConfigActionPerformed
		int iChannelNumber = -1;
		int iIndex = jComboBoxChannelNumber.getSelectedIndex();
		if ((iIndex < m_strDeviceInfo.byChanNum) && (iIndex >= 0)) {
			iChannelNumber = iIndex + m_strDeviceInfo.byStartChan;
		} else {
			iChannelNumber = 32 + (iIndex - m_strDeviceInfo.byChanNum) + m_strDeviceInfo.byStartChan;
		}
		// ������ֵ
		m_struPicCfg.sChanName = (jTextFieldChannelName.getText() + "\0").getBytes(); // ͨ������

		// ѹ��������ѹ���������б���

		// ¼�����
		m_struRemoteRecCfg.dwRecord = (jCheckBoxRecord.isSelected() == true) ? 1 : 0;
		m_struRemoteRecCfg.dwPreRecordTime = jComboBoxPreRecordTime.getSelectedIndex();
		m_struRemoteRecCfg.dwRecordTime = jComboBoxRecordDelay.getSelectedIndex();
		m_struRemoteRecCfg.dwRecorderDuration = Integer.parseInt(jTextFieldSaveDays.getText());
		m_struRemoteRecCfg.byRedundancyRec = (byte) ((jCheckBoxRebundancy.isSelected() == true) ? 1 : 0);
		m_struRemoteRecCfg.byAudioRec = (byte) ((jCheckBoxAudioRec.isSelected() == true) ? 1 : 0);

		// ͼ�����
		m_struPicCfg.dwEnableHide = (jCheckBoxHideArea.isSelected() == true) ? 1 : 0;
		m_struPicCfg.struMotion.byEnableHandleMotion = (byte) ((jCheckBoxMotion.isSelected() == true) ? 1 : 0);
		m_struPicCfg.struVILost.byEnableHandleVILost = (byte) ((jCheckBoxSignalLost.isSelected() == true) ? 1 : 0);
		m_struPicCfg.struHideAlarm.dwEnableHideAlarm = (jCheckBoxHideAlarm.isSelected() == true) ? 1 : 0;
		m_struPicCfg.dwShowOsd = (jCheckBoxOSD.isSelected() == true) ? 1 : 0;
		m_struPicCfg.wOSDTopLeftX = (short) Integer.parseInt(jTextFieldOSDX.getText());
		m_struPicCfg.wOSDTopLeftY = (short) Integer.parseInt(jTextFieldOSDY.getText());
		m_struPicCfg.byDispWeek = (byte) ((jCheckBoxOSDDate.isSelected() == true) ? 1 : 0);

		m_struPicCfg.byHourOSDType = (byte) jComboBoxTimeFormat.getSelectedIndex();
		m_struPicCfg.byOSDAttrib = (byte) (jComboBoxOSDFormat.getSelectedIndex() + 2);
		m_struPicCfg.byOSDType = (byte) jComboBoxDateFormat.getSelectedIndex();

		m_struPicCfg.dwShowChanName = (jCheckBoxChannelName.isSelected() == true) ? 1 : 0;
		m_struPicCfg.wShowNameTopLeftX = (short) Integer.parseInt(jTextFieldChannekNameX.getText());
		m_struPicCfg.wShowNameTopLeftY = (short) Integer.parseInt(jTextFieldChannelNameY.getText());

		m_struPicCfg.write();
		Pointer lpPicConfig = m_struPicCfg.getPointer();
		boolean setDVRConfigSuc = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_PICCFG_V30,
				new NativeLong(iChannelNumber), lpPicConfig, m_struPicCfg.size());
		m_struPicCfg.read();
		if (setDVRConfigSuc != true) {
			JOptionPane.showMessageDialog(this, "����ͼƬ����ʧ��");
			return;
		}

		setDVRConfigSuc = false;
		m_struCompressionCfg.write();
		lpPicConfig = m_struCompressionCfg.getPointer();
		setDVRConfigSuc = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_COMPRESSCFG_V30,
				new NativeLong(iChannelNumber), lpPicConfig, m_struCompressionCfg.size());
		m_struCompressionCfg.read();
		if (setDVRConfigSuc != true) {
			JOptionPane.showMessageDialog(this, "����ѹ������ʧ��");
			System.out.println(hCNetSDK.NET_DVR_GetLastError());
			return;
		}

		setDVRConfigSuc = false;
		m_struRemoteRecCfg.write();
		lpPicConfig = m_struRemoteRecCfg.getPointer();
		setDVRConfigSuc = hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_RECORDCFG_V30,
				new NativeLong(iChannelNumber), lpPicConfig, m_struRemoteRecCfg.size());
		m_struRemoteRecCfg.read();
		if (setDVRConfigSuc != true) {
			System.out.println(hCNetSDK.NET_DVR_GetLastError());
			JOptionPane.showMessageDialog(this, "����¼�����ʧ��");
			return;
		}
		JOptionPane.showMessageDialog(this, "����ͨ�������ɹ�");
	}// GEN-LAST:event_jButtonSetChanConfigActionPerformed

	/*************************************************
	 * ����: "�˳� " ��ť��Ӧ���� ��������: ���ٶԻ���
	 *************************************************/
	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonExitActionPerformed
		this.dispose();
	}// GEN-LAST:event_jButtonExitActionPerformed

	/*************************************************
	 * ����: "ͨ���� "��Ͽ� ѡ��ı��¼���Ӧ���� ��������: ��ȡ����ʾͨ������ͨ������Ӧ��ͨ������
	 *************************************************/
	private void jComboBoxChannelNumberActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxChannelNumberActionPerformed
		showChannelConfig();
	}// GEN-LAST:event_jComboBoxChannelNumberActionPerformed

	/*************************************************
	 * ����: "ѹ���������� "��Ͽ� ѡ��ı��¼���Ӧ���� ��������: ��ȡ����ʾѹ���������Ͷ�Ӧ�Ĳ���
	 *************************************************/
	private void jComboBoxCompressTypeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jComboBoxCompressTypeActionPerformed
		int iCompressType = jComboBoxCompressType.getSelectedIndex();
		switch (iCompressType) {
		case 0:
			jComboBoxImageQulity.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byPicQuality);
			jComboBoxStreamType.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byStreamType);
			jComboBoxResolution.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byResolution);
			jComboBoxBitRateType.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byBitrateType);
			jComboBoxFrameRate.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.dwVideoFrameRate);
			if (((m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate >> 31) & 0x01) == 1)// λ������
			{
				jComboBoxMaxBitRate.setSelectedIndex(22);
				jTextFieldBitRate
						.setText((m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate & 0x7fffffff) / 1024 + "");
			} else {
				jComboBoxMaxBitRate.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate - 2);
				jTextFieldBitRate.setText("");
			}
			jTextFieldIInterval.setText(m_struCompressionCfg.struNormHighRecordPara.wIntervalFrameI + "");
			jComboBoxBpInterval.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byIntervalBPFrame);
			break;

		case 1:
			jComboBoxImageQulity.setSelectedIndex(m_struCompressionCfg.struNetPara.byPicQuality);
			jComboBoxStreamType.setSelectedIndex(m_struCompressionCfg.struNetPara.byStreamType);
			jComboBoxResolution.setSelectedIndex(m_struCompressionCfg.struNetPara.byResolution);
			jComboBoxBitRateType.setSelectedIndex(m_struCompressionCfg.struNetPara.byBitrateType);
			jComboBoxFrameRate.setSelectedIndex(m_struCompressionCfg.struNetPara.dwVideoFrameRate);
			if (((m_struCompressionCfg.struNetPara.dwVideoBitrate >> 31) & 0x01) == 1)// λ������
			{
				jComboBoxMaxBitRate.setSelectedIndex(22);
				jTextFieldBitRate.setText((m_struCompressionCfg.struNetPara.dwVideoBitrate & 0x7fffffff) / 1024 + "");
			} else {
				jComboBoxMaxBitRate.setSelectedIndex(m_struCompressionCfg.struNetPara.dwVideoBitrate - 2);
				jTextFieldBitRate.setText("");
			}
			jTextFieldIInterval.setText(m_struCompressionCfg.struNetPara.wIntervalFrameI + "");
			jComboBoxBpInterval.setSelectedIndex(m_struCompressionCfg.struNetPara.byIntervalBPFrame);
			break;

		case 2:
			jComboBoxImageQulity.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.byPicQuality);
			jComboBoxStreamType.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.byStreamType);
			jComboBoxResolution.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.byResolution);
			jComboBoxBitRateType.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.byBitrateType);
			jComboBoxFrameRate.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.dwVideoFrameRate);
			if (((m_struCompressionCfg.struEventRecordPara.dwVideoBitrate >> 31) & 0x01) == 1)// λ������
			{
				jComboBoxMaxBitRate.setSelectedIndex(22);
				jTextFieldBitRate
						.setText((m_struCompressionCfg.struEventRecordPara.dwVideoBitrate & 0x7fffffff) / 1024 + "");
			} else {
				jComboBoxMaxBitRate.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.dwVideoBitrate - 2);
				jTextFieldBitRate.setText("");
			}
			jTextFieldIInterval.setText(m_struCompressionCfg.struEventRecordPara.wIntervalFrameI + "");
			jComboBoxBpInterval.setSelectedIndex(m_struCompressionCfg.struEventRecordPara.byIntervalBPFrame);
			break;
		}
	}// GEN-LAST:event_jComboBoxCompressTypeActionPerformed

	/*************************************************
	 * ����: ��ʱ¼�� "����" ��ť������Ӧ���� ��������: �����Ի�����ʾ������¼�����
	 *************************************************/
	private void jButtonSetRecordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetRecordActionPerformed
		JDialogRecordSchedule dialogRecordSchedule = new JDialogRecordSchedule(this, true, m_struRemoteRecCfg);// ��ģʽ�Ի���
		dialogRecordSchedule.setSize(490, 435);
		Vision.centerWindow(dialogRecordSchedule);
		dialogRecordSchedule.jComboBoxWeekDay.setSelectedIndex(0);

		dialogRecordSchedule.showRecordSchedule();
		dialogRecordSchedule.setVisible(true);
	}// GEN-LAST:event_jButtonSetRecordActionPerformed

	/*************************************************
	 * ����: "�ڸ�" checkbox ѡ��ı��¼���Ӧ���� ��������: ���ð�ťenable����
	 *************************************************/
	private void jCheckBoxHideAreaActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jCheckBoxHideAreaActionPerformed
	{// GEN-HEADEREND:event_jCheckBoxHideAreaActionPerformed
		jButtonSetHideArea.setEnabled(jCheckBoxHideArea.isSelected());
	}// GEN-LAST:event_jCheckBoxHideAreaActionPerformed

	/*************************************************
	 * ����: "�ƶ����" checkbox ѡ��ı��¼���Ӧ���� ��������: ���ð�ťenable����
	 *************************************************/
	private void jCheckBoxMotionActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jCheckBoxMotionActionPerformed
	{// GEN-HEADEREND:event_jCheckBoxMotionActionPerformed
		jButtonSetMotion.setEnabled(jCheckBoxMotion.isSelected());
	}// GEN-LAST:event_jCheckBoxMotionActionPerformed

	/*************************************************
	 * ����: "��Ƶ�źŶ�ʧ����" checkbox ѡ��ı��¼���Ӧ���� ��������: ���ð�ťenable����
	 *************************************************/
	private void jCheckBoxSignalLostActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jCheckBoxSignalLostActionPerformed
	{// GEN-HEADEREND:event_jCheckBoxSignalLostActionPerformed
		jButtonSetSignalLost.setEnabled(jCheckBoxSignalLost.isSelected());
	}// GEN-LAST:event_jCheckBoxSignalLostActionPerformed

	/*************************************************
	 * ����: "�ڵ�����" checkbox ѡ��ı��¼���Ӧ���� ��������: ���ð�ťenable����
	 *************************************************/
	private void jCheckBoxHideAlarmActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_jCheckBoxHideAlarmActionPerformed
	{// GEN-HEADEREND:event_jCheckBoxHideAlarmActionPerformed
		jButtonSetHideAlarm.setEnabled(jCheckBoxHideAlarm.isSelected());
	}// GEN-LAST:event_jCheckBoxHideAlarmActionPerformed

	/*************************************************
	 * ����: showChannelConfig ��������: ��ȡͨ����������ʾ
	 *************************************************/
	public void showChannelConfig() {
		int iChannelNumber = -1;
		int iIndex = jComboBoxChannelNumber.getSelectedIndex();
		if ((iIndex < m_strDeviceInfo.byChanNum) && (iIndex >= 0)) {
			iChannelNumber = iIndex + m_strDeviceInfo.byStartChan;
		} else {
			iChannelNumber = 32 + (iIndex - m_strDeviceInfo.byChanNum) + m_strDeviceInfo.byStartChan;
		}
		IntByReference ibrBytesReturned = new IntByReference(0);// ��ȡͼƬ����
		m_struPicCfg = new HCNetSDK.NET_DVR_PICCFG_V30();
		m_struPicCfg.write();
		Pointer lpPicConfig = m_struPicCfg.getPointer();
		boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_PICCFG_V30,
				new NativeLong(iChannelNumber), lpPicConfig, m_struPicCfg.size(), ibrBytesReturned);
		m_struPicCfg.read();
		if (getDVRConfigSuc != true) {
			JOptionPane.showMessageDialog(this, "��ȡͼƬ����ʧ��");
			return;
		}

		ibrBytesReturned = new IntByReference(0);// ��ȡѹ������
		getDVRConfigSuc = false;
		m_struCompressionCfg = new HCNetSDK.NET_DVR_COMPRESSIONCFG_V30();
		m_struCompressionCfg.write();
		lpPicConfig = m_struCompressionCfg.getPointer();
		getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_COMPRESSCFG_V30,
				new NativeLong(iChannelNumber), lpPicConfig, m_struCompressionCfg.size(), ibrBytesReturned);
		m_struCompressionCfg.read();
		if (getDVRConfigSuc != true) {
			JOptionPane.showMessageDialog(null, "��ȡѹ������ʧ��");
			return;
		}

		ibrBytesReturned = new IntByReference(0);// ��ȡ¼�����
		getDVRConfigSuc = false;
		m_struRemoteRecCfg = new HCNetSDK.NET_DVR_RECORD_V30();
		m_struRemoteRecCfg.write();
		lpPicConfig = m_struRemoteRecCfg.getPointer();
		getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_RECORDCFG_V30,
				new NativeLong(iChannelNumber), lpPicConfig, m_struRemoteRecCfg.size(), ibrBytesReturned);
		m_struRemoteRecCfg.read();
		if (getDVRConfigSuc != true) {
			System.out.println(hCNetSDK.NET_DVR_GetLastError());
			JOptionPane.showMessageDialog(this, "��ȡ¼�����ʧ��");
			return;
		}

		// ��ʾ����
		String[] sName = new String[2];
		sName = new String(m_struPicCfg.sChanName).split("\0", 2);
		jTextFieldChannelName.setText(sName[0]);
		// ѹ������
		jComboBoxCompressType.setSelectedIndex(0);
		jComboBoxImageQulity.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byPicQuality);
		jComboBoxStreamType.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byStreamType);
		jComboBoxResolution.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byResolution);
		jComboBoxBitRateType.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byBitrateType);
		jComboBoxFrameRate.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.dwVideoFrameRate);
		if (((m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate >> 31) & 0x01) == 1)// λ������
		{
			jComboBoxMaxBitRate.setSelectedIndex(22);
			jTextFieldBitRate
					.setText((m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate & 0x7fffffff) / 1024 + "");
		} else {
			jComboBoxMaxBitRate.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.dwVideoBitrate - 2);
			jTextFieldBitRate.setText("");
		}
		jTextFieldIInterval.setText(m_struCompressionCfg.struNormHighRecordPara.wIntervalFrameI + "");
		jComboBoxBpInterval.setSelectedIndex(m_struCompressionCfg.struNormHighRecordPara.byIntervalBPFrame);

		// ¼�����
		jCheckBoxRecord.setSelected((m_struRemoteRecCfg.dwRecord > 0) ? true : false);
		jComboBoxPreRecordTime.setSelectedIndex(m_struRemoteRecCfg.dwPreRecordTime);
		jComboBoxRecordDelay.setSelectedIndex(m_struRemoteRecCfg.dwRecordTime);
		jTextFieldSaveDays.setText(m_struRemoteRecCfg.dwRecorderDuration + "");
		jCheckBoxRebundancy.setSelected((m_struRemoteRecCfg.byRedundancyRec > 0) ? true : false);
		jCheckBoxAudioRec.setSelected((m_struRemoteRecCfg.byAudioRec > 0) ? true : false);

		// ͼ�����
		jCheckBoxHideArea.setSelected((m_struPicCfg.dwEnableHide > 0) ? true : false);
		jCheckBoxMotion.setSelected((m_struPicCfg.struMotion.byEnableHandleMotion > 0) ? true : false);
		jCheckBoxSignalLost.setSelected((m_struPicCfg.struVILost.byEnableHandleVILost > 0) ? true : false);
		jCheckBoxHideAlarm.setSelected((m_struPicCfg.struHideAlarm.dwEnableHideAlarm > 0) ? true : false);
		jCheckBoxOSD.setSelected((m_struPicCfg.dwShowOsd > 0) ? true : false);
		jTextFieldOSDX.setText(m_struPicCfg.wOSDTopLeftX + "");
		jTextFieldOSDY.setText(m_struPicCfg.wOSDTopLeftY + "");
		jCheckBoxOSDDate.setSelected((m_struPicCfg.byDispWeek > 0) ? true : false);
		jComboBoxTimeFormat.setSelectedIndex(m_struPicCfg.byHourOSDType);
		jComboBoxOSDFormat.setSelectedIndex(m_struPicCfg.byOSDAttrib - 2);
		jComboBoxDateFormat.setSelectedIndex(m_struPicCfg.byOSDType);
		jCheckBoxChannelName.setSelected((m_struPicCfg.dwShowChanName > 0) ? true : false);
		jTextFieldChannekNameX.setText(m_struPicCfg.wShowNameTopLeftX + "");
		jTextFieldChannelNameY.setText(m_struPicCfg.wShowNameTopLeftY + "");

		// �����ӽṹ���ڵ�����ť�Ŀɱ༭��
		jButtonSetHideArea.setEnabled(jCheckBoxHideArea.isSelected());
		jButtonSetMotion.setEnabled(jCheckBoxMotion.isSelected());
		jButtonSetSignalLost.setEnabled(jCheckBoxSignalLost.isSelected());
		jButtonSetHideAlarm.setEnabled(jCheckBoxHideAlarm.isSelected());
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButtonCompressOK;
	private javax.swing.JButton jButtonExit;
	private javax.swing.JButton jButtonSetChanConfig;
	public javax.swing.JButton jButtonSetHideAlarm;
	public javax.swing.JButton jButtonSetHideArea;
	public javax.swing.JButton jButtonSetMotion;
	private javax.swing.JButton jButtonSetRecord;
	public javax.swing.JButton jButtonSetSignalLost;
	private javax.swing.JButton jButtonString;
	public javax.swing.JCheckBox jCheckBoxAudioRec;
	public javax.swing.JCheckBox jCheckBoxChannelName;
	public javax.swing.JCheckBox jCheckBoxHideAlarm;
	public javax.swing.JCheckBox jCheckBoxHideArea;
	public javax.swing.JCheckBox jCheckBoxMotion;
	public javax.swing.JCheckBox jCheckBoxOSD;
	public javax.swing.JCheckBox jCheckBoxOSDDate;
	public javax.swing.JCheckBox jCheckBoxRebundancy;
	public javax.swing.JCheckBox jCheckBoxRecord;
	public javax.swing.JCheckBox jCheckBoxSignalLost;
	public javax.swing.JComboBox jComboBoxBitRateType;
	public javax.swing.JComboBox jComboBoxBpInterval;
	public javax.swing.JComboBox jComboBoxChannelNumber;
	public javax.swing.JComboBox jComboBoxCompressType;
	public javax.swing.JComboBox jComboBoxDateFormat;
	public javax.swing.JComboBox jComboBoxFrameRate;
	public javax.swing.JComboBox jComboBoxImageQulity;
	public javax.swing.JComboBox jComboBoxMaxBitRate;
	public javax.swing.JComboBox jComboBoxOSDFormat;
	public javax.swing.JComboBox jComboBoxPreRecordTime;
	public javax.swing.JComboBox jComboBoxRecordDelay;
	public javax.swing.JComboBox jComboBoxResolution;
	public javax.swing.JComboBox jComboBoxStreamType;
	public javax.swing.JComboBox jComboBoxTimeFormat;
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
	private javax.swing.JLabel jLabel21;
	private javax.swing.JLabel jLabel22;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JLabel jLabelChannelName;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanelChannelConfig;
	private javax.swing.JPanel jPanelCompressPara;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	public javax.swing.JTextField jTextFieldBitRate;
	public javax.swing.JTextField jTextFieldChannekNameX;
	public javax.swing.JTextField jTextFieldChannelName;
	public javax.swing.JTextField jTextFieldChannelNameY;
	public javax.swing.JTextField jTextFieldIInterval;
	public javax.swing.JTextField jTextFieldOSDX;
	public javax.swing.JTextField jTextFieldOSDY;
	public javax.swing.JTextField jTextFieldSaveDays;
	// End of variables declaration//GEN-END:variables
}
