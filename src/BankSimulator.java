/*
 * @autor Andrzej Piszcze
 * @email andpis58@gmail.com
 * 
 * s174644 UEK
 */
public class BankSimulator {
	
	/**
	 * Klienci banku, mo�na dodawa� ile si� podoba. Im wi�cej tym wi�ksze obci��enie procesora
	 */
	private static String[] clients = {"Bank", "Anna", "Piotr", "Maria", "Krzysztof",
			"Katarzyna", "Andrzej", "Ma�gorzata", "Jan", "Agnieszka",
			"Stanis�aw", "Barbara", "Tomasz", "Krystyna", "Pawe�", "Ewa",
			"Marcin", "El�bieta", "Micha�", "Zofia", "Marek", "Teresa",
			"Grzegorz", "Magdalena", "J�zef", "Joanna", "�ukasz", "Janina",
			"Adam", "Monika", "Zbigniew", "Danuta", "Jerzy", "Jadwiga",
			"Tadeusz", "Aleksandra", "Mateusz", "Halina", "Dariusz", "Irena",
			"Mariusz" };

	/**
	 * Konto bankowe, ob�uguj�ce wplaty/wyp�aty i kolejkowanie.
	 */
	private Bank account;
	/**
	 * Procesy maj�ce dostep do konta bankowego. Proces to klient.
	 * Procesy s� synchronizowane w celu oczekiwania na mo�liwo�c wyp�aty.
	 */
	private Process[] processes;
	/**
	 * Zmienna do sprawdzania czy program aktualnie pracuje.
	 */
	private boolean running = false;

	
	/**
	 * Konstruktor symulatora.
	 * Domy�lnie podczas tworzenia tworzy nowe zmienne.
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
		// Konto to w�tek, kt�ry sprawdza ca�y czas, czy jaki� proces nie chce
		// wyp�aci� got�wki i zarz�dza kolejk�
		account = new Bank(this);

		
		
		/*
		 * Pierwszym procesem jest proces banku, potrzebny do r�cznego wp�acania
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
		
		
		//Startujemy w�tek banku
		account.start();
		
		// Pomijamy proces banku, ktory jest pierwszy
		for (int i = 1; i < clients.length; i++) 
			processes[i].start();
		

	}

	/**
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
			 * Procesy mog� wy�wietla� jeszcze informacje typu, kodem ni�ej
			 * mo�na by przechwyci� czy wszystkie na pewno si� zako�czy�y
			 * 
			 * 
			 */
		}
	}
	/**
	 * Zwaraca aktualny zmienn� bank na jakiej operuje symulator.
	 * @return Bank
	 */
	public Bank getAccount() {
		return account;
	}
	/**
	 * Zwaraca aktualn� tablic� proces�w na kt�ry operuje symulator.
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
