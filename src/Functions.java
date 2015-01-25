import java.util.Random;


public class Functions {

	/**
	 * @param str
	 */
	public static void print(String str){
	    System.out.print(str);
	    MainFrame.addLogs(str);
	  }	
	/**
	 * @param str
	 */
	public static void println(final String str){
	    System.out.println(str);
	    MainFrame.addLogs(str+"\n");
	   
	  }	
	/**
	 * @param time
	 */
	public static void sleep(int time){
		 try {
			 Thread.sleep(time);
		 } catch (InterruptedException e) {
			 System.out.println("B³¹d");
		 }
	}
	/**
	 * @param min Minimalna wartoœc losowania
	 * @param max
	 * @return Test
	 */
	public static int random(int min, int max){
		return (new Random().nextInt(max-min)+min);
	}

	/**
	 * Obs³uguje przerwaania i wyœwietla to w konsoli
	 */
	public static void handleInterrupt(){
		if (Projekt.DEBUG_MODE)
			Functions.println(String.format(
					"[%s.%s] Przerwano dzia³anie w¹tku [%s].",
					Thread.currentThread().getClass().getName(), Thread.currentThread().getStackTrace()[2].getMethodName(), // [2] Bo ostatnia funkcja
					Thread.currentThread().getName()));
	}
}
