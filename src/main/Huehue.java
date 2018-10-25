package srvcifIC201820;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Delegado3 extends Thread {
	public static final String OK = "OK";
	public static final String ALGORITMOS = "ALGORITMOS";
	public static final String HMACMD5 = "HMACMD5";
	public static final String HMACSHA1 = "HMACSHA1";
	public static final String HMACSHA256 = "HMACSHA256";
	public static final String CERTSRV = "CERTSRV";
	public static final String CERCLNT = "CERCLNT";
	public static final String SEPARADOR = ":";
	public static final String HOLA = "HOLA";
	public static final String INICIO = "INICIO";
	public static final String ERROR = "ERROR";
	public static final String REC = "recibio-";
	private Socket sc = null;
	private String dlg;
	private byte[] mybyte;

	Delegado3(Socket csP, int idP) {
		this.sc = csP;
		this.dlg = new String("delegado " + idP + ": ");
		try {
			this.mybyte = new byte['?'];
			this.mybyte = Coordinador.certSer.getEncoded();
		} catch (Exception e) {
			System.out.println("Error creando encoded del certificado para el thread" + this.dlg);
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println(this.dlg + "Empezando atencion.");
		try {
			PrintWriter ac = new PrintWriter(this.sc.getOutputStream(), true);
			BufferedReader dc = new BufferedReader(new InputStreamReader(this.sc.getInputStream()));

			String linea = dc.readLine();
			if (!linea.equals("HOLA")) {
				ac.println("ERROR");
				this.sc.close();
				throw new Exception(this.dlg + "ERROR" + "recibio-" + linea + "-terminando.");
			}
			ac.println("OK");
			System.out.println(this.dlg + "recibio-" + linea + "-OK, continuando.");

			linea = dc.readLine();
			if ((!linea.contains(":")) || (!linea.split(":")[0].equals("ALGORITMOS"))) {
				ac.println("ERROR");
				this.sc.close();
				throw new Exception(this.dlg + "ERROR" + "recibio-" + linea + "-terminando.");
			}
			String[] algoritmos = linea.split(":");
			if ((!algoritmos[1].equals("DES")) && (!algoritmos[1].equals("AES")) && (!algoritmos[1].equals("Blowfish"))
					&& (!algoritmos[1].equals("RC4"))) {
				ac.println("ERROR");
				this.sc.close();
				throw new Exception(this.dlg + "ERROR" + "Alg.Simetrico" + "recibio-" + algoritmos + "-terminando.");
			}
			if (!algoritmos[2].equals("RSA")) {
				ac.println("ERROR");
				this.sc.close();
				throw new Exception(this.dlg + "ERROR" + "Alg.Asimetrico." + "recibio-" + algoritmos + "-terminando.");
			}
			if ((!algoritmos[3].equals("HMACMD5")) && (!algoritmos[3].equals("HMACSHA1"))
					&& (!algoritmos[3].equals("HMACSHA256"))) {
				ac.println("ERROR");
				this.sc.close();
				throw new Exception(this.dlg + "ERROR" + "AlgHash." + "recibio-" + algoritmos + "-terminando.");
			}
			System.out.println(this.dlg + "recibio-" + linea + "-OK, continuando.");
			ac.println("OK");

			String strCertificadoCliente = dc.readLine();
			byte[] certificadoCbytes = new byte['?'];
			certificadoCbytes = toByteArray(strCertificadoCliente);
			CertificateFactory creador = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certificadoCbytes);
			X509Certificate certificadoCliente = (X509Certificate) creador.generateCertificate(in);
			System.out.println(this.dlg + "recibio certificado del cliente. -OK, continuando.");
			ac.println("OK");

			ac.println(toHexString(this.mybyte));
			System.out.println(this.dlg + "envio certificado del servidor. continuando.");
			linea = dc.readLine();
			if (!linea.equals("OK")) {
				ac.println("ERROR");
				throw new Exception(this.dlg + "ERROR" + "recibio-" + linea + "-terminando.");
			}
			System.out.println(this.dlg + "recibio-" + linea + "-OK, continuando.");

			SecretKey simetrica = Seg.kgg(algoritmos[1]);
			byte[] ciphertext1 = Seg.ae(simetrica.getEncoded(), certificadoCliente.getPublicKey(), algoritmos[2]);
			ac.println(toHexString(ciphertext1));
			System.out.println(this.dlg + "envio llave simetrica al cliente. -OK, continuando.");

			linea = dc.readLine();
			byte[] llaveS = Seg.ad(toByteArray(linea), Coordinador.keyPairServidor.getPrivate(), algoritmos[2]);
			if (!toHexString(llaveS).equals(toHexString(simetrica.getEncoded()))) {
				ac.println("ERROR");
				throw new Exception(this.dlg + "ERROR" + "Problema confirmando llave. terminando.");
			}
			ac.println("OK");

			linea = dc.readLine();
			String datos = new String(Seg.sd(toByteArray(linea), simetrica, algoritmos[1]));
			linea = dc.readLine();
			byte[] hmac = toByteArray(linea);
			boolean verificacion = Seg.vi(datos.getBytes(), simetrica, algoritmos[3], hmac);
			if (verificacion) {
				System.out.println(this.dlg + "verificacion de integridad. -OK, continuando.");
				boolean rta = esta(datos);
				if (rta) {
					ac.println("OK:DEBE");
				} else {
					ac.println("OK:PAZYSALVO");
				}
			} else {
				ac.println("ERROR");
				throw new Exception(this.dlg + "Error en verificacion de integridad. -terminando.");
			}
			this.sc.close();
			System.out.println(this.dlg + "Termino exitosamente.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean esta(String inDato) {
		int num = Integer.parseInt(inDato);
		Random rand = new Random();
		int value = rand.nextInt(10);
		while (value == 0) {
			value = rand.nextInt();
		}
		return (num - value) % 2 == 1;
	}

	public String toHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public byte[] toByteArray(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}
}
