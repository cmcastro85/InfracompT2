package gloadTest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {

	
	public static String IP = "localhost";
	
	public static int puerto = 8081;
	
	public static String a1 = "AES";
	
	public static String a2 = "RSA";
	
	public static String a3 = "HMACSHA256";
	
	private LoadGenerator generator;
	
	private int numTasks;
	
	private int gapTasks;
	
	/**
	 * Tiempos, se guardan de la forma <id>,<tiempoVerificacion>,<tiempoConsulta>
	 */
	private ArrayList<String> tiempos;
	
	public Generator(int tasks, int gap, String rutaCSV, int threads) throws IOException, InterruptedException {
		
		System.out.println("Iniciando prueba de carga a " + IP + ":" + puerto + 
				"\nEl server tiene " + threads + " threads en el pool");
		tiempos = new ArrayList<>();
		numTasks = tasks;
		
		gapTasks = gap;
		
		Task work = createTask();
		
		generator = new LoadGenerator("Cliente Servidor Load Test", numTasks, work, gapTasks);
		generator.generate();
		System.out.println("\n\n\n\n\n\n\nAfterGenerate\n\n\n\nn\nn\n\nn\nn\n\nn\n");
		
		File f = new File(rutaCSV);
		if(!f.exists()) f.createNewFile();
		PrintWriter pw = new PrintWriter(f);
		pw.println("Tasks: " + tasks);
		pw.println("Gap: " + gapTasks);
		pw.println("Threads: " + threads);
		for(String s: tiempos) {
			pw.println(s);
		}
		pw.println("Numero de fallos: " + fails);
		
		pw.close();
	}
	
	
	private Task createTask() {
		return new ClienteCSTask(IP, puerto, 0, a1, a2, a3, tiempos, this);
	}
	
	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			Generator gen = new Generator(10, 1000, "./data/testCSV.csv", 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int fails;
	
	public void  fail() {
		fails ++;
	}
	
}
