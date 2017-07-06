package vision;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import javax.swing.JOptionPane;

/**
 * �����������
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
class JFrameNetWorkConfig extends javax.swing.JFrame {

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private HCNetSDK.NET_DVR_NETCFG_V30 m_strNetConfig;// �������
	private NativeLong m_lUserID;// �û����

	public JFrameNetWorkConfig(NativeLong lUserID) {
		m_lUserID = lUserID;// ���������ڴ������û�ID
		initComponents();
		initDialog();
	}

	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		jLabel14 = new javax.swing.JLabel();
		jLabel15 = new javax.swing.JLabel();
		jComboBoxAdapter = new javax.swing.JComboBox();
		jRadioButtonGetIPAddress = new javax.swing.JRadioButton();
		jTextFieldIPAddress = new javax.swing.JTextField();
		jTextFieldSubMask = new javax.swing.JTextField();
		jTextFieldGatewayAddress = new javax.swing.JTextField();
		jTextFieldPreDNS = new javax.swing.JTextField();
		jTextFieldAltDNS = new javax.swing.JTextField();
		jTextFieldPhyAddress = new javax.swing.JTextField();
		jTextFieldPrivateDomain = new javax.swing.JTextField();
		jTextFieldComPort = new javax.swing.JTextField();
		jTextFieldHTTPPort = new javax.swing.JTextField();
		jTextField1MultiAddress = new javax.swing.JTextField();
		jTextField1AlarmAddress = new javax.swing.JTextField();
		jTextField1AlarmPort = new javax.swing.JTextField();
		jTextField1MTU = new javax.swing.JTextField();
		jButtonSetup = new javax.swing.JButton();
		jButtonExit = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("�����������");
		getContentPane().setLayout(null);

		jLabel1.setText("��������");
		jLabel1.setAutoscrolls(true);
		jLabel1.setFocusTraversalPolicyProvider(true);
		getContentPane().add(jLabel1);
		jLabel1.setBounds(30, 40, 90, 15);

		jLabel2.setText("�Զ����IP");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(30, 70, 90, 15);

		jLabel3.setText("IP��ַ");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(30, 100, 90, 15);

		jLabel4.setText("IP��ַ����");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(30, 130, 90, 15);

		jLabel5.setText("���ص�ַ");
		getContentPane().add(jLabel5);
		jLabel5.setBounds(30, 160, 90, 15);

		jLabel6.setText("��ѡDNS��ַ");
		getContentPane().add(jLabel6);
		jLabel6.setBounds(30, 190, 100, 15);

		jLabel7.setText("��ѡDNS��ַ");
		getContentPane().add(jLabel7);
		jLabel7.setBounds(30, 220, 100, 15);

		jLabel8.setText("�����ַ");
		getContentPane().add(jLabel8);
		jLabel8.setBounds(30, 250, 90, 15);

		jLabel9.setText("˽��������ַ");
		jLabel9.setAutoscrolls(true);
		jLabel9.setFocusTraversalPolicyProvider(true);
		getContentPane().add(jLabel9);
		jLabel9.setBounds(270, 40, 100, 15);

		jLabel10.setText("�豸ͨѶ�˿ں�");
		jLabel10.setAutoscrolls(true);
		jLabel10.setFocusTraversalPolicyProvider(true);
		getContentPane().add(jLabel10);
		jLabel10.setBounds(270, 70, 110, 15);

		jLabel11.setText("HTTP�˿ں�");
		jLabel11.setAutoscrolls(true);
		jLabel11.setFocusTraversalPolicyProvider(true);
		getContentPane().add(jLabel11);
		jLabel11.setBounds(270, 100, 100, 15);

		jLabel12.setText("�ಥ���ַ");
		getContentPane().add(jLabel12);
		jLabel12.setBounds(270, 130, 100, 15);

		jLabel13.setText("�澯����������ַ");
		getContentPane().add(jLabel13);
		jLabel13.setBounds(270, 160, 110, 15);

		jLabel14.setText("�澯���������˿�");
		getContentPane().add(jLabel14);
		jLabel14.setBounds(270, 190, 120, 15);

		jLabel15.setText("MTU");
		getContentPane().add(jLabel15);
		jLabel15.setBounds(270, 220, 90, 15);

		jComboBoxAdapter.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "10MBase-T", "10MBase-Tȫ˫��", "100MBase-TX", "4-100Mȫ˫��", "10M/100M����Ӧ" }));
		jComboBoxAdapter.setAutoscrolls(true);
		jComboBoxAdapter.setBorder(null);
		jComboBoxAdapter.setFocusTraversalPolicyProvider(true);
		getContentPane().add(jComboBoxAdapter);
		jComboBoxAdapter.setBounds(120, 40, 140, 19);
		getContentPane().add(jRadioButtonGetIPAddress);
		jRadioButtonGetIPAddress.setBounds(140, 70, 21, 21);
		getContentPane().add(jTextFieldIPAddress);
		jTextFieldIPAddress.setBounds(140, 100, 110, 21);
		getContentPane().add(jTextFieldSubMask);
		jTextFieldSubMask.setBounds(140, 130, 110, 21);
		getContentPane().add(jTextFieldGatewayAddress);
		jTextFieldGatewayAddress.setBounds(140, 160, 110, 21);
		getContentPane().add(jTextFieldPreDNS);
		jTextFieldPreDNS.setBounds(140, 190, 110, 21);
		getContentPane().add(jTextFieldAltDNS);
		jTextFieldAltDNS.setBounds(140, 220, 110, 21);
		getContentPane().add(jTextFieldPhyAddress);
		jTextFieldPhyAddress.setBounds(140, 250, 110, 21);
		getContentPane().add(jTextFieldPrivateDomain);
		jTextFieldPrivateDomain.setBounds(390, 40, 110, 21);
		getContentPane().add(jTextFieldComPort);
		jTextFieldComPort.setBounds(390, 70, 110, 21);
		getContentPane().add(jTextFieldHTTPPort);
		jTextFieldHTTPPort.setBounds(390, 100, 110, 21);
		getContentPane().add(jTextField1MultiAddress);
		jTextField1MultiAddress.setBounds(390, 130, 110, 21);
		getContentPane().add(jTextField1AlarmAddress);
		jTextField1AlarmAddress.setBounds(390, 160, 110, 21);
		getContentPane().add(jTextField1AlarmPort);
		jTextField1AlarmPort.setBounds(390, 190, 110, 21);
		getContentPane().add(jTextField1MTU);
		jTextField1MTU.setBounds(390, 220, 110, 20);

		jButtonSetup.setText("����");
		jButtonSetup.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetupActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonSetup);
		jButtonSetup.setBounds(30, 300, 70, 23);

		jButtonExit.setText(" �˳�");
		jButtonExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonExit);
		jButtonExit.setBounds(120, 300, 80, 23);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/*************************************************
	 * ����: "����" ��ť������Ӧ���� ��������: �����������
	 *************************************************/
	private void jButtonSetupActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetupActionPerformed
		m_strNetConfig.struEtherNet[0].struDVRIP.sIpV4 = jTextFieldIPAddress.getText().getBytes();// IP��ַ
		m_strNetConfig.struEtherNet[0].struDVRIPMask.sIpV4 = jTextFieldSubMask.getText().getBytes();// ��������
		m_strNetConfig.struGatewayIpAddr.sIpV4 = jTextFieldGatewayAddress.getText().getBytes();// ����
		m_strNetConfig.struAlarmHostIpAddr.sIpV4 = jTextField1AlarmAddress.getText().getBytes();// ������������
		m_strNetConfig.struDnsServer1IpAddr.sIpV4 = jTextFieldPreDNS.getText().getBytes();// ��ѡDNS������
		m_strNetConfig.struDnsServer2IpAddr.sIpV4 = jTextFieldAltDNS.getText().getBytes();// ��ѡDNS������
		m_strNetConfig.struMulticastIpAddr.sIpV4 = jTextField1MultiAddress.getText().getBytes(); // �ಥ���ַ

		m_strNetConfig.wAlarmHostIpPort = (short) Integer.parseInt(jTextField1AlarmPort.getText());// �������������˿�
		m_strNetConfig.wHttpPortNo = (short) Integer.parseInt(jTextFieldHTTPPort.getText());// Http�˿�
		m_strNetConfig.struEtherNet[0].wMTU = (short) Integer.parseInt(jTextField1MTU.getText()); // MTU
		m_strNetConfig.struEtherNet[0].wDVRPort = (short) Integer.parseInt(jTextFieldComPort.getText());// �豸ͨѶ�˿ں�
		if (jRadioButtonGetIPAddress.isSelected()) {
			m_strNetConfig.byUseDhcp = 1;
		} else {
			m_strNetConfig.byUseDhcp = 0;
		}
		m_strNetConfig.struEtherNet[0].dwNetInterface = jComboBoxAdapter.getSelectedIndex() + 1;
		m_strNetConfig.write();
		Pointer lpNetConfig = m_strNetConfig.getPointer();
		boolean setDVRConfigSuc = hCNetSDK.NET_DVR_SetDVRConfig(m_lUserID, HCNetSDK.NET_DVR_SET_NETCFG_V30,
				new NativeLong(0), lpNetConfig, m_strNetConfig.size());
		m_strNetConfig.read();
		if (setDVRConfigSuc == false) {
			JOptionPane.showMessageDialog(this, "�����������ʧ��");
			System.out.print("" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			JOptionPane.showMessageDialog(this, "������������ɹ�");
		}
	}// GEN-LAST:event_jButtonSetupActionPerformed

	/*************************************************
	 * ����: initDialog ��������: ��ʼ���Ի���,��ʾ�������
	 *************************************************/
	private void initDialog() {
		m_strNetConfig = new HCNetSDK.NET_DVR_NETCFG_V30();
		IntByReference ibrBytesReturned = new IntByReference(0);
		m_strNetConfig.write();
		Pointer lpNetConfig = m_strNetConfig.getPointer();
		boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(m_lUserID, HCNetSDK.NET_DVR_GET_NETCFG_V30,
				new NativeLong(0), lpNetConfig, m_strNetConfig.size(), ibrBytesReturned);
		m_strNetConfig.read();
		if (getDVRConfigSuc != true) {
			JOptionPane.showMessageDialog(this, "��ȡ�������ʧ��");
			return;
		}

		// ��Ϣ��ʾ
		jTextFieldIPAddress.setText(new String(m_strNetConfig.struEtherNet[0].struDVRIP.sIpV4));// IP��ַ
		jTextFieldSubMask.setText(new String(m_strNetConfig.struEtherNet[0].struDVRIPMask.sIpV4));// ��������
		jTextFieldGatewayAddress.setText(new String(m_strNetConfig.struGatewayIpAddr.sIpV4));// ����
		jTextField1AlarmAddress.setText(new String(m_strNetConfig.struAlarmHostIpAddr.sIpV4));// ������������
		jTextFieldPreDNS.setText(new String(m_strNetConfig.struDnsServer1IpAddr.sIpV4));// ��ѡDNS������
		jTextFieldAltDNS.setText(new String(m_strNetConfig.struDnsServer2IpAddr.sIpV4));// ��ѡDNS������
		jTextField1MultiAddress.setText(new String(m_strNetConfig.struMulticastIpAddr.sIpV4));// �ಥ���ַ
		int iTemp = m_strNetConfig.wAlarmHostIpPort; // �������������˿�
		String MyString = "" + iTemp;
		jTextField1AlarmPort.setText(MyString);
		iTemp = m_strNetConfig.wHttpPortNo; // HTTP�˿�
		MyString = "" + iTemp;
		jTextFieldHTTPPort.setText(MyString);
		iTemp = m_strNetConfig.struEtherNet[0].wMTU; // MTU����䵥Ԫ
		MyString = "" + iTemp;
		jTextField1MTU.setText(MyString);
		iTemp = m_strNetConfig.struEtherNet[0].wDVRPort; // MTU����䵥Ԫ
		MyString = "" + iTemp;
		jTextFieldComPort.setText(MyString); // ͨ�Ŷ˿�
		String sPhyAddress = "";
		for (int i = 0; i < 6; i++) {
			iTemp = m_strNetConfig.struEtherNet[0].byMACAddr[i];
			if (iTemp < 0) {
				iTemp += 256;
			}
			if (i != 5) {
				sPhyAddress = sPhyAddress + Integer.toHexString(iTemp) + ".";
			} else {
				sPhyAddress = sPhyAddress + Integer.toHexString(iTemp);
			}
		}
		jTextFieldPhyAddress.setText(sPhyAddress);// �����ַ
		jTextFieldPhyAddress.setEnabled(false);
		boolean b = m_strNetConfig.byUseDhcp > 0;
		jRadioButtonGetIPAddress.setSelected(b);// �Ƿ��Զ���ȡIP��ַ
		jRadioButtonGetIPAddress.setEnabled(false);
		jComboBoxAdapter.setSelectedIndex(m_strNetConfig.struEtherNet[0].dwNetInterface - 1);
	}

	/*************************************************
	 * ����: "�˳�" ��ť������Ӧ���� ��������: ���ٴ���
	 *************************************************/
	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonExitActionPerformed
		this.dispose();
	}// GEN-LAST:event_jButtonExitActionPerformed

	private javax.swing.JButton jButtonExit;
	private javax.swing.JButton jButtonSetup;
	javax.swing.JComboBox jComboBoxAdapter;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JRadioButton jRadioButtonGetIPAddress;
	private javax.swing.JTextField jTextField1AlarmAddress;
	private javax.swing.JTextField jTextField1AlarmPort;
	private javax.swing.JTextField jTextField1MTU;
	private javax.swing.JTextField jTextField1MultiAddress;
	private javax.swing.JTextField jTextFieldAltDNS;
	private javax.swing.JTextField jTextFieldComPort;
	private javax.swing.JTextField jTextFieldGatewayAddress;
	private javax.swing.JTextField jTextFieldHTTPPort;
	private javax.swing.JTextField jTextFieldIPAddress;
	private javax.swing.JTextField jTextFieldPhyAddress;
	private javax.swing.JTextField jTextFieldPreDNS;
	private javax.swing.JTextField jTextFieldPrivateDomain;
	private javax.swing.JTextField jTextFieldSubMask;
}
