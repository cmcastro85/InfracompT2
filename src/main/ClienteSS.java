package main;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

public class ClienteSS {

	protected KeyPair key;
	protected byte[] certif;

	public ClienteSS() {
		try {
			key = Seguridad.llaves();
			certif = Seguridad.getCertificado(key).getEncoded();
		} catch (CertificateEncodingException e) {
			System.out.println("No se creo la llave");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Scanner sn = new Scanner(System.in);
			InetAddress addr = InetAddress.getByName("localhost");
			System.out.println("Iniciando Cliente sin seguridad\nIngrese el puerto del servidor: ");
			int port = Integer.parseInt(sn.nextLine());
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket sc = new Socket();

			sc.connect(sockaddr);
			ClienteSS cl = new ClienteSS();

			//Iniciación
			PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);
			BufferedReader bf = new BufferedReader(new InputStreamReader(sc.getInputStream()));


			System.out.println("Comienza la conexion");
			//HOLA
			String consola;

			pw.println("HOLA");
			String lectura = bf.readLine();
			if(!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			//ALGS
			System.out.println("¿Que algoritmos desea usar ?");
			String a1 = sn.nextLine();
			String a2 = sn.nextLine();
			String a3 = sn.nextLine();
			consola = "ALGORITMOS" + ":" + a1 + ":" +a2 + ":" +a3;
			pw.println(consola);
			System.out.println(consola);
			lectura = bf.readLine();
			if(!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Algoritmos OK");
			//CERTIFICADOS
			pw.println(cl.toHexString(cl.certif));

			lectura = bf.readLine();
			if(!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}

			lectura = bf.readLine();
			pw.println("OK");
			System.out.println("Certificados OK");
			//LLAVE SIMETRICA
			lectura = bf.readLine();
			pw.println("LS");

			lectura = bf.readLine();
			if(!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Llave OK");
			//VERIFICACION DE CUENTA

			System.out.println("asdf");
			int cuentaI = cl.numeroCuenta();
			String cuenta = cuentaI +"";
			pw.println(cuenta);
			pw.println(cuenta);

			lectura = bf.readLine();
			if(lectura.equals("OK:DEBE")) {
				System.out.println("Debe");
			}
			else if(lectura.equals("OK:PAZYSALVO")) {
				System.out.println("Paz y Salvo");
			}
			else {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Final OK");
			//Final
			sc.close();
			sn.close();
			pw.close();
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	private int numeroCuenta() {
		return (int) (Math.random() * (1000 - 100)) + 100;
	}



}




