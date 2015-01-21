import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.JTextArea;


public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BankSimulator simulator;
	private Process[] processes;
	
	private JPanel contentPane;
	private JButton startButton, stopButton, payinButton;
	private static JTextPane logsTextArea;
	private static DefaultListModel<String>  queueListModel;
	private SettingsFrame settingsFrame;
	//private JTable table;
	private static DefaultTableModel usersModel;
	
	/**
	 * Tworzy ca³¹ formê
	 * @return 
	 * 
	 * 
	 * 
	 */
	public MainFrame(BankSimulator simulator) {

		this.simulator = simulator;
		this.processes = simulator.getProcesses();
		//System.out.println(processes[0].getName());
		settingsFrame = new SettingsFrame();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 804, 434);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//settingsPanel.setPreferredSize(new Dimension(640, 480));;
		
		/*
		 * 
		 * WYKRES
		 */
		JPanel chartPanel = new JPanel();
		chartPanel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		chartPanel.setLayout(new BorderLayout(0, 0));
		LineChart lineChart = new LineChart();
		chartPanel.add(lineChart.getChartPanel(), BorderLayout.CENTER);
		
		
		/*
		 * PRZYCISKI
		 */
		startButton = new JButton("Start");
		startButton.addActionListener(new startAction());
		
		stopButton = new JButton("Stop");
		stopButton.addActionListener(new stopAction());
		stopButton.setEnabled(false);
		
		payinButton = new JButton("Wp\u0142a\u0107 1000$");
		payinButton.addActionListener(new payinAction());
		payinButton.setEnabled(false);
		/*
		 *  // Przyciski
		 */
		
		
		JLabel lblLogi = new JLabel("Aktualna kolejka:");

		JScrollPane scrollPane = new JScrollPane();
		
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JButton btnUstawienia = new JButton("Ustawienia");
		btnUstawienia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.setVisible(true);
			}
		});
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(1)
					.addComponent(chartPanel, GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
							.addGap(10)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblLogi)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnUstawienia)
							.addPreferredGap(ComponentPlacement.RELATED, 396, Short.MAX_VALUE)
							.addComponent(payinButton)
							.addGap(18)
							.addComponent(stopButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(chartPanel, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblLogi)
							.addGap(10)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(stopButton)
						.addComponent(payinButton)
						.addComponent(btnUstawienia)))
		);
		/*
		 * 
		 *  LOGI
		 */
		JPanel panel = new JPanel();
		tabbedPane.addTab("Logi konta", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPaneLogs = new JScrollPane();
		scrollPaneLogs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		logsTextArea = new JTextPane();
		logsTextArea.setEditable(false);
		scrollPaneLogs.setViewportView(logsTextArea);
		panel.add(scrollPaneLogs);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Logi u\u017Cytkownik\u00F3w", null, getContentPane().add(scrollPane_1), null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.addTab("O co chodzi", null, scrollPane_2, null);
		
		JTextArea txtrTre = new JTextArea();
		txtrTre.setWrapStyleWord(true);
		txtrTre.setLineWrap(true);
		txtrTre.setText("Tre\u015B\u0107:\r\nKonto w banku jest wsp\u00F3ln\u0105 w\u0142asno\u015Bci\u0105 grupy n-proces\u00F3w. Ka\u017Cdy proces mo\u017Ce wp\u0142aci\u0107 lub wyp\u0142acic pieni\u0105dze z konta. Bei\u017Cacy stan konta jest sum\u0105 wszystkich dotychczasowych wp\u0142at minus suma wszystkich dotychczasowych wyp\u0142at. Na koncie nigdy nie mo\u017Ce powsta\u0107 debet. Je\u017Celi kwota, kt\u00F3r\u0105 proces pr\u00F3buje wyp\u0142aci\u0107 przewy\u017Csza stan konta, jest on wstrzymywany do czasu, gdy wykonanie tej operacji b\u0119dzie niemo\u017Cliwe.\r\n\r\n======================================\r\n\r\nProblem pojawia si\u0119 momencie gdy wszystkie procesy pr\u00F3buj\u0105 wyp\u0142aci\u0107 z konta pieni\u0105dze, a mo\u017Ce si\u0119 okaza\u0107 \u017Ce nie ma wystarczaj\u0105cych \u015Brodk\u00F3w: procesy si\u0119 wtedy blokuj\u0105. \r\n");
		scrollPane_2.setViewportView(txtrTre);
		
		JTable usersTable = new JTable();
		usersModel = new DefaultTableModel(
				new Object[][] {
					},
					new String[] {
						"U\u017Cytkownik", "Wp\u0142aci\u0142", "Wyp\u0142aci\u0142", "Razem"
					}
				);
		usersTable.setModel(usersModel);
		usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPane_1.setViewportView(usersTable);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(usersModel);
		usersTable.setRowSorter(sorter);
		///*userLabels = new JLabel[clients.length];
		for (int i=0; i<processes.length; i++){
			Object[] insertRowData = {processes[i].getName(), 0, 0,0 };
			usersModel.insertRow(i, insertRowData);
			//usersModel.setValueAt(processes[i].getName(), i+1, 0);
			//usersModel.ad
		}
		
		/*
		 *  KOLEJKA
		 */
		
		queueListModel = new DefaultListModel<String>();
		JList<String> queueList = new JList<String>(queueListModel);
		scrollPane.setViewportView(queueList);
		contentPane.setLayout(gl_contentPane);
		
		pack();
		setTitle("Symulator banku");
		setVisible(true);
		
	}

	private class startAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			if (simulator.isRunning())
			{
				
				Functions.println("Symulator jest juz uruchomiony");
				return;
				
			}
			payinButton.setEnabled(true);
			stopButton.setEnabled(true);			
			simulator.start();
		}
	}
	
	private class stopAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if (!simulator.isRunning())
				return;
			
			simulator.stop();
			
			payinButton.setEnabled(false);
			stopButton.setEnabled(false);	
		}
	}
	
	private class payinAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			new Thread(){
				public void run() {
		    		simulator.getAccount().set(2000, simulator.getProcesses()[0]);
				}}.start() ;
					   
		}
	}
	
	public static void addLogs ( final String log ){
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run (){
	    		StyledDocument doc =logsTextArea.getStyledDocument();
	    		try {
					doc.insertString(doc.getLength(), log, null);
				} catch (BadLocationException e) {e.printStackTrace();}
	    		//logsTextArea.setText(logsTextArea.getText());
	    		//logsTextArea.append(log);
	    	}});
	}

	public static void showQueue ( final Object[] processes){
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run (){
	    	
	    		queueListModel.clear();
	    		for (int i = 0; i <processes.length;i++)
	    			queueListModel.addElement(Integer.toString(i+1)+". "+((Process)processes[i]).getName() + " - " + ((Process)processes[i]).getNeedsToPayout() + "$");
	    	}});	
	}
	public static void showSummaries (final Process [] processes){
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run (){
	    		for (int i = 0; i <processes.length;i++){
	    			
	    			usersModel.setValueAt(processes[i].getSummaryPayin(), i, 1);
	    			usersModel.setValueAt(processes[i].getSummaryPayout(), i, 2);
	    			usersModel.setValueAt(processes[i].getSummaryPayin()+processes[i].getSummaryPayout(), i, 3);
	    		}}
	    	
	    });
	}
}
	
