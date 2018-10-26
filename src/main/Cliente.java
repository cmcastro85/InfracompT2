package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
			InetAddress addr = InetAddress.getByName("localhost");
		    int port = 9090;
		    SocketAddress sockaddr = new InetSocketAddress(addr, port);
			Socket sc = new Socket();
			
			sc.connect(sockaddr);
			Cliente cl = new Cliente();
			
			//Iniciación
			PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);
			BufferedReader bf = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			Scanner sn = new Scanner(System.in);
			
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
			byte[] certfBytes = new byte['?'];
			certfBytes = cl.toByteArray(lectura);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certfBytes);
			X509Certificate certificadoS = (X509Certificate) factory.generateCertificate(in);
			if(!Seguridad.verificarCertificado(certificadoS)) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			pw.println("OK");
			System.out.println("Certificados OK");
			//LLAVE SIMETRICA
			lectura = bf.readLine();
			byte[] simByEnc = new byte['?'];
			simByEnc = cl.toByteArray(lectura);
			byte[] simB = Seguridad.desencriptarAsimetrico(simByEnc, cl.key.getPrivate(), a2);
			SecretKey sim = new SecretKeySpec(simB, a1);
			
			byte[] simBE2 = Seguridad.encriptarAsimetrico(sim.getEncoded(), certificadoS.getPublicKey(), a2);
			pw.println(cl.toHexString(simBE2));
			
			lectura = bf.readLine();
			if(!lectura.equals("OK")) {
				System.out.println("Recibio " + lectura);
				pw.println("ERROR");
				sc.close();
			}
			System.out.println("Llave OK");
			//VERIFICACION DE CUENTA
			boolean pazysalvo = false;
			
			while(!pazysalvo) {
				int cuentaI = cl.numeroCuenta();
				String cuenta = cuentaI +"";
				byte[] cuentaCifrada = Seguridad.encriptarSimetrico(cl.toByteArray(cuenta), sim, a1);
				pw.println(cl.toHexString(cuentaCifrada));
				
				byte[] hmac = Seguridad.mac(cl.toByteArray(cuenta), sim, a3);
				pw.println(cl.toHexString(hmac));
				
				lectura = bf.readLine();
				if(lectura.equals("OK:DEBE")) {
					pazysalvo = false;
				}
				if(lectura.equals("OK:PAZYSALVO")) {
					pazysalvo = true;
				}
				else {
					System.out.println("Recibio " + lectura);
					pw.println("ERROR");
					sc.close();
				}
			}
			System.out.println("Final OK");
			//Final
			sc.close();
			sn.close();
			pw.close();
			bf.close();
		} catch (IOException | CertificateException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
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
		return (int) (Math.random() * (1000 - 100)) + 100;
	}
	
}
