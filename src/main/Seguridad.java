package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Seguridad {

	public static final String DES = "DES";
	public static final String AES = "AES";
	public static final String BLOWFISH = "Blowfish";
	public static final String RSA = "RSA";
	public static final String RC4 = "RC4";

	public static KeyPair llaves() {
		try {
			KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance("RSA");
			return keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static byte[] encriptarSimetrico(byte[] msg, Key key, String algo) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		if(algo.equals("RSA") || algo.equals("DES")){
			algo = algo + "/ECB/PKCS5Padding";
		}
		Cipher cipher = Cipher.getInstance(algo);
		cipher.init(1, key);
		return cipher.doFinal(msg);
	}

	public static byte[] desencriptarSimetrico(byte[] msg, Key key, String algo) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		algo = algo + "/ECB/PKCS5Padding";
		Cipher cipher = Cipher.getInstance(algo);
		cipher.init(2, key);
		return cipher.doFinal(msg);
	}

	public static byte[] encriptarAsimetrico(byte[] msg, Key key, String algo) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algo);
		cipher.init(1, key);
		return cipher.doFinal(msg);
	}

	public static byte[] desencriptarAsimetrico(byte[] msg, Key key, String algo) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algo);
		cipher.init(2, key);
		return cipher.doFinal(msg);
	}

	public static byte[] mac(byte[] msg, Key key, String algo) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Mac mac = Mac.getInstance(algo);
		mac.init(key);

		return  mac.doFinal(msg);
	}

	public static boolean verificacionI(byte[] msg, Key key, String algo, byte[] hash) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] nuevo = mac(msg, key, algo);
		if (nuevo.length != hash.length) {
			return false;
		}
		for (int i = 0; i < nuevo.length; i++) {
			if (nuevo[i] != hash[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Crea un certificado con una llave RSA y la fecha actual.
	 * 
	 * @return certificado generado.
	 */
	public static X509Certificate getCertificado(KeyPair keypair) {

		Security.addProvider(new BouncyCastleProvider());

		try {

			ContentSigner sigGen = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC")
					.build(keypair.getPrivate());

			byte[] encoded = keypair.getPublic().getEncoded();

			@SuppressWarnings("deprecation")
			SubjectPublicKeyInfo subPubKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(encoded));

			Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

			X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(new X500Name("CN=Test"), BigInteger.ONE,
					startDate, endDate, new X500Name("CN=Test"), subPubKeyInfo);

			X509CertificateHolder certHolder = v1CertGen.build(sigGen);

			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certHolder.getEncoded());
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);

			return cert;
		} catch (OperatorCreationException | CertificateException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Verifica la fecha del certificado
	 * 
	 * @param cert el cerfificado a verificar
	 * @return si es o no valido
	 */
	public static boolean verificarCertificado(X509Certificate cert) {

		Date after = cert.getNotAfter();
		Date before = cert.getNotBefore();
		Date actual = new Date(System.currentTimeMillis());

		return actual.before(after) && actual.after(before);
	}

}
