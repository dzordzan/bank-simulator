import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class Projekt {

	
	public static boolean DEBUG_MODE = false;
	
	
	private static String[] clients = {
	"Anna","Piotr","Maria","Krzysztof","Katarzyna","Andrzej","Ma³gorzata","Jan"};
	/*"Agnieszka","Stanis³aw","Barbara","Tomasz","Krystyna","Pawe³","Ewa","Marcin",
	"El¿bieta","Micha³","Zofia","Marek","Teresa","Grzegorz","Magdalena","Józef",
	"Joanna","£ukasz","Janina","Adam","Monika","Zbigniew","Danuta","Jerzy","Jadwiga",
	"Tadeusz","Aleksandra","Mateusz","Halina","Dariusz","Irena","Mariusz"};*/


	private static Bank account;
	private static Process[] processes;
	private static boolean running = false;
	private static BlockingQueue<Process> blockingQueue;
	
	
	
	
	
	
	public static void start(){
		running = true;
		
		// Wielkoœæ kolejki to iloœæ klientów
		blockingQueue = new ArrayBlockingQueue<Process>(clients.length);
		
		// Otwieramy nowe konto
		// Konto to w¹tek, który sprawdza ca³y czas, czy jakiœ proces nie chce wyp³aciæ gotówki i zarz¹dza kolejk¹
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
			
			
			
			Functions.println("[Projek] Wys³ano sygna³ stopu do wszystkich procesów");
			
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
 * N - procesów
 * Ka¿dy ma dostep do wspólnego konta bankowego
 * Proces mo¿e wp³acaæ/wyp³acaæ
 * 
 * Nie mo¿e byæ debetu
 * Nie mo¿na wyp³aciæ wiêcej ni¿ jest na koncie
 * *** Proces czeka, a¿ iloœæ œrodków na koncie bêdzie równa, b¹dŸ wiêksza ile ma zamiar wyp³aciæ
 * *** Kolejkowanie 
 * *** *** Czy kase dostanie ten, który najd³u¿ej czeka w kolejce, czy ten dla którego jest odpowiednia iloœæ kasy
 * Niezale¿ne wp³aty odœwie¿aj¹ce stan konta
 * 
 * 
 */
