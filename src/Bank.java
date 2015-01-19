import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank extends Thread{
	 public static int BANK_SPEED = 100;
	 private ReentrantReadWriteLock operationLock = new ReentrantReadWriteLock(true);
	 private Lock readOperation = operationLock.readLock();
	 private Lock writeOperation = operationLock.writeLock();
	 
	 private List<String> operationsHistory = new ArrayList<String>();
	
	 
	 private BlockingQueue<Process> blockingQueue;
	 
	 private int cash = 2099;
	 
	 
	 
	 
	 public Bank (BlockingQueue<Process> blockingQueue){
		 this.blockingQueue = blockingQueue;
	 }
	 
	 public void run(){
		 while (true){
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
							Functions.println(String.format("[%s.%s] 1 w kolejce [%s, %d]. Stan konta: [%d]", 
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
					MainFrame.showQueue2( blockingQueue.toArray() );
					this.set(-waitingProcess.getNeedsToPayout(), waitingProcess);
					
					/*
					 * Wysy³a sygna³ do procesu (klienta), który doda³ siê do kolejki po wyp³ate
					 * 
					 * Process pauzuje siê sam po dodaniu do kolejki
					 */
					waitingProcess.waitForPayout(false);
					
				} catch (InterruptedException e) {
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
				MainFrame.showQueue2(blockingQueue.toArray());
			} catch (InterruptedException e) {
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
					 if (cash> 0) 
						 notify();
					 
					 //new AePlayWave( System.getProperty("user.dir")+"/src/CASHREG.WAV").start();
	
					 Functions.println(String.format(
							 "[BANK] Klient %s %s [%d]. Aktualny stan konta: [%d]", 
							 	process.getName(), (cash<0)?"WYP£ACI£":"WP£ACI£",
							 	cash, this.cash));
							 
					 process.setSummayPayout(cash);
					 LineChart.updateDataSet(this.cash);	
					 
					 try {
							sleep(Functions.random(
									BANK_SPEED-BANK_SPEED/3,
									BANK_SPEED+BANK_SPEED/3+1));
						} catch (InterruptedException e) {
							Functions.handleInterrupt();
							
						}
				 }
			 
		 } finally {
			 writeOperation.unlock();
		 }
		 
		 return result;
	 }
	 
	 public synchronized int add(int amount, String name) {
		 writeOperation.lock();
		 try {
			// Functions.println("[BankAccount] "+name+" wp³aca "+amount+" z³");
			 // Za³ó¿my, ze bankomat przetwarza 2 sekundy dodawanie gotówki na konto
			 Functions.sleep(100);
			 new AePlayWave( System.getProperty("user.dir")+"/src/CASHREG.WAV").start();
			 this.cash += amount;
			 LineChart.updateDataSet(cash);
			 Functions.println("[BankAccount] "+name+" wp³aci³ "+amount+" z³. Stan konta:"+this.getInfo() );
			 
			 
			 notify();
		 	this.operationsHistory.add(name+" wp³aci³ na konto "+ Integer.toString(amount) + "z³. Stan konta:"+this.getInfo());
		 	return cash;
		 } finally {
			 writeOperation.unlock();
		 }
		
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
