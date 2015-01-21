import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class SettingsFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	

	public SettingsFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	
		setBounds(100, 100, 335, 242);
		JPanel mainPanel = new JPanel();
		setContentPane(mainPanel);
		mainPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(71dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(111dlu;default):grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblNewLabel = new JLabel("Szybko\u015B\u0107 symulatora");
		mainPanel.add(lblNewLabel, "2, 2");
		
		final JSlider processSpeedSlider = new JSlider();
		
		processSpeedSlider.setMaximum(999);
		processSpeedSlider.setValue(800);
		processSpeedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
	            Process.PROCESS_SPEED = 1000-processSpeedSlider.getValue();
			}
		});
		Process.PROCESS_SPEED = 1000-processSpeedSlider.getValue();
		
		mainPanel.add(processSpeedSlider, "4, 2, center, center");
		
		JLabel lblCzstotliwoWpat = new JLabel("Cz\u0119stotliwo\u015B\u0107 wp\u0142at");
		mainPanel.add(lblCzstotliwoWpat, "2, 4");
		
		final JSlider payinRatioSlider = new JSlider();
		payinRatioSlider.setValue(15);
		payinRatioSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
	            Process.PAYIN_RATIO = payinRatioSlider.getValue();
			}
		});
		Process.PAYIN_RATIO = payinRatioSlider.getValue();
		
		payinRatioSlider.setSnapToTicks(true);
		payinRatioSlider.setMajorTickSpacing(2);
		payinRatioSlider.setToolTipText("Prowdopodobie\u0144stwo dokonania wp\u0142aty zamiast wyp\u0142aty przez klienta.");
		mainPanel.add(payinRatioSlider, "4, 4, center, center");
		
		JLabel lblredniaWpata = new JLabel("\u015Arednia wp\u0142ata ~1000$");
		mainPanel.add(lblredniaWpata, "2, 6");
		
		final JSlider averagePayinSlider = new JSlider();
		
		averagePayinSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
	            Process.AVERAGE_PAYIN = averagePayinSlider.getValue();
				}
			});
		Process.AVERAGE_PAYIN  = averagePayinSlider.getValue();
		
		averagePayinSlider.setMajorTickSpacing(5000);
		averagePayinSlider.setPaintTicks(true);
		averagePayinSlider.setMinorTickSpacing(290);
		averagePayinSlider.setPaintLabels(true);
		averagePayinSlider.setMaximum(5000);
		averagePayinSlider.setValue(4000);
		mainPanel.add(averagePayinSlider, "4, 6");
		
		JLabel lblredniaWypata = new JLabel("\u015Arednia wyp\u0142ata ~200$");
		lblredniaWypata.setHorizontalAlignment(SwingConstants.LEFT);
		mainPanel.add(lblredniaWypata, "2, 8");
		
		final JSlider averagePayoutSlider = new JSlider();
		averagePayoutSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
	            Process.AVERAGE_PAYOUT = averagePayoutSlider.getValue();
				}
			});
		Process.AVERAGE_PAYOUT  = averagePayoutSlider.getValue();
		
		averagePayoutSlider.setPaintLabels(true);
		averagePayoutSlider.setMinorTickSpacing(100);
		averagePayoutSlider.setMajorTickSpacing(2000);
		averagePayoutSlider.setPaintTicks(true);
		averagePayoutSlider.setMaximum(2000);
		averagePayoutSlider.setValue(550);
		mainPanel.add(averagePayoutSlider, "4, 8, fill, default");
		
		JLabel lblrSzybkocBankomatu = new JLabel("\u015Ar. szybko\u015Bc bankomatu");
		mainPanel.add(lblrSzybkocBankomatu, "2, 10");
		
		final JSlider bankSpeedSlider = new JSlider();
		
		bankSpeedSlider.setToolTipText("Wp\u0142ywa na wielko\u015B\u0107 kolejki i szybko\u015Bc symulatora");
		bankSpeedSlider.setMaximum(1000);
		bankSpeedSlider.setValue(950);
		bankSpeedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
	            Bank.BANK_SPEED = bankSpeedSlider.getMaximum() - bankSpeedSlider.getValue();
				}
			});
		Bank.BANK_SPEED = bankSpeedSlider.getMaximum() - bankSpeedSlider.getValue();
		bankSpeedSlider.setPaintTicks(true);
		bankSpeedSlider.setPaintLabels(true);
		mainPanel.add(bankSpeedSlider, "4, 10");
		
		//JCheckBox chckbxGdyBrakrodkw = new JCheckBox("W\u0142\u0105cz kolejke");
		//mainPanel.add(chckbxGdyBrakrodkw, "2, 12");
		
		final JCheckBox chckbxSkalujWykres = new JCheckBox("skaluj wykres");
		chckbxSkalujWykres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LineChart.SCALABLE = chckbxSkalujWykres.isSelected();
				
			}
		});
		LineChart.SCALABLE = chckbxSkalujWykres.isSelected();
		mainPanel.add(chckbxSkalujWykres, "2, 12");
		
		final JCheckBox chckbxDwikWpaty = new JCheckBox("d\u017Awi\u0119k wp\u0142aty");
		mainPanel.add(chckbxDwikWpaty, "4, 12");
		chckbxDwikWpaty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Bank.PAYIN_SOUND = chckbxDwikWpaty.isSelected();
				
			}
		});
		Bank.PAYIN_SOUND = chckbxDwikWpaty.isSelected();
		final JCheckBox debugCheckBox = new JCheckBox("debuguj");
		debugCheckBox.setSelected(true);
		debugCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Projekt.DEBUG_MODE = debugCheckBox.isSelected();
			}
		});
		Projekt.DEBUG_MODE = debugCheckBox.isSelected();
		mainPanel.add(debugCheckBox, "2, 14");
		
		pack();
		setTitle("Ustawienia");
		//setVisible(true);
		
	}
	

}
