package main;

import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;

public class Cliente {

	Socket sc = null;
	KeyPair key;
	byte[] certif;
	
	public Cliente(Socket sc) {
		try {
			this.sc = sc;
			key = Seguridad.llaves();
			certif = Seguridad.getCertificado(key).getEncoded();
		} catch (CertificateEncodingException e) {
			System.out.println("No se creo la llave");
			e.printStackTrace();
		}
	}
	
	
}
