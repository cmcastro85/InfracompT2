package gloadTest;

import java.io.IOException;

public class Main extends Thread{

	
	public static String IP = "172.24.41.148";

	public static int puerto = 8085;

	public static String a1 = "AES";

	public static String a2 = "RSA";

	public static String a3 = "HMACSHA256";
	
	public static void main(String[] args) {
		
		probar(10,20,10,1);
		System.exit(0);
	}
	
	public static void probar(int test, int gap, int loops, int threads) {
		System.out.println("pruebas: \nt:" + test + "\ng:" + gap + "\nc:" + threads + "\n Loops: " + loops);
		for(int i = 0; i < loops; i++) {
			String name = "data CS t" + test + "g" + gap + "c" + threads + " try"+i + ".csv";
			try {
				Generator gen = new Generator(test, gap, "./data/" + name, 1);
				while(!gen.ready()) {
					sleep(1000L);
				}
				System.out.println("End Prueba " + (i+1));
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("End Pruebas");
	}

}
