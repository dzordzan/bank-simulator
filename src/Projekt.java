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
			"Katarzyna", "Andrzej", "Ma³gorzata", "Jan", "Agnieszka",
			"Stanis³aw", "Barbara", "Tomasz", "Krystyna", "Pawe³", "Ewa",
			"Marcin", "El¿bieta", "Micha³", "Zofia", "Marek", "Teresa",
			"Grzegorz", "Magdalena", "Józef", "Joanna", "£ukasz", "Janina",
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
		// Wielkoœæ kolejki to iloœæ klientów
				blockingQueue = new ArrayBlockingQueue<Process>(clients.length);

				// Otwieramy nowe konto
				// Konto to w¹tek, który sprawdza ca³y czas, czy jakiœ proces nie chce
				// wyp³aciæ gotówki i zarz¹dza kolejk¹
				account = new Bank(this, blockingQueue);

				
				processes = new Process[clients.length];
				/*
				 * Pierwszym procesem jest proces banku, potrzebny do rêcznego wp³acania
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
		
		
		//Startujemy w¹tek banku
		account.start();
		
		// Pomijamy proces banku, ktory jest pierwszy
		for (int i = 1; i < clients.length; i++) 
			processes[i].start();
		

	}

	/*
	 * Stopuje aktualny projeky Wysy³a sygna³ stop do wszystkich dzia³aj¹cych
	 * w¹tków (interrupt) W¹tki przechwytuj¹ sygna³ i po zakoñczeniu bie¿acych
	 * operacji koñcz¹ pêtle
	 */
	public void stop() {
		if (running) {
			running = false;

			account.interrupt();
			for (int i = 0; i < processes.length; i++)
				processes[i].interrupt();

			Functions
					.println("[Projek] Wys³ano sygna³ stopu do wszystkich procesów");

			account = null;
			processes = null;
			
			blockingQueue = null;
			create();
			/*
			 * Procesy mog¹ wyœwietlaæ jeszcze informacje typu, kodem ni¿ej
			 * mo¿na by przechwyciæ czy wszystkie na pewno siê zakoñczy³y
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
 * N - procesów Ka¿dy ma dostep do wspólnego konta bankowego Proces mo¿e
 * wp³acaæ/wyp³acaæ
 * 
 * Nie mo¿e byæ debetu Nie mo¿na wyp³aciæ wiêcej ni¿ jest na koncie *** Proces
 * czeka, a¿ iloœæ œrodków na koncie bêdzie równa, b¹dŸ wiêksza ile ma zamiar
 * wyp³aciæ *** Kolejkowanie *** *** Czy kase dostanie ten, który najd³u¿ej
 * czeka w kolejce, czy ten dla którego jest odpowiednia iloœæ kasy Niezale¿ne
 * wp³aty odœwie¿aj¹ce stan konta
 * 
 * 
 */
