import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;
/*
 * @autor Andrzej Piszcze
 * @email andpis58@gmail.com
 * 
 * s174644 UEK
 */
class BankSimulator {
	private static String[] clients = {"Bank", "Anna", "Piotr", "Maria", "Krzysztof",
			"Katarzyna", "Andrzej", "Ma�gorzata", "Jan", "Agnieszka",
			"Stanis�aw", "Barbara", "Tomasz", "Krystyna", "Pawe�", "Ewa",
			"Marcin", "El�bieta", "Micha�", "Zofia", "Marek", "Teresa",
			"Grzegorz", "Magdalena", "J�zef", "Joanna", "�ukasz", "Janina",
			"Adam", "Monika", "Zbigniew", "Danuta", "Jerzy", "Jadwiga",
			"Tadeusz", "Aleksandra", "Mateusz", "Halina", "Dariusz", "Irena",
			"Mariusz" };

	private Bank account;
	private Process[] processes;
	private boolean running = false;
	private BlockingQueue<Process> blockingQueue;

	
	public BankSimulator(){
		create();
	}
	
	public void create(){
		// Wielko�� kolejki to ilo�� klient�w
				blockingQueue = new ArrayBlockingQueue<Process>(clients.length);

				// Otwieramy nowe konto
				// Konto to w�tek, kt�ry sprawdza ca�y czas, czy jaki� proces nie chce
				// wyp�aci� got�wki i zarz�dza kolejk�
				account = new Bank(this, blockingQueue);

				
				processes = new Process[clients.length];
				/*
				 * Pierwszym procesem jest proces banku, potrzebny do r�cznego wp�acania
				 */
				processes[0] = new Process(account, blockingQueue);
				processes[0].setName(clients[0]);
				
				for (int i = 1; i < clients.length; i++) {
					processes[i] = new Process(account, blockingQueue);
					processes[i].setName(clients[i]);
				}
			
	}
	/*
	 * Startuje nowy Projekt z nowymi obiektami
	 */
		
	public void start() {
		running = true;
		
		
		//Startujemy w�tek banku
		account.start();
		
		// Pomijamy proces banku, ktory jest pierwszy
		for (int i = 1; i < clients.length; i++) 
			processes[i].start();
		

	}

	/*
	 * Stopuje aktualny projeky Wysy�a sygna� stop do wszystkich dzia�aj�cych
	 * w�tk�w (interrupt) W�tki przechwytuj� sygna� i po zako�czeniu bie�acych
	 * operacji ko�cz� p�tle
	 */
	public void stop() {
		if (running) {
			running = false;

			account.interrupt();
			for (int i = 0; i < processes.length; i++)
				processes[i].interrupt();

			Functions
					.println("[Projek] Wys�ano sygna� stopu do wszystkich proces�w");

			account = null;
			processes = null;
			
			blockingQueue = null;
			create();
			/*
			 * Procesy mog� wy�wietla� jeszcze informacje typu, kodem ni�ej
			 * mo�na by przechwyci� czy wszystkie na pewno si� zako�czy�y
			 * 
			 * for (Thread process : processes){ try { process.join(); } catch
			 * (InterruptedException e) { e.printStackTrace(); } }
			 * Functions.println("[Main] Wszystkie procesy zastopowane.");
			 */
		}
	}
	public Bank getAccount() {
		return account;
	}
	public Process[] getProcesses(){
		return this.processes;
	}
	public BlockingQueue<Process> getBlockingQueue() {
		return blockingQueue;
	}

	public boolean isRunning() {
		return this.running;
	}
	
}
public class Projekt {
	public static boolean DEBUG_MODE = false;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame(new BankSimulator());
			}
		});

	}

}

/**
 * 
 * N - proces�w Ka�dy ma dostep do wsp�lnego konta bankowego Proces mo�e
 * wp�aca�/wyp�aca�
 * 
 * Nie mo�e by� debetu Nie mo�na wyp�aci� wi�cej ni� jest na koncie *** Proces
 * czeka, a� ilo�� �rodk�w na koncie b�dzie r�wna, b�d� wi�ksza ile ma zamiar
 * wyp�aci� *** Kolejkowanie *** *** Czy kase dostanie ten, kt�ry najd�u�ej
 * czeka w kolejce, czy ten dla kt�rego jest odpowiednia ilo�� kasy Niezale�ne
 * wp�aty od�wie�aj�ce stan konta
 * 
 * 
 */
