import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Functions {

	public static void print(String str){
	    System.out.print(str);
	    MainFrame.addLogs(str);
	  }	
	public static void println(final String str){
	    System.out.println(str);
	    MainFrame.addLogs(str+"\n");
	   
	  }	
	public static void sleep(int time){
		 try {
			 Thread.sleep(time);
		 } catch (InterruptedException e) {
			 System.out.println("B³¹d");
		 }
	}
	public static int random(int min, int max){
		return (new Random().nextInt(max-min)+min);
	}
	public static synchronized void playSound(final String url) {
		  new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run() {
		      try {
		        
		        //Functions.println(Projekt.class.getResourceAsStream("C:\Users\Andrzej\Desktop\Programowanie\Workspace\projekt\src\" + url));
		        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
		        		new File("C:/Users/Andrzej/Desktop/Programowanie/Workspace/projekt/src/" + url));
		        Clip clip = AudioSystem.getClip();
		        clip.open(inputStream);
		        clip.start(); 
		      } catch (Exception e) {
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}
	public static void handleInterrupt(){
		if (Projekt.DEBUG_MODE)
			Functions.println(String.format(
					"[%s.%s] Przerwano dzia³anie w¹tku [%s].",
					Thread.currentThread().getClass().getName(), Thread.currentThread().getStackTrace()[2].getMethodName(), // [2] Bo ostatnia funkcja
					Thread.currentThread().getName()));
	}
}
