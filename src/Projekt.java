import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class Projekt {

	
	public static boolean DEBUG_MODE = false;
	
	
	private static String[] clients = {
	"Anna","Piotr","Maria","Krzysztof","Katarzyna","Andrzej","Ma�gorzata","Jan"};
	/*"Agnieszka","Stanis�aw","Barbara","Tomasz","Krystyna","Pawe�","Ewa","Marcin",
	"El�bieta","Micha�","Zofia","Marek","Teresa","Grzegorz","Magdalena","J�zef",
	"Joanna","�ukasz","Janina","Adam","Monika","Zbigniew","Danuta","Jerzy","Jadwiga",
	"Tadeusz","Aleksandra","Mateusz","Halina","Dariusz","Irena","Mariusz"};*/


	private static Bank account;
	private static Process[] processes;
	private static boolean running = false;
	private static BlockingQueue<Process> blockingQueue;
	
	
	
	
	
	
	public static void start(){
		running = true;
		
		// Wielko�� kolejki to ilo�� klient�w
		blockingQueue = new ArrayBlockingQueue<Process>(clients.length);
		
		// Otwieramy nowe konto
		// Konto to w�tek, kt�ry sprawdza ca�y czas, czy jaki� proces nie chce wyp�aci� got�wki i zarz�dza kolejk�
		account = new Bank(blockingQueue);
		account.start();

		
		processes = new Process[clients.length];
		
		for (int i=0; i<clients.length; i++){
			processes[i] = new Process(account, blockingQueue);
			processes[i].setName(clients[i]);
			processes[i].start();
		}
		
	}
	
	public static void stop(){
		if (running){	
			running = false;
			
			account.interrupt();
			for (int i=0; i<processes.length; i++)
				processes[i].interrupt();
			
			
			
			Functions.println("[Projek] Wys�ano sygna� stopu do wszystkich proces�w");
			
			/*for (Thread process : processes){
				try {
					process.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Functions.println("[Main] Wszystkie procesy zastopowane.");*/
		}
	}
	
	
	public static void main(String[] args) {
		 SwingUtilities.invokeLater(new Runnable() {
				 public void run() {
					 new MainFrame(clients);
				 }
			 });

	}
	
	public static Bank getAccount(){
		return account;
	}

	public static BlockingQueue<Process> getBlockingQueue(){
		return blockingQueue;
	}
	
	
	public static boolean isRunning(){
		return running;
	}

}



/**
 * 
 * N - proces�w
 * Ka�dy ma dostep do wsp�lnego konta bankowego
 * Proces mo�e wp�aca�/wyp�aca�
 * 
 * Nie mo�e by� debetu
 * Nie mo�na wyp�aci� wi�cej ni� jest na koncie
 * *** Proces czeka, a� ilo�� �rodk�w na koncie b�dzie r�wna, b�d� wi�ksza ile ma zamiar wyp�aci�
 * *** Kolejkowanie 
 * *** *** Czy kase dostanie ten, kt�ry najd�u�ej czeka w kolejce, czy ten dla kt�rego jest odpowiednia ilo�� kasy
 * Niezale�ne wp�aty od�wie�aj�ce stan konta
 * 
 * 
 */
