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
	 * Zmienna do bezpo�redniej obs�ugi kolejki. Operuj� przechowuj� klas�
	 * Process a dostep do niej ma konto bankowe. Konto bankowe sprawdza czy
	 * jest mozliwo�c wyp�aty dla najd�u�ej czekaj�cego procesu i go
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
				
		// Wielko�� kolejki to ilo�� klient�w
		this.bankSimulator = bankSimulator;
	}

	/** 
	 * Nieskonczona p�tla sprawdzaj�ca kolejk�
	 */
	public void run() {
		while (isRunning) {
			checkQueue();
		}
	}

	/**
	 * Sprawdzanie kolejki.
	 * Sprawdzanie jest skomplikowane wszystko wyt�umaczone w komentarzach.
	 */
	public synchronized void checkQueue() {
		try {
			if (blockingQueue.isEmpty())
				return;

			/*
			 * Sprawd� czy pierwszy oczekuj�cy proces mo�e wyp�aci� got�wk�
			 * Je�eli NIE mo�e metoda checkQueue zostaje zapauzowana i oczekuje
			 * na sygna� wp�aty Je�eli MO�E wyp�aci� kontynuuje dalej
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
			 * Zdejmuje najd�u�ej czekaj�cy prroces Zmienia stan konta Wy�wietla
			 * informacje na form� o kolejce
			 */
			Process waitingProcess = blockingQueue.take();
			MainFrame.showQueue(blockingQueue.toArray());
			this.set(-waitingProcess.getNeedsToPayout(), waitingProcess);

			/*
			 * Wysy�a sygna� do procesu (klienta), kt�ry doda� si� do kolejki po
			 * wyp�ate
			 * 
			 * Process pauzuje si� sam po dodaniu do kolejki
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

			// Pauzuj process, kt�ry wyowa�a� metode
			process.waitForPayout(true);
			/*
			 * Dodaje proces do kolejki. Teoretycznie, czeka gdyby kolejka si�
			 * przep�ni�a (ale nie ma prawa do tego doj��)
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
	 * Metoda ko�cowa zmieniaj�ca stan konta w banku
	 * Dzia�a na osobnej blokadzie dostepu (w sumie ju� 3 u�ytej w programie).
	 * Teoretycznei nie ma mo�liwo�ci wywo�ania tej metody gdy gotowki jest mniej ni� 0
	 * ale w razie b��du zwraca warto�� false
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
				 * Aktualizuje stan konta Sprawdza czy by�a to WP�ATA JE�ELI
				 * TAK: wysy�a sygna� do kolejki, �eby sprawdzi�a czy jaki�
				 * proces czeka i wyp�acila mu got�wke jesli mo�liwe
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
						process.getName(), (cash < 0) ? "WYP�ACI�" : "WP�ACI�",
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
	 * Pobiera aktualna got�wk� na koncie
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
