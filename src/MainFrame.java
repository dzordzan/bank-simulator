import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;


public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JButton startButton, stopButton, payinButton;
	private static JTextPane logsTextArea;
	private static DefaultListModel<String>  queueListModel;
	private static JLabel[] userLabels;
	private SettingsFrame settingsFrame;
	/**
	 * Create the frame.
	 */
	public MainFrame(String[] clients) {
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
		
		logsTextArea = new JTextPane();
		logsTextArea.setEditable(false);
		scrollPaneLogs.setViewportView(logsTextArea);
		panel.add(scrollPaneLogs);
		
		/*
		 * U¿ytkownicy
		 */
		JTabbedPane usersTabbedPanel = new JTabbedPane(JTabbedPane.TOP);
		usersTabbedPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab("Logi u\u017Cytkownik\u00F3w", null, usersTabbedPanel, null);
		userLabels = new JLabel[clients.length];
		for (int i=0; i<clients.length; i++){
			
			userLabels[i] = new JLabel("0");
			userLabels[i].setName(clients[i]);
			usersTabbedPanel.addTab(clients[i],null, new JPanel().add(userLabels[i]),null );
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

			if (Projekt.isRunning())
			{
				
				Functions.println("Symulator jest juz uruchomiony");
				return;
				
			}
			payinButton.setEnabled(true);
			stopButton.setEnabled(true);			
			Projekt.start();
		}
	}
	
	private class stopAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if (!Projekt.isRunning())
				return;
			
			Projekt.stop();
			
			payinButton.setEnabled(false);
			stopButton.setEnabled(false);	
		}
	}
	
	private class payinAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			Projekt.getAccount().add(200, null);
			

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
	public static void showQueue ( final ArrayList<Process> processes){
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run (){
	    		queueListModel.clear();
	    		for (int i = 0; i <processes.size();i++)
	    			queueListModel.addElement(Integer.toString(i+1)+". "+processes.get(i).getName() + " - " + processes.get(i).getNeedsToPayout() + "$");
	    	}});	
	}
	public static void showQueue2 ( final Object[] processes){
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run (){
	    	
	    		queueListModel.clear();
	    		for (int i = 0; i <processes.length;i++)
	    			queueListModel.addElement(Integer.toString(i+1)+". "+((Process)processes[i]).getName() + " - " + ((Process)processes[i]).getNeedsToPayout() + "$");
	    	}});	
	}
	public static void showLabel ( final String comName, final int cash){
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run (){
	    		for (int i = 0; i <userLabels.length;i++){
	    			if (userLabels[i].getName() == comName){
	    				System.out.print("tes2t");
	    				userLabels[i].setText(Integer.toString(cash));
	    			}
	    			
	    		}}
	    	
	    });
	}
}
	
