import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank extends Thread{
	 public static int BANK_SPEED = 100;
	 public static boolean PAYIN_SOUND;//, PAYOUT_SOUND;
	 private ReentrantReadWriteLock operationLock = new ReentrantReadWriteLock(true);
	 private Lock readOperation = operationLock.readLock();
	 private Lock writeOperation = operationLock.writeLock();
	
	
	 private BankSimulator bankSimulator;
	 private BlockingQueue<Process> blockingQueue;
	 
	 private int cash = 2099;
	 private boolean isRunning = true;
	  
	 
	 
	 public Bank (BankSimulator bankSimulator, BlockingQueue<Process> blockingQueue){
		 this.blockingQueue = blockingQueue;
		 this.bankSimulator = bankSimulator;
	 }
	 
	 public void run(){
		 while (isRunning){
			 checkQueue();
		 }
	 }
	 public synchronized void checkQueue(){
				try {
					if (blockingQueue.isEmpty())
						return;
					

					/*
					 * SprawdŸ czy pierwszy oczekuj¹cy proces mo¿e wyp³aciæ gotówkê
					 * Je¿eli NIE mo¿e metoda checkQueue zostaje zapauzowana i oczekuje na sygna³ wp³aty
					 * Je¿eli MO¯E wyp³aciæ kontynuuje dalej wykonywanie metody
					 * 
					 */
					while (blockingQueue.peek().getNeedsToPayout() > this.getCash()){
						Process firstProcess = blockingQueue.peek();
						
						if (Projekt.DEBUG_MODE)
							Functions.println(String.format("[%s.%s] Pierwszy w kolejce [%s, %d]. Stan konta: [%d]", 
									this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
									firstProcess.getName(), firstProcess.getNeedsToPayout(),
									this.getCash()
									));

						
						wait();
					}
					/*
					 * Zdejmuje najd³u¿ej czekaj¹cy prroces
					 * Zmienia stan konta
					 * Wyœwietla informacje na formê o kolejce
					 * 
					 */
					Process waitingProcess = blockingQueue.take();
					MainFrame.showQueue( blockingQueue.toArray() );
					this.set(-waitingProcess.getNeedsToPayout(), waitingProcess);
					
					/*
					 * Wysy³a sygna³ do procesu (klienta), który doda³ siê do kolejki po wyp³ate
					 * 
					 * Process pauzuje siê sam po dodaniu do kolejki
					 */
					waitingProcess.waitForPayout(false);
					
				} catch (InterruptedException e) {
					this.isRunning = false;
					Functions.handleInterrupt();
					e.printStackTrace();
				}
			
			
	 }
	 
	 public void addToQueue(Process process){
			try {
				/*
				 * Dodaje proces do kolejki
				 * Teoretycznie, czeka gdyby kolejka siê przep³ni³a (ale nie ma prawa do tego dojœæ)
				 */
				blockingQueue.put(process);
				MainFrame.showQueue(blockingQueue.toArray());
			} catch (InterruptedException e) {
				this.isRunning = false;
				Functions.handleInterrupt();
				return;
			}
	 }
	 
	 public synchronized boolean set(int cash, Process process){
		 boolean result = true;
		 
		 writeOperation.lock();
		 try {
			 
			 if ((this.cash + cash ) < 0 )
				 result = false; else {
					 
					 /*
					  * Aktualizuje stan konta
					  * Sprawdza czy by³a to WP£ATA
					  * 	JE¯ELI TAK: wysy³a sygna³ do kolejki, ¿eby sprawdzi³a czy jakiœ proces czeka i wyp³acila mu gotówke jesli mo¿liwe
					  */
	
					 this.cash += cash;
					 if (cash> 0) {
						 notify();
					 
						 if (Bank.PAYIN_SOUND)
							 new AePlayWave( System.getProperty("user.dir")+"/files/CASHREG.WAV").start();
					 }
	
					 Functions.println(String.format(
							 "[BANK] Klient %s %s [%d]. Aktualny stan konta: [%d]", 
							 	process.getName(), (cash<0)?"WYP£ACI£":"WP£ACI£",
							 	cash, this.cash));
							 
					 process.setSummary(cash);
					 MainFrame.showSummaries(bankSimulator.getProcesses());
					 LineChart.updateDataSet(this.cash);	
					 
					 try {
							sleep(Functions.random(
									BANK_SPEED-BANK_SPEED/3,
									BANK_SPEED+BANK_SPEED/3+1)+5);
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
	 

	
	 
	 public String getInfo(){
		 readOperation.lock();
		 try {
			 return Integer.toString(this.cash);

		 } finally {
			 readOperation.unlock();
		 }
		 
	 }
	 public int getCash(){
		 readOperation.lock();
		 try {
		
			 return this.cash;
		
		 } finally {
			 readOperation.unlock();
		 }	 
	 }
	 
}
