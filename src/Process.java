import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * 
 */

/**
 * @author Andrzejek
 *
 */
public class Process extends Thread {//implements Runnable { 
	
	/*
	 * STA£E zmieniane dla wszystkich procesów
	 *
	 */
	public static int PROCESS_SPEED = 1;
	public static int PAYIN_RATIO = 10;
	
	public static int AVERAGE_PAYIN = 4000;
	public static int AVERAGE_PAYOUT = 500;
	
	
	private Bank account;
	private boolean suspended = false;
	private int needsToPayout;
	
	
	private int summaryPayout;
	private int summaryPayin;
	protected BlockingQueue<Process> blockingQueue = null;

	public Process(Bank account, BlockingQueue<Process> blockingQueue) {
		 this.account = account;
		 this.blockingQueue = blockingQueue;
	 }
	
	public void payoutQueue(){
		synchronized (blockingQueue){
			try {
				
				
				//MainFrame.showQueue(blockingQueue.toArray());
				
				
				
				while (((Process) blockingQueue.toArray()[0]).getNeedsToPayout() > account.getCash()){
					Process firstProcess = (Process) blockingQueue.toArray()[0];
					
					Functions.println(String.format("[QUEUE] 1 w kolejce [%s, %d]. Kolejke wywo³a³: [%s, %d]. Konto: [%d]", 
							firstProcess.getName(), firstProcess.getNeedsToPayout(),
							getName(), getNeedsToPayout(),
							account.getCash()
							));
					
					
					blockingQueue.wait();
				}
					
				//Process firstProcess = (Process) blockingQueue.toArray()[0];
				Process wProcess = blockingQueue.take();
				
				//Functions.println(firstProcess.getProcessName() + "-  "+Integer.toString(firstProcess.getNeedsToPayout()) +" wProc: "+ wProcess.getProcessName());
				
				//MainFrame.showQueue(blockingQueue.toArray());
				
				account.set(-wProcess.needsToPayout, wProcess);
				wProcess.run();

			} catch (InterruptedException e) {
				Functions.handleInterrupt();
				
				return;
			}
		}
		
	}

	public synchronized void payout(int cash){
		this.needsToPayout = cash;
		
		account.addToQueue(this);
		waitForPayout(true);
		
		while (suspended)
			try {
				wait();
				
			} catch (InterruptedException e) {
				Functions.handleInterrupt();
				return;
			}
		
	}
	
	void payin(int amount){
		 account.set(amount, this);
	 }
	

	public synchronized void waitForPayout(boolean suspended){
		this.suspended = suspended;
		if (!suspended)
			notify();
	}

	
	public int getNeedsToPayout(){
		return needsToPayout;
	}
	
	 public void run() {
		 while (!this.isInterrupted()) {
			 try {
				sleep(Functions.random(5 * PROCESS_SPEED, 11 * PROCESS_SPEED + 15));
			 } catch (InterruptedException e) {
				 Functions.handleInterrupt();
				 return;
				 }
			 
			 if (new Random().nextInt(100)<PAYIN_RATIO)
				 payin( Functions.random(
						 Math.round(AVERAGE_PAYIN-AVERAGE_PAYIN/2), 
						 Math.round(AVERAGE_PAYIN+AVERAGE_PAYIN/2)) );
			  else
				 payout( Functions.random( 
						 Math.round(AVERAGE_PAYOUT-AVERAGE_PAYOUT/2), 
						 Math.round(AVERAGE_PAYOUT+AVERAGE_PAYOUT/2)) );

			 }
		 if (Projekt.DEBUG_MODE)
		 	Functions.println(String.format(
		 			"[%s.%s] W¹tek [%s] zakoñcz³ prace.",
		 			this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
		 			this.getName()));
	 }



	public int getSummaryPayout() {
		return summaryPayout;
	}
	public int getSummaryPayin() {
		return summaryPayin;
	}
	public void setSummary(int summary) {		
		if (summary<0)
			this.summaryPayout += summary; else
				this.summaryPayin += summary;
	}
}
