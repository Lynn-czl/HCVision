package vision;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * �豸״̬��ʾ
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class JDialogDeviceState extends javax.swing.JDialog {

	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private NativeLong m_lUserID;// �û����
	private HCNetSDK.NET_DVR_WORKSTATE_V30 m_strWorkState;
	private HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;
	private HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;
	private boolean[] bChannelEnabled;// ������ȡ��Ӧͨ���ŵ�ͨ���Ƿ���Ч
	private int iTotlaLink;// �����Ӹ���
	private String m_sDeviceIP;

	/**
	 * ���캯�� Creates new form JDialogDeviceState
	 * 
	 * @param parent
	 * @param modal
	 * @param lUserID
	 * @param strDeviceInfo
	 * @param sIP
	 */
	public JDialogDeviceState(java.awt.Frame parent, boolean modal, NativeLong lUserID,
			HCNetSDK.NET_DVR_DEVICEINFO_V30 strDeviceInfo, String sIP) {
		super(parent, modal);
		m_lUserID = lUserID;
		m_strDeviceInfo = strDeviceInfo;
		bChannelEnabled = new boolean[HCNetSDK.MAX_ANALOG_CHANNUM];// Ĭ�ϳ�ʼֵΪfalse
		iTotlaLink = 0;
		m_sDeviceIP = sIP;
		initComponents();
		showState();
	}

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane2 = new javax.swing.JScrollPane();
		jTable2 = new javax.swing.JTable();
		jPanelChannelState = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTableChannelState = new javax.swing.JTable();
		jPanelHDState = new javax.swing.JPanel();
		jScrollPane3 = new javax.swing.JScrollPane();
		jTableHDState = new javax.swing.JTable();
		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jTextFieldDeviceIP = new javax.swing.JTextField();
		jTextFieldDeviceState = new javax.swing.JTextField();
		jTextFieldTotalLink = new javax.swing.JTextField();
		jButtonRefresh = new javax.swing.JButton();
		jButtonExit = new javax.swing.JButton();

		jTable2.setModel(
				new javax.swing.table.DefaultTableModel(
						new Object[][] { { null, null, null, null }, { null, null, null, null },
								{ null, null, null, null }, { null, null, null, null } },
						new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane2.setViewportView(jTable2);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("�豸״̬");
		getContentPane().setLayout(null);

		jPanelChannelState.setBorder(javax.swing.BorderFactory.createTitledBorder("ͨ��״̬"));

		jTableChannelState.setModel(this.initialChannelTableModel());
		jScrollPane1.setViewportView(jTableChannelState);

		javax.swing.GroupLayout jPanelChannelStateLayout = new javax.swing.GroupLayout(jPanelChannelState);
		jPanelChannelState.setLayout(jPanelChannelStateLayout);
		jPanelChannelStateLayout.setHorizontalGroup(
				jPanelChannelStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						jPanelChannelStateLayout.createSequentialGroup().addContainerGap()
								.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
								.addContainerGap()));
		jPanelChannelStateLayout.setVerticalGroup(jPanelChannelStateLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanelChannelStateLayout.createSequentialGroup().addComponent(jScrollPane1,
						javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(11, Short.MAX_VALUE)));

		getContentPane().add(jPanelChannelState);
		jPanelChannelState.setBounds(10, 80, 650, 280);

		jPanelHDState.setBorder(javax.swing.BorderFactory.createTitledBorder("Ӳ��״̬"));

		jTableHDState.setModel(this.initialHDTableModel());
		jScrollPane3.setViewportView(jTableHDState);

		javax.swing.GroupLayout jPanelHDStateLayout = new javax.swing.GroupLayout(jPanelHDState);
		jPanelHDState.setLayout(jPanelHDStateLayout);
		jPanelHDStateLayout
				.setHorizontalGroup(jPanelHDStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanelHDStateLayout.createSequentialGroup().addContainerGap()
								.addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
								.addContainerGap()));
		jPanelHDStateLayout
				.setVerticalGroup(jPanelHDStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanelHDStateLayout.createSequentialGroup()
								.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 236,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		getContentPane().add(jPanelHDState);
		jPanelHDState.setBounds(10, 360, 650, 270);

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("�豸״̬"));
		jPanel1.setLayout(null);

		jLabel2.setText("IP��ַ");
		jPanel1.add(jLabel2);
		jLabel2.setBounds(30, 20, 50, 15);

		jLabel3.setText("�豸״̬");
		jPanel1.add(jLabel3);
		jLabel3.setBounds(230, 20, 60, 15);

		jLabel4.setText("������·��");
		jPanel1.add(jLabel4);
		jLabel4.setBounds(430, 20, 70, 15);
		jPanel1.add(jTextFieldDeviceIP);
		jTextFieldDeviceIP.setBounds(100, 20, 100, 21);
		jPanel1.add(jTextFieldDeviceState);
		jTextFieldDeviceState.setBounds(310, 20, 70, 21);
		jPanel1.add(jTextFieldTotalLink);
		jTextFieldTotalLink.setBounds(510, 20, 70, 21);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(10, 20, 650, 50);

		jButtonRefresh.setText("ˢ��");
		jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonRefreshActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonRefresh);
		jButtonRefresh.setBounds(470, 640, 60, 23);

		jButtonExit.setText("�˳�");
		jButtonExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonExitActionPerformed(evt);
			}
		});
		getContentPane().add(jButtonExit);
		jButtonExit.setBounds(570, 640, 60, 23);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/*************************************************
	 * ����: "ˢ��" ��ť������Ӧ���� ��������: ˢ���豸״̬
	 *************************************************/
	private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonRefreshActionPerformed
		showState();
	}// GEN-LAST:event_jButtonRefreshActionPerformed

	/*************************************************
	 * ����: "�˳�" ��ť������Ӧ���� ��������: ���ٴ���
	 *************************************************/
	private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonExitActionPerformed
		this.dispose();
	}// GEN-LAST:event_jButtonExitActionPerformed

	/*************************************************
	 * ����: initialChannelTableModel ��������: ��ʼ��ͨ��״̬�б�
	 *************************************************/
	public DefaultTableModel initialChannelTableModel() {
		String tabeTile[];
		tabeTile = new String[] { "ͨ����", "¼��״̬", "�ź�״̬", "Ӳ��״̬", "������", "��ǰ����(bps)", "IPC������" };
		DefaultTableModel channelTableModel = new DefaultTableModel(tabeTile, 0);
		return channelTableModel;
	}

	/*************************************************
	 * ����: initialHDTableModel ��������: ��ʼ��Ӳ��״̬�б�
	 *************************************************/
	public DefaultTableModel initialHDTableModel() {
		String tabeTile[];
		tabeTile = new String[] { "Ӳ�̺�", "Ӳ������(MB)", "ʣ��ռ�(MB)", "Ӳ��״̬" };
		DefaultTableModel HDTableModel = new DefaultTableModel(tabeTile, 0);
		return HDTableModel;
	}

	/*************************************************
	 * ����: showState ��������: ���ýӿڻ�ȡ�豸״̬����ʾ
	 *************************************************/
	private void showState() {
		((DefaultTableModel) jTableChannelState.getModel()).getDataVector().removeAllElements();// ���ͨ��״̬�б�
		((DefaultTableModel) jTableHDState.getModel()).getDataVector().removeAllElements();// ���Ӳ��״̬�б�
		iTotlaLink = 0;// �����������Ŀ

		m_strWorkState = new HCNetSDK.NET_DVR_WORKSTATE_V30();// ���ýӿڻ�ȡ�豸����״̬
		boolean getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRWorkState_V30(m_lUserID, m_strWorkState);
		if (getDVRConfigSuc != true) {
			System.out.println(hCNetSDK.NET_DVR_GetLastError());
			JOptionPane.showMessageDialog(this, "��ȡ�豸״̬ʧ��");
		}

		IntByReference ibrBytesReturned = new IntByReference(0);// ��ȡIP�������ò���
		getDVRConfigSuc = false;
		m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		m_strIpparaCfg.write();
		Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
		getDVRConfigSuc = hCNetSDK.NET_DVR_GetDVRConfig(m_lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0),
				lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
		m_strIpparaCfg.read();
		if (getDVRConfigSuc != true) {// �豸��֧��,���ʾû��IPͨ��
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				bChannelEnabled[iChannum] = true;
			}
		} else {// ����IPͨ��
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				bChannelEnabled[iChannum] = (m_strIpparaCfg.byAnalogChanEnable[iChannum] == 1) ? true : false;
			}
		}

		// ��ʾ�豸IP;
		jTextFieldDeviceIP.setText(m_sDeviceIP);

		// ��ʾ�豸״̬
		switch (m_strWorkState.dwDeviceStatic) {
		case 0:
			jTextFieldDeviceState.setText("����");
			break;
		case 1:
			jTextFieldDeviceState.setText("CPUռ����̫��,����85%");
			break;
		case 2:
			jTextFieldDeviceState.setText("Ӳ������");
			break;
		default:
			jTextFieldDeviceState.setText("δ֪");
			break;
		}

		// ��ʾģ��ͨ��״̬
		for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
			DefaultTableModel channelTableModel = ((DefaultTableModel) jTableChannelState.getModel());// ��ȡ���ģ��
			Vector<String> newRow = new Vector<String>();
			if (bChannelEnabled[iChannum] == true) { // ͨ����Ч
				newRow.add("Camera" + (iChannum + m_strDeviceInfo.byStartChan));// ���ģ��ͨ����

				// �Ƿ�¼��
				if (0 == m_strWorkState.struChanStatic[iChannum].byRecordStatic) {
					newRow.add("��¼��");
				} else {
					if (1 == m_strWorkState.struChanStatic[iChannum].byRecordStatic) {
						newRow.add("¼��");
					}
				}
				// �ź�״̬
				if (0 == m_strWorkState.struChanStatic[iChannum].bySignalStatic) {
					newRow.add("����");
				} else {
					if (1 == m_strWorkState.struChanStatic[iChannum].bySignalStatic) {
						newRow.add("�źŶ�ʧ");
					}
				}
				// Ӳ��״̬
				if (0 == m_strWorkState.struChanStatic[iChannum].byHardwareStatic) {
					newRow.add("����");
				} else {
					if (1 == m_strWorkState.struChanStatic[iChannum].byHardwareStatic) {
						newRow.add("�쳣");
					}
				}
				// ������
				newRow.add(m_strWorkState.struChanStatic[iChannum].dwLinkNum + "");
				// �˴�������������
				iTotlaLink += m_strWorkState.struChanStatic[iChannum].dwLinkNum;
				// ��ǰ����
				newRow.add(m_strWorkState.struChanStatic[iChannum].dwBitRate + "");
				// IPC������
				newRow.add(m_strWorkState.struChanStatic[iChannum].dwIPLinkNum + "");
			} else {
				continue;
			}
			channelTableModel.getDataVector().add(newRow);
			((DefaultTableModel) jTableHDState.getModel()).fireTableStructureChanged();
		}

		// ��ʾIPͨ��״̬
		// ���Ȼ�ȡIP����,���IP������Ӧͨ��������byEnableΪ��Ч,���ͨ����Ч,
		// �ٴӹ��������ṹ����ȡ�����״̬����,ģ��ͨ����:0-31,IPͨ����.32-64
		for (int iChannum = 0; iChannum < 32; iChannum++) {
			DefaultTableModel channelTableModel = ((DefaultTableModel) jTableChannelState.getModel());// ��ȡ���ģ��
			Vector<String> newRow = new Vector<String>();
			if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {// �ж϶�ӦIPͨ���Ƿ���Ч
				newRow.add("IPCamera" + (iChannum + 1));// ���IPͨ����
				// �Ƿ�¼�����
				if (0 == m_strWorkState.struChanStatic[32 + iChannum].byRecordStatic) {
					newRow.add("��¼��");
				} else {
					if (1 == m_strWorkState.struChanStatic[32 + iChannum].byRecordStatic) {
						newRow.add("¼��");
					}
				}
				// �ź�״̬
				if (0 == m_strWorkState.struChanStatic[32 + iChannum].bySignalStatic) {
					newRow.add("����");
				} else {
					if (1 == m_strWorkState.struChanStatic[32 + iChannum].bySignalStatic) {
						newRow.add("�źŶ�ʧ");
					}
				}
				// Ӳ��״̬
				if (0 == m_strWorkState.struChanStatic[32 + iChannum].byHardwareStatic) {
					newRow.add("����");
				} else {
					if (1 == m_strWorkState.struChanStatic[32 + iChannum].byHardwareStatic) {
						newRow.add("�쳣");
					}
				}
				// ������
				newRow.add(m_strWorkState.struChanStatic[32 + iChannum].dwLinkNum + "");
				// �˴�������������
				iTotlaLink += m_strWorkState.struChanStatic[32 + iChannum].dwLinkNum;
				// ��ǰ����
				newRow.add(m_strWorkState.struChanStatic[32 + iChannum].dwBitRate + "");
				// IPC������
				newRow.add(m_strWorkState.struChanStatic[32 + iChannum].dwIPLinkNum + "");
				channelTableModel.getDataVector().add(newRow);
				((DefaultTableModel) jTableHDState.getModel()).fireTableStructureChanged();
			}
		}

		// ��ʾ��������
		jTextFieldTotalLink.setText(iTotlaLink + "");

		// ��ʾӲ��״̬
		for (int i = 0; i < HCNetSDK.MAX_DISKNUM_V30; i++) {
			DefaultTableModel HDTableModel = ((DefaultTableModel) jTableHDState.getModel());// ��ȡ���ģ��
			Vector<String> newRow = new Vector<String>();

			newRow.add("Ӳ��" + (i + 1));// ���Ӳ�̺�
			newRow.add(m_strWorkState.struHardDiskStatic[i].dwVolume + "");// ���Ӳ������
			newRow.add(m_strWorkState.struHardDiskStatic[i].dwFreeSpace + "");// ���Ӳ��ʣ��ռ�
			if (m_strWorkState.struHardDiskStatic[i].dwVolume != 0) {// ���Ӳ��״̬
				switch (m_strWorkState.struHardDiskStatic[i].dwHardDiskStatic) {
				case 0:
					newRow.add("�");
					break;
				case 1:
					newRow.add("����");
					break;
				case 2:
					newRow.add("������");
					break;

				default:
					break;
				}
			} else {
				newRow.add("");// ����ļ�����Ϣ
			}
			HDTableModel.getDataVector().add(newRow);
			((DefaultTableModel) jTableHDState.getModel()).fireTableStructureChanged();
		}

		jTableChannelState.repaint();
		jTableHDState.repaint();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButtonExit;
	private javax.swing.JButton jButtonRefresh;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanelChannelState;
	private javax.swing.JPanel jPanelHDState;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JTable jTable2;
	private javax.swing.JTable jTableChannelState;
	private javax.swing.JTable jTableHDState;
	private javax.swing.JTextField jTextFieldDeviceIP;
	private javax.swing.JTextField jTextFieldDeviceState;
	private javax.swing.JTextField jTextFieldTotalLink;
	// End of variables declaration//GEN-END:variables
}
