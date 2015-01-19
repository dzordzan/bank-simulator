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
					 * Sprawd� czy pierwszy oczekuj�cy proces mo�e wyp�aci� got�wk�
					 * Je�eli NIE mo�e metoda checkQueue zostaje zapauzowana i oczekuje na sygna� wp�aty
					 * Je�eli MO�E wyp�aci� kontynuuje dalej wykonywanie metody
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
					 * Zdejmuje najd�u�ej czekaj�cy prroces
					 * Zmienia stan konta
					 * Wy�wietla informacje na form� o kolejce
					 * 
					 */
					Process waitingProcess = blockingQueue.take();
					MainFrame.showQueue2( blockingQueue.toArray() );
					this.set(-waitingProcess.getNeedsToPayout(), waitingProcess);
					
					/*
					 * Wysy�a sygna� do procesu (klienta), kt�ry doda� si� do kolejki po wyp�ate
					 * 
					 * Process pauzuje si� sam po dodaniu do kolejki
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
				 * Teoretycznie, czeka gdyby kolejka si� przep�ni�a (ale nie ma prawa do tego doj��)
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
					  * Sprawdza czy by�a to WP�ATA
					  * 	JE�ELI TAK: wysy�a sygna� do kolejki, �eby sprawdzi�a czy jaki� proces czeka i wyp�acila mu got�wke jesli mo�liwe
					  */
	
					 this.cash += cash;
					 if (cash> 0) 
						 notify();
					 
					 //new AePlayWave( System.getProperty("user.dir")+"/src/CASHREG.WAV").start();
	
					 Functions.println(String.format(
							 "[BANK] Klient %s %s [%d]. Aktualny stan konta: [%d]", 
							 	process.getName(), (cash<0)?"WYP�ACI�":"WP�ACI�",
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
			// Functions.println("[BankAccount] "+name+" wp�aca "+amount+" z�");
			 // Za��my, ze bankomat przetwarza 2 sekundy dodawanie got�wki na konto
			 Functions.sleep(100);
			 new AePlayWave( System.getProperty("user.dir")+"/src/CASHREG.WAV").start();
			 this.cash += amount;
			 LineChart.updateDataSet(cash);
			 Functions.println("[BankAccount] "+name+" wp�aci� "+amount+" z�. Stan konta:"+this.getInfo() );
			 
			 
			 notify();
		 	this.operationsHistory.add(name+" wp�aci� na konto "+ Integer.toString(amount) + "z�. Stan konta:"+this.getInfo());
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
