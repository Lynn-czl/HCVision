package vision;

import com.sun.jna.NativeLong;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * �豸����
 * 
 * @author WSL
 *
 */
@SuppressWarnings("all")
public class JDialogUpGrade extends javax.swing.JDialog {

	static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	private NativeLong m_lUserID;// �û�ID
	private JFileChooser upgradeJFileChooser;// �ļ�ѡ����
	private NativeLong m_lUpgradeHandle;// �ļ��������
	Timer timer;// ��ʱ��

	public JDialogUpGrade(java.awt.Frame parent, boolean modal, NativeLong lUserID) {
		super(parent, modal);
		m_lUserID = lUserID;
		m_lUpgradeHandle = new NativeLong(-1);
		initComponents();
		jProgressBarUpgrade.setMinimum(0);
		jProgressBarUpgrade.setMaximum(100);
	}

	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jComboBoxNetEnvir = new javax.swing.JComboBox();
		jButtonSetNetEnvir = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		jTextFieldFileDir = new javax.swing.JTextField();
		jButtonBrowse = new javax.swing.JButton();
		jLabel3 = new javax.swing.JLabel();
		jLabelUpgradeState = new javax.swing.JLabel();
		jButtonUpgrade = new javax.swing.JButton();
		jButtonExit = new javax.swing.JButton();
		jProgressBarUpgrade = new javax.swing.JProgressBar();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("�豸����");
		getContentPane().setLayout(null);

		jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

		jLabel1.setText("���绷��");

		jComboBoxNetEnvir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LAN", "WAN" }));

