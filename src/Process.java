import java.util.Random;

/**
 * @author Andrzej Piszczek
 *
 */
public class Process extends Thread {//implements Runnable { 
	
	/*
	 * STA�E zmieniane dla wszystkich proces�w
	 *
	 */
	public static int PROCESS_SPEED = 1;
	public static int PAYIN_RATIO = 10;
	public static int AVERAGE_PAYIN = 4000;
	public static int AVERAGE_PAYOUT = 500;
	

	private Bank account;
	
	/**
	 * Zmienna przechowywuj�ca czy aktualny proces jest zapauzowany i oczekuje na mo�liwo�c wyp�aty.
	 */
	private boolean suspended = false;
	/**
	 * Zmienna przechowywuj�ca ile potrzebuje proces wywp�aci� z banku.
	 */
	private int needsToPayout;
	/**
	 * Suma wyp�at.
	 */
	private int summaryPayout;
	/**
	 * Suma wp�at.
	 */
	private int summaryPayin;


	/**
	 * Tworzy nowy proces, kt�ry symuluje dzia�ania klienta.
	 * @param account
	 */
	public Process(Bank account) {
		 this.account = account;
	 }
	

	/**
	 * Metoda wyp�acaj�ca got�wka z konta.
	 * Synchronizowana w celu mo�liwo�ci pauzowania.
	 * @param cash
	 */
	public synchronized void payout(int cash){
		//Ustawia now� warto�� ile potrzebuje do wyp�aty, �eby inna klasa j� mog�� odczyta� (bank)
		this.needsToPayout = cash;
		
		//Dodaj proces do kolejki, kolejka automatycznie blokuje ten w�tek
		account.addToQueue(this);
		
		//Czekaj az a� bank wyp�aci g�t�wk�
		while (suspended)
			try {
				wait();
				
			} catch (InterruptedException e) {
				Functions.handleInterrupt();
				
				return;
			}
		
	}
	
	/**
	 * Metoda wp�acaj�ca got�wke do banku.
	 * 
	 * Problem jest, ze dzia�a na tym samym "bankomacie" co metoda wyplacaj�ca
	 * Dlatego je�eli szyko�c bankomatu jest ustawiona na powolne dzia�anie
	 * schemat dzia�ania bedzie wygl�da�: seria wyp�at - seria wp�at - seria wyp�at
	 * a schemat powinien wyglada�: wyp�ata - wyp�ata -...- wp�ata - wyp�ata -..- wp�ata
	 * Wp�aty nie powinny by� "LOCKowane" do bankomatu ( dostep do zmiennych bankowych dzia�a 
	 * na ReentrantReadWriteLock )
	 * Rozwi�zanie tego problemu jest skomplikowane i czasoch�onne dlatego zostawi�em tak jak teraz jest.
	 * @param amount
	 */
	void payin(int amount){
		 account.set(amount, this);
	 }
	

	/**
	 * Pauzuje lub powiadamia w�tek (notify)
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
		 			"[%s.%s] W�tek [%s] zako�cz� prace.",
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
