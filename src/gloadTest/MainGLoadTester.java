package gloadTest;

import java.io.File;
import java.io.IOException;

public class MainGLoadTester extends Thread{

	
	public static String IP = "172.24.41.148";

	public static int puerto = 8085;

	public static String a1 = "AES";

	public static String a2 = "RSA";

	public static String a3 = "HMACSHA256";
	
	public static void main(String[] args) {
		
		probar(400,20,10,1);
		probar(200,40,10,1);
		probar(80,100,10,1);
		System.exit(0);
	}
	
	public static void probar(int test, int gap, int loops, int threads) {
		System.out.println("pruebas: \nt:" + test + "\ng:" + gap + "\nc:" + threads + "\nLoops: " + loops);
		for(int i = 0; i < loops; i++) {
			String name = "datosCSconT" + test + "G" + gap + "C" + threads + "/try"+i + ".csv";
			String dir = "./data/datosCSconT" + test + "G" + gap + "C" + threads;
			File f = new File(dir);
			if(!f.exists()) f.mkdirs();
			try {
				Generator gen = new Generator(test, gap, "./data/" + name, 1);
				while(!gen.ready()) {
					sleep(3000L);
				}
				System.out.println("\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1) + "\nEnd Prueba " + (i+1));
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas"+"\nEnd Pruebas");
	}

}