		jButtonSetNetEnvir.setText("����");
		jButtonSetNetEnvir.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonSetNetEnvirActionPerformed(evt);
			}
		});

		jLabel2.setText("�����ļ�");

		jTextFieldFileDir.setText("c:\\digicap");

		jButtonBrowse.setText("���");
		jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonBrowseActionPerformed(evt);
			}
		});

		jLabel3.setText("״̬");

		jButtonUpgrade.setText("����");
		jButtonUpgrade.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonUpgradeActionPerformed(evt);
			}
		});

		jButtonExit.setText("�˳�");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGap(27, 27, 27).addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(jProgressBarUpgrade, javax.swing.GroupLayout.Alignment.LEADING,
								javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
						.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
								jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jButtonUpgrade, javax.swing.GroupLayout.PREFERRED_SIZE,
														70, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(33, 33, 33).addComponent(jButtonExit,
														javax.swing.GroupLayout.PREFERRED_SIZE, 70,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(18, 18, 18).addComponent(jLabelUpgradeState,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(jButtonBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jTextFieldFileDir, javax.swing.GroupLayout.PREFERRED_SIZE,
														256, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jComboBoxNetEnvir, javax.swing.GroupLayout.PREFERRED_SIZE,
														70, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(31, 31, 31).addComponent(jButtonSetNetEnvir,
														javax.swing.GroupLayout.PREFERRED_SIZE, 60,
														javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap(47, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGap(21, 21, 21)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1)
								.addComponent(jComboBoxNetEnvir, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButtonSetNetEnvir))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel2).addComponent(jTextFieldFileDir,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jButtonBrowse).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jLabel3).addComponent(jLabelUpgradeState,
										javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jProgressBarUpgrade, javax.swing.GroupLayout.PREFERRED_SIZE, 15,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonUpgrade).addComponent(jButtonExit))
						.addContainerGap()));

		getContentPane().add(jPanel1);
		jPanel1.setBounds(10, 10, 410, 210);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/*************************************************
	 * ����: "���" ��ť������Ӧ���� ��������: ѡ�������ļ�
	 *************************************************/
	private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonBrowseActionPerformed
		JFileChooser JFileChooser1 = new JFileChooser("c:/digicap");// ����һ���ļ�ѡ����
		if (JFileChooser.APPROVE_OPTION == JFileChooser1.showOpenDialog(this))// ����ļ�ѡ�����
		{
			openFile(JFileChooser1.getSelectedFile().getPath());// ��Ϊ�����Ľӿ�
			String filepath = JFileChooser1.getSelectedFile().getPath();// ��ȡ��ѡ���ļ���·��
			jTextFieldFileDir.setText(filepath);// ����ļ�·��
		}
	}// GEN-LAST:event_jButtonBrowseActionPerformed

	/*************************************************
	 * ����: "����" ��ť������Ӧ���� ��������: �������绷��
	 *************************************************/
	private void jButtonSetNetEnvirActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSetNetEnvirActionPerformed
		if (!hCNetSDK.NET_DVR_SetNetworkEnvironment(jComboBoxNetEnvir.getSelectedIndex())) {
			JOptionPane.showMessageDialog(this, "�������绷��ʧ��");
			return;
		}
	}// GEN-LAST:event_jButtonSetNetEnvirActionPerformed

	/*************************************************
	 * ����: "����" ��ť������Ӧ���� ��������: ���ýӿ�,��ʼ����
	 *************************************************/
	private void jButtonUpgradeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonUpgradeActionPerformed
		File fileUpgrade = new File(jTextFieldFileDir.getText());
		if (fileUpgrade.canRead() == false) {
			JOptionPane.showMessageDialog(this, "��Ч�ļ�");
			return;
		}
		if (fileUpgrade.length() == 0) {
			JOptionPane.showMessageDialog(this, "�ļ�Ϊ��");
			return;
		}
		m_lUpgradeHandle = hCNetSDK.NET_DVR_Upgrade(m_lUserID, jTextFieldFileDir.getText());
		if (m_lUpgradeHandle.intValue() < 0) {
			JOptionPane.showMessageDialog(this, "����ʧ��");
		} else {
			timer = new Timer();// �½���ʱ��
			timer.schedule(new MyTask(), 0, 500);// 0���ʼ��Ӧ����
		}
	}// GEN-LAST:event_jButtonUpgradeActionPerformed

	/*************************************************
	 * ��: MyTask ������: ��ʱ����Ӧ����
	 *************************************************/
	class MyTask extends java.util.TimerTask {// ��ʱ������ �൱��c�����е�onTimer();

		@Override
		public void run() {
			int UpgradeStatic = hCNetSDK.NET_DVR_GetUpgradeState(m_lUpgradeHandle);
			int iPos = hCNetSDK.NET_DVR_GetUpgradeProgress(m_lUpgradeHandle);

			if (iPos > 0) {
				jProgressBarUpgrade.setValue(iPos);
			}
			if (UpgradeStatic == 2) {
				jLabelUpgradeState.setText("״̬�����������豸����ȴ�......");
			} else {
				switch (UpgradeStatic) {
				case -1:
					jLabelUpgradeState.setText("����ʧ��");
					break;
				case 1:
					jLabelUpgradeState.setText("״̬�������豸�ɹ�");
					jProgressBarUpgrade.setValue(100);
					break;
				case 3:
					jLabelUpgradeState.setText("״̬�������豸ʧ��");
					break;
				case 4:
					jLabelUpgradeState.setText("״̬������Ͽ�,״̬δ֪");
					break;
				case 5:
					jLabelUpgradeState.setText("״̬�������ļ����԰汾��ƥ��");
					break;
				default:
					break;
				}
				if (hCNetSDK.NET_DVR_CloseUpgradeHandle(m_lUpgradeHandle) == true) {
					System.out.println("NET_DVR_CloseUpgradeHandle");
				}
				m_lUpgradeHandle = new NativeLong(-1);
				timer.cancel();// ʹ����������˳�����
			}
		}
	}

	void openFile(String fileName) {
		try {
			File file = new File(fileName);
			int size = (int) file.length();
			int chars_read = 0;
			FileReader in = new FileReader(file);
			char[] data = new char[size];
			while (in.ready()) {
				chars_read += in.read(data, chars_read, size - chars_read);
				// read(Ŀ�����顢�ļ���ʼλ�á��ļ�����λ��)
				// ���ض����������
			}
			in.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	private javax.swing.JButton jButtonBrowse;
	private javax.swing.JButton jButtonExit;
	private javax.swing.JButton jButtonSetNetEnvir;
	private javax.swing.JButton jButtonUpgrade;
	private javax.swing.JComboBox jComboBoxNetEnvir;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabelUpgradeState;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JProgressBar jProgressBarUpgrade;
	private javax.swing.JTextField jTextFieldFileDir;
}
