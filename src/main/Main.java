package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main {

	public static void main(String[] args) {

		

	}
	/**
	 * 
	 * @return certificado con RSA
	 */
	public X509Certificate getCertificado() {
		
		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			KeyPair keypair = keyGen.generateKeyPair();

			ContentSigner sigGen = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC")
					.build(keypair.getPrivate());
			
			byte[] encoded = keypair.getPublic().getEncoded();
			
			@SuppressWarnings("deprecation")
			SubjectPublicKeyInfo subPubKeyInfo = new SubjectPublicKeyInfo(
				    ASN1Sequence.getInstance(encoded));

			Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

			X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(new X500Name("CN=Test"), BigInteger.ONE,
					startDate, endDate, new X500Name("CN=Test"), subPubKeyInfo);

			X509CertificateHolder certHolder = v1CertGen.build(sigGen);
			
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certHolder.getEncoded());
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
			
			return cert;
		} catch (NoSuchAlgorithmException | OperatorCreationException | CertificateException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
