package gloadTest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorSS {


	public static String IP = "172.24.41.148";

	public static int puerto = 8085;

	public static String a1 = "AES";

	public static String a2 = "RSA";

	public static String a3 = "HMACSHA256";

	private LoadGenerator generator;

	private int numTasks;

	private int gapTasks;

	private String test = "";

	private PrintWriter pw;
	/**
	 * Tiempos, se guardan de la forma <id>,<tiempoVerificacion>,<tiempoConsulta>
	 */

	public GeneratorSS(int tasks, int gap, String rutaCSV, int threads) throws IOException, InterruptedException {

		ended = 0;
		System.out.println("Iniciando prueba de carga a " + IP + ":" + puerto + 
				"\nEl server tiene " + threads + " threads en el pool");
		numTasks = tasks;
		ready = false;
		gapTasks = gap;

		Task work = createTask();

		File f = new File(rutaCSV);
		System.out.println(rutaCSV);
		if(!f.exists()) f.createNewFile();
		PrintWriter pw = new PrintWriter(f);
		this.pw = pw;
		pw.println("Tasks: " + tasks);
		pw.println("Gap: " + gapTasks);
		pw.println("Threads: " + threads);
		pw.println("Verificacion ns,Consulta ms");
		
		System.out.println("Tasks: " + tasks);
		System.out.println("Gap: " + gapTasks);
		System.out.println("Threads: " + threads);

		generator = new LoadGenerator("Cliente Servidor Load Test", numTasks, work, gapTasks);
		generator.generate();




	

	}

	private int ended;
	
	private boolean ready;

	public void end() {
		synchronized (test) {
			ended++;
			if(ended == numTasks) {
				pw.println("Numero de fallos: " + fails);
				pw.close();
				System.out.println("End Test");
				ready = true;
			}
		}
	}
	
	public boolean ready() {
		return ready;
	}

	public synchronized void printTime(String time) {
		//System.out.println("print: " + time);
		pw.println(time);
		pw.flush();
	}

	private Task createTask() {
		return new ClienteSSTask(IP, puerto, 0, a1, a2, a3, this);
	}
	
	/*
	public static void main(String[] args) {
			System.out.println("Corriendo 10 pruebas de 400, 20, 8");
			for(int i = 0; i < 1; i++) {
				try {
				String name = "data CS t400g20c8 try"+i + ".csv";
				Generator gen = new Generator(400, 20, "./data/" + name, 8);
				System.out.println("\n\nFin Prueba " + (i+1));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		System.out.println("fin 10 pruebas!!!!!!!!!!!!!!!!!!!!!!!!");
		//System.exit(0);
	}
	*/

	private int fails;

	public void  fail() {
		fails ++;
	}

}
