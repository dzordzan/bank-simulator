
import javax.swing.SwingUtilities;


/**
 * @author Andrzej Piszczek (c) 2015 andpis58@gmail.com
 * Klasa zawieraj¹ca metode main.
 * Tworzy formê, tworzy obiekt symulatora
 *
 */
public class Projekt {
	public static boolean DEBUG_MODE = false;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame(new BankSimulator());
			}
		});

	}

}

