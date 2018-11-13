package clientes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Cliente {

	protected KeyPair key;
	protected byte[] certif;

	public Cliente() {
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
			System.out.println("Iniciando Cliente con seguridad\nIngrese el puerto del servidor: ");
			int port = Integer.parseInt(sn.nextLine());
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket sc = new Socket();

			sc.connect(sockaddr);
			Cliente cl = new Cliente();

			// Iniciación
			PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);
			BufferedReader bf = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			

			System.out.println("Comienza la conexion");
			// HOLA
			String consola;

			pw.println("HOLA");
			String lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			// ALGS
			System.out.println("¿Que algoritmos desea usar ?");
			String a1 = sn.nextLine();
			String a2 = sn.nextLine();
			String a3 = sn.nextLine();
			consola = "ALGORITMOS" + ":" + a1 + ":" + a2 + ":" + a3;
			pw.println(consola);
			System.out.println(consola);
			lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Algoritmos OK");
			// CERTIFICADOS
			pw.println(cl.toHexString(cl.certif));

			lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			
			lectura = bf.readLine();
			byte[] certfBytes = new byte['?'];
			certfBytes = cl.toByteArray(lectura);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certfBytes);
			X509Certificate certificadoS = (X509Certificate) factory.generateCertificate(in);
			pw.println("OK");
			System.out.println("Certificados OK");
			// LLAVE SIMETRICA
			lectura = bf.readLine();
			byte[] simByEnc = new byte['?'];
			simByEnc = cl.toByteArray(lectura);
			byte[] simB = Seguridad.desencriptarAsimetrico(simByEnc, cl.key.getPrivate(), a2);
			SecretKey sim = new SecretKeySpec(simB, a1);

			byte[] simBE2 = Seguridad.encriptarAsimetrico(sim.getEncoded(), certificadoS.getPublicKey(), a2);
			pw.println(cl.toHexString(simBE2));

			lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Llave OK");
			// VERIFICACION DE CUENTA

			int cuentaI = cl.numeroCuenta();
			String cuenta = cuentaI + "";
			byte[] cuentaCifrada = Seguridad.encriptarSimetrico(cuenta.getBytes(), sim, a1);
			pw.println(cl.toHexString(cuentaCifrada));

			byte[] hmac = Seguridad.mac(cuenta.getBytes(), sim, a3);
			pw.println(cl.toHexString(hmac));

			lectura = bf.readLine();
			if (lectura.equals("OK:DEBE")) {
				System.out.println("Debe");
			} else if (lectura.equals("OK:PAZYSALVO")) {
				System.out.println("Paz y salvo");
			} else {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Final OK");
			// Final
			sc.close();
			sn.close();
			pw.close();
			bf.close();
		} catch (IOException | CertificateException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	private byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}

	private int numeroCuenta() {
		return (int) (Math.random() * (9999 - 1000)) + 1000;
	}

	
	
	private long startVer;
	
	private long endVer;
	
	private long startConsulta;
	
	private long endConsulta;
	
	public void comenzarTransaccion(String ip, int id, int puerto, String algo1, String algo2, String algo3) throws Exception {
			//Scanner sn = new Scanner(System.in);
			InetAddress addr = InetAddress.getByName(ip);
			//System.out.println("Iniciando Cliente sin seguridad\nIngrese el puerto del servidor: ");
			//int port = Integer.parseInt(sn.nextLine());
			int port = puerto;
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket sc = new Socket();

			sc.connect(sockaddr);
			Cliente cl = new Cliente();

			// Iniciación
			PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);
			BufferedReader bf = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			

			System.out.println("Comienza la conexion con: " + ip + ":" + puerto);
			// HOLA
			String consola;

			pw.println("HOLA");
			
			//------------------------------------
			//START CONSULTA
			//------------------------------------
			startConsulta = System.nanoTime();
			
			String lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
				throw new Exception("ERROR");
			}
			
			// ALGS
			//System.out.println("¿Que algoritmos desea usar ?");
//			String a1 = sn.nextLine();
//			String a2 = sn.nextLine();
//			String a3 = sn.nextLine();
			String a1 = algo1;
			String a2 = algo2;
			String a3 = algo3;
			consola = "ALGORITMOS" + ":" + a1 + ":" + a2 + ":" + a3;
			pw.println(consola);
			//System.out.println(consola);
			lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
				throw new Exception("ERROR");
			}
			//System.out.println("Algoritmos OK");
			// CERTIFICADOS
			pw.println(cl.toHexString(cl.certif));

			lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
				throw new Exception("ERROR");
			}
			
			lectura = bf.readLine();
			
			//------------------------------------
			//START VERIFICACION
			//------------------------------------
			
			startVer = System.nanoTime();
			
			byte[] certfBytes = new byte['?'];
			certfBytes = cl.toByteArray(lectura);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certfBytes);
			X509Certificate certificadoS = (X509Certificate) factory.generateCertificate(in);
			pw.println("OK");
			//System.out.println("Certificados OK");
			
			
			
			// LLAVE SIMETRICA
			lectura = bf.readLine();
			byte[] simByEnc = new byte['?'];
			simByEnc = cl.toByteArray(lectura);
			byte[] simB = Seguridad.desencriptarAsimetrico(simByEnc, cl.key.getPrivate(), a2);
			SecretKey sim = new SecretKeySpec(simB, a1);

			byte[] simBE2 = Seguridad.encriptarAsimetrico(sim.getEncoded(), certificadoS.getPublicKey(), a2);
			pw.println(cl.toHexString(simBE2));

			lectura = bf.readLine();
			if (!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
				throw new Exception("ERROR");
			}
			//System.out.println("Llave OK");
			
			//------------------------------------
			//END VERIFICACION
			//------------------------------------
			
			
			endVer = System.nanoTime();
			// VERIFICACION DE CUENTA

			int cuentaI = cl.numeroCuenta();
			String cuenta = cuentaI + "";
			byte[] cuentaCifrada = Seguridad.encriptarSimetrico(cuenta.getBytes(), sim, a1);
			pw.println(cl.toHexString(cuentaCifrada));

			byte[] hmac = Seguridad.mac(cuenta.getBytes(), sim, a3);
			pw.println(cl.toHexString(hmac));

			lectura = bf.readLine();
			if (lectura.equals("OK:DEBE")) {
				//System.out.println("Debe");
			} else if (lectura.equals("OK:PAZYSALVO")) {
				//System.out.println("Paz y salvo");
			} else {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
				throw new Exception("ERROR");
			}
			//------------------------------------
			//END CONSULTA
			//------------------------------------
			endConsulta = System.nanoTime();
			
			System.out.println("Final OK");

			
			
			
			// Final
			sc.close();
			//sn.close();
			pw.close();
			bf.close();
		
	}
	
	/**
	 * retorna la duracion en MS de la verificacion
	 * @return
	 */
	public long getVerDuration() {
		return (endVer-startVer);
	}
	
	/**
	 * retorna la duracion en MS de la verificacion
	 * @return
	 */
	public long getConsultaDuration() {
		return (endConsulta-startConsulta)/1000000;
	}

}
