package servidorConSeguridad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coordinador {

	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	static java.security.cert.X509Certificate certSer; /* acceso default */
	static KeyPair keyPairServidor; /* acceso default */

	/**
	 * @param args

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear certificados.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
		keyPairServidor = Seg.grsa();
		certSer = Seg.gc(keyPairServidor);

		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");		
		while (true) {
			try { 
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				Delegado3 d3 = new Delegado3(sc,idThread);
				idThread++;
				d3.start();
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
	}
	 * @throws NoSuchAlgorithmException 
	 * @throws CertificateException 
	 * @throws IllegalStateException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidKeyException 
	 */

	public static void main(String[] args) throws NumberFormatException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, IllegalStateException, CertificateException {

		// TODO Auto-generated method stub

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);


		//Crea el pool

		int numT = 2;

		ExecutorService exec = Executors.newFixedThreadPool(numT);

		System.out.println(MAESTRO + "Creado pool de tamanio "+ numT);

		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear certificados.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
		keyPairServidor = Seg.grsa();
		certSer = Seg.gc(keyPairServidor);

		int idThread = 0;

		// Crea el socket que escucha en el puerto seleccionado.

		ss = new ServerSocket(ip);

		System.out.println(MAESTRO + "Socket creado.");

		while (true) {

			try { 

				Socket sc = ss.accept();

				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");

				//Delegado3 d3 = new Delegado3(sc,idThread);

				exec.execute(new Delegado3(sc,idThread));

				idThread++;

				//d3.start();

			} catch (IOException e) {

				System.out.println(MAESTRO + "Error creando el socket cliente.");

				e.printStackTrace();

			}

		}

	}




}
