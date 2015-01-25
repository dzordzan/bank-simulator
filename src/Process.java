import java.util.Random;

/**
 * @author Andrzej Piszczek
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
	
	/**
	 * Zmienna przechowywuj¹ca czy aktualny proces jest zapauzowany i oczekuje na mo¿liwoœc wyp³aty.
	 */
	private boolean suspended = false;
	/**
	 * Zmienna przechowywuj¹ca ile potrzebuje proces wywp³aciæ z banku.
	 */
	private int needsToPayout;
	/**
	 * Suma wyp³at.
	 */
	private int summaryPayout;
	/**
	 * Suma wp³at.
	 */
	private int summaryPayin;


	/**
	 * Tworzy nowy proces, który symuluje dzia³ania klienta.
	 * @param account
	 */
	public Process(Bank account) {
		 this.account = account;
	 }
	

	/**
	 * Metoda wyp³acaj¹ca gotówka z konta.
	 * Synchronizowana w celu mo¿liwoœci pauzowania.
	 * @param cash
	 */
	public synchronized void payout(int cash){
		//Ustawia now¹ wartoœæ ile potrzebuje do wyp³aty, ¿eby inna klasa j¹ mog³¹ odczytaæ (bank)
		this.needsToPayout = cash;
		
		//Dodaj proces do kolejki, kolejka automatycznie blokuje ten w¹tek
		account.addToQueue(this);
		
		//Czekaj az a¿ bank wyp³aci gótówkê
		while (suspended)
			try {
				wait();
				
			} catch (InterruptedException e) {
				Functions.handleInterrupt();
				
				return;
			}
		
	}
	
	/**
	 * Metoda wp³acaj¹ca gotówke do banku.
	 * 
	 * Problem jest, ze dzia³a na tym samym "bankomacie" co metoda wyplacaj¹ca
	 * Dlatego je¿eli szykoœc bankomatu jest ustawiona na powolne dzia³anie
	 * schemat dzia³ania bedzie wygl¹da³: seria wyp³at - seria wp³at - seria wyp³at
	 * a schemat powinien wygladaæ: wyp³ata - wyp³ata -...- wp³ata - wyp³ata -..- wp³ata
	 * Wp³aty nie powinny byæ "LOCKowane" do bankomatu ( dostep do zmiennych bankowych dzia³a 
	 * na ReentrantReadWriteLock )
	 * Rozwi¹zanie tego problemu jest skomplikowane i czasoch³onne dlatego zostawi³em tak jak teraz jest.
	 * @param amount
	 */
	void payin(int amount){
		 account.set(amount, this);
	 }
	

	/**
	 * Pauzuje lub powiadamia w¹tek (notify)
	 * @param suspended
	 */
	public synchronized void waitForPayout(boolean suspended){
		this.suspended = suspended;
		if (!suspended)
			notify();
	}

	
	 /* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		 while (!this.isInterrupted()) {
			 try {
				sleep(Functions.random(5 * PROCESS_SPEED, 11 * PROCESS_SPEED + 15));

			 
			 if (new Random().nextInt(100)<PAYIN_RATIO)
				 payin( Functions.random(
						 Math.round(AVERAGE_PAYIN-AVERAGE_PAYIN/2), 
						 Math.round(AVERAGE_PAYIN+AVERAGE_PAYIN/2)) );
			  else
				 payout( Functions.random( 
						 Math.round(AVERAGE_PAYOUT-AVERAGE_PAYOUT/2), 
						 Math.round(AVERAGE_PAYOUT+AVERAGE_PAYOUT/2)) );
			 
			 } catch (InterruptedException e) {
				 Functions.handleInterrupt();
				 return;
				 }

			 }
		 if (Projekt.DEBUG_MODE)
		 	Functions.println(String.format(
		 			"[%s.%s] W¹tek [%s] zakoñcz³ prace.",
		 			this.getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(),
		 			this.getName()));
	 }


	/**
	 * @return
	 */
	public int getNeedsToPayout(){
		return needsToPayout;
	}
	/**
	 * @return
	 */
	public int getSummaryPayout() {
		return summaryPayout;
	}
	/**
	 * @return
	 */
	public int getSummaryPayin() {
		return summaryPayin;
	}
	/**
	 * @param summary
	 */
	public void setSummary(int summary) {		
		if (summary<0)
			this.summaryPayout += summary; else
				this.summaryPayin += summary;
	}
}
