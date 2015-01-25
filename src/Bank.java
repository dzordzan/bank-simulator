import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank extends Thread {
	public static int BANK_SPEED = 100;
	public static boolean PAYIN_SOUND;// , PAYOUT_SOUND;
	private ReentrantReadWriteLock operationLock = new ReentrantReadWriteLock(
			true);
	private Lock readOperation = operationLock.readLock();
	private Lock writeOperation = operationLock.writeLock();

	private BankSimulator bankSimulator;
	/**
	 * Zmienna do bezpoœredniej obs³ugi kolejki. Operuj¹ przechowujê klasê
	 * Process a dostep do niej ma konto bankowe. Konto bankowe sprawdza czy
	 * jest mozliwoœc wyp³aty dla najd³u¿ej czekaj¹cego procesu i go
	 * odblokowywuje.
	 */
	private BlockingQueue<Process> blockingQueue;

	/**
	 * Starowo w banku jest 2000
	 */
	private int cash = 2000;

	private boolean isRunning = true;

	/**
	 * @param bankSimulator
	 */
	public Bank(BankSimulator bankSimulator) {
		this.blockingQueue = new ArrayBlockingQueue<Process>(bankSimulator.getProcesses().length);	
				
		// Wielkoœæ kolejki to iloœæ klientów
		this.bankSimulator = bankSimulator;
	}

	/** 
	 * Nieskonczona pêtla sprawdzaj¹ca kolejkê
	 */
	public void run() {
		while (isRunning) {
			checkQueue();
		}
	}

	/**
	 * Sprawdzanie kolejki.
	 * Sprawdzanie jest skomplikowane wszystko wyt³umaczone w komentarzach.
	 */
	public synchronized void checkQueue() {
		try {
			if (blockingQueue.isEmpty())
				return;

			/*
			 * SprawdŸ czy pierwszy oczekuj¹cy proces mo¿e wyp³aciæ gotówkê
			 * Je¿eli NIE mo¿e metoda checkQueue zostaje zapauzowana i oczekuje
			 * na sygna³ wp³aty Je¿eli MO¯E wyp³aciæ kontynuuje dalej
			 * wykonywanie metody
			 */
			while (blockingQueue.peek().getNeedsToPayout() > this.getCash()) {
				Process firstProcess = blockingQueue.peek();

				if (Projekt.DEBUG_MODE)
					Functions
							.println(String
									.format("[%s.%s] Pierwszy w kolejce [%s, %d]. Stan konta: [%d]",
											this.getClass().getName(), Thread
													.currentThread()
													.getStackTrace()[1]
													.getMethodName(),
											firstProcess.getName(),
											firstProcess.getNeedsToPayout(),
											this.getCash()));

				wait();
			}
			/*
			 * Zdejmuje najd³u¿ej czekaj¹cy prroces Zmienia stan konta Wyœwietla
			 * informacje na formê o kolejce
			 */
			Process waitingProcess = blockingQueue.take();
			MainFrame.showQueue(blockingQueue.toArray());
			this.set(-waitingProcess.getNeedsToPayout(), waitingProcess);

			/*
			 * Wysy³a sygna³ do procesu (klienta), który doda³ siê do kolejki po
			 * wyp³ate
			 * 
			 * Process pauzuje siê sam po dodaniu do kolejki
			 */
			waitingProcess.waitForPayout(false);

		} catch (InterruptedException e) {
			this.isRunning = false;
			Functions.handleInterrupt();
		}

	}

	/**
	 * Dodawanie procesu do kolejki
	 * @param process
	 */
	public void addToQueue(Process process) {
		try {

			// Pauzuj process, który wyowa³a³ metode
			process.waitForPayout(true);
			/*
			 * Dodaje proces do kolejki. Teoretycznie, czeka gdyby kolejka siê
			 * przep³ni³a (ale nie ma prawa do tego dojœæ)
			 */
			blockingQueue.put(process);
			MainFrame.showQueue(blockingQueue.toArray());
		} catch (InterruptedException e) {
			this.isRunning = false;
			Functions.handleInterrupt();
			return;
		}
	}

	/**
	 * Metoda koñcowa zmieniaj¹ca stan konta w banku
	 * Dzia³a na osobnej blokadzie dostepu (w sumie ju¿ 3 u¿ytej w programie).
	 * Teoretycznei nie ma mo¿liwoœci wywo³ania tej metody gdy gotowki jest mniej ni¿ 0
	 * ale w razie b³êdu zwraca wartoœæ false
	 * @param cash
	 * @param process
	 * @return
	 */
	public synchronized boolean set(int cash, Process process) {
		boolean result = true;

		writeOperation.lock();
		try {

			if ((this.cash + cash) < 0)
				result = false;
			else {

				/*
				 * Aktualizuje stan konta Sprawdza czy by³a to WP£ATA JE¯ELI
				 * TAK: wysy³a sygna³ do kolejki, ¿eby sprawdzi³a czy jakiœ
				 * proces czeka i wyp³acila mu gotówke jesli mo¿liwe
				 */

				this.cash += cash;
				if (cash > 0) {
					notify();

					if (Bank.PAYIN_SOUND)
						new AePlayWave(System.getProperty("user.dir")
								+ "/files/CASHREG.WAV").start();
				}

				Functions.println(String.format(
						"[BANK] Klient %s %s [%d]. Aktualny stan konta: [%d]",
						process.getName(), (cash < 0) ? "WYP£ACI£" : "WP£ACI£",
						cash, this.cash));

				process.setSummary(cash);
				MainFrame.showSummaries(bankSimulator.getProcesses());
				LineChart.updateDataSet(this.cash);

				try {
					sleep(Functions.random(BANK_SPEED - BANK_SPEED / 3,
							BANK_SPEED + BANK_SPEED / 3 + 1) + 5);
				} catch (InterruptedException e) {
					isRunning = false;
					Functions.handleInterrupt();

				}
			}

		} finally {
			writeOperation.unlock();
		}

		return result;
	}

	/**
	 * Pobiera aktualna gotówkê na koncie
	 * @return
	 */
	public int getCash() {
		readOperation.lock();
		try {

			return this.cash;

		} finally {
			readOperation.unlock();
		}
	}

	public BlockingQueue<Process> getBlockingQueue() {
		return blockingQueue;
	}

}
