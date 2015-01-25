/*
 * @autor Andrzej Piszcze
 * @email andpis58@gmail.com
 * 
 * s174644 UEK
 */
public class BankSimulator {
	
	/**
	 * Klienci banku, mo¿na dodawaæ ile siê podoba. Im wiêcej tym wiêksze obci¹¿enie procesora
	 */
	private static String[] clients = {"Bank", "Anna", "Piotr", "Maria", "Krzysztof",
			"Katarzyna", "Andrzej", "Ma³gorzata", "Jan", "Agnieszka",
			"Stanis³aw", "Barbara", "Tomasz", "Krystyna", "Pawe³", "Ewa",
			"Marcin", "El¿bieta", "Micha³", "Zofia", "Marek", "Teresa",
			"Grzegorz", "Magdalena", "Józef", "Joanna", "£ukasz", "Janina",
			"Adam", "Monika", "Zbigniew", "Danuta", "Jerzy", "Jadwiga",
			"Tadeusz", "Aleksandra", "Mateusz", "Halina", "Dariusz", "Irena",
			"Mariusz" };

	/**
	 * Konto bankowe, ob³uguj¹ce wplaty/wyp³aty i kolejkowanie.
	 */
	private Bank account;
	/**
	 * Procesy maj¹ce dostep do konta bankowego. Proces to klient.
	 * Procesy s¹ synchronizowane w celu oczekiwania na mo¿liwoœc wyp³aty.
	 */
	private Process[] processes;
	/**
	 * Zmienna do sprawdzania czy program aktualnie pracuje.
	 */
	private boolean running = false;

	
	/**
	 * Konstruktor symulatora.
	 * Domyœlnie podczas tworzenia tworzy nowe zmienne.
	 */
	public BankSimulator(){
		create();
	}
	
	/**
	 * Tworzy nowe zmienne.
	 * Potrzebne w razie ewentualnego zastopowania i uruchomienia od nowa projektu.
	 */
	public void create(){

		processes = new Process[clients.length];
		// Otwieramy nowe konto
		// Konto to w¹tek, który sprawdza ca³y czas, czy jakiœ proces nie chce
		// wyp³aciæ gotówki i zarz¹dza kolejk¹
		account = new Bank(this);

		
		
		/*
		 * Pierwszym procesem jest proces banku, potrzebny do rêcznego wp³acania
		 */
		processes[0] = new Process(account);
		processes[0].setName(clients[0]);
		
		for (int i = 1; i < clients.length; i++) {
			processes[i] = new Process(account);
			processes[i].setName(clients[i]);
		}
			
	}
	
	/**
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

	/**
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
			
			new Thread(){
				@SuppressWarnings("deprecation")
				public void run() {
					for (Process process : processes){ try { process.join(200); process.stop(); } catch
						 (InterruptedException e) { e.printStackTrace(); } }
					
					Functions.println("[BankSimulator] Wszystkie procesy zastopowane.");
					account = null;
					processes = null;
					LineChart.clearDataSet();
					create();
				}}.start() ;

			/*
			 * Procesy mog¹ wyœwietlaæ jeszcze informacje typu, kodem ni¿ej
			 * mo¿na by przechwyciæ czy wszystkie na pewno siê zakoñczy³y
			 * 
			 * 
			 */
		}
	}
	/**
	 * Zwaraca aktualny zmienn¹ bank na jakiej operuje symulator.
	 * @return Bank
	 */
	public Bank getAccount() {
		return account;
	}
	/**
	 * Zwaraca aktualn¹ tablicê procesów na który operuje symulator.
	 * @return Array of Process
	 */
	public Process[] getProcesses(){
		return this.processes;
	}

	/**
	 * @return
	 */
	public boolean isRunning() {
		return this.running;
	}
	
}
