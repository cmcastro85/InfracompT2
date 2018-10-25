package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
		Socket sc = new Socket();
		Cliente cl = new Cliente();
		
		try {
			//Iniciación
			PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);
			BufferedReader bf = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			Scanner sn = new Scanner(System.in);
			
			//HOLA
			String consola;
			pw.print("HOLA");
			String lectura = bf.readLine();
			if(lectura != "OK") {
				pw.println("ERROR");
				sc.close();
			}
			//ALGS
			String a1 = sn.nextLine();
			String a2 = sn.nextLine();
			String a3 = sn.nextLine();
			consola = "ALGORITMOS:" + ":" + a1 + ":" +a2 + ":" +a3;
			pw.println(consola);
			lectura = bf.readLine();
			if(lectura != "OK") {
				pw.println("ERROR");
				sc.close();
			}
			//CERTIFICADOS
			pw.println(cl.toHexString(cl.certif));
			
			lectura = bf.readLine();
			if(lectura != "OK") {
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
				pw.println("ERROR");
				sc.close();
			}
			pw.println("OK");
			//ETAPA 4
			lectura = bf.readLine();
			byte[] simByEnc = new byte['?'];
			simByEnc = cl.toByteArray(lectura);
			byte[] simB = Seguridad.desencriptarSimetrico(simByEnc, certificadoS.getPublicKey(), a2);
			SecretKey sim = new SecretKeySpec(simB, a1);
			
			
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
	
}
