package be.alexandre01.dreamnetwork.core.websocket.ssl.read;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.ssl.SSLException;
import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/01/2024 at 16:21
*/
public class LocalHostSSL extends AutoReadSSL {
    @Override
    public SslContext read() throws RuntimeException {
        InputStream crt = getClass().getClassLoader().getResourceAsStream("files/ssl/localhost.direct.crt");
        InputStream key = getClass().getClassLoader().getResourceAsStream("files/ssl/localhost.direct.key");
        System.out.println("->"+crt);
        System.out.println("->"+key);


       // System.out.println("->"+new InputStreamReader(key));
        PublicKey publicKey = null;
        X509Certificate x509Certificate = null;
        PEMParser publicParser = new PEMParser(new InputStreamReader(crt));
        try {
            Object object = publicParser.readObject();
            System.out.println("Hmm " + object);
            System.out.println("Ok " + object.getClass().getSimpleName());
            if(object instanceof X509CertificateHolder) {
                X509CertificateHolder x509CertificateHolder = (X509CertificateHolder) object;

                // to private key
                 publicKey = new JcaPEMKeyConverter().getPublicKey(x509CertificateHolder.getSubjectPublicKeyInfo());
                try {
                   x509Certificate = new JcaX509CertificateConverter().getCertificate(x509CertificateHolder);
                } catch (CertificateException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrivateKey privateKey = null;
        PEMParser privateParser = new PEMParser(new InputStreamReader(key));
        try {
            Object object = privateParser.readObject();
            System.out.println(object);
            System.out.println(object.getClass().getSimpleName());
            if(object instanceof PrivateKeyInfo) {
                PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) object;
                // to private key
                 privateKey = new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("->"+publicKey);
        System.out.println("->"+privateKey);
        try {
            return SslContextBuilder.forServer(privateKey, x509Certificate).build();
        } catch (SSLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static RSAPublicKey readX509PublicKey(InputStream in) throws Exception {

        String key;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            key = reader.lines().reduce("", String::concat);
        }

        System.out.println(key);

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        System.out.println(new String(encoded));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        // generate X509 public key

        System.out.println("generate X509 public key");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public RSAPrivateKey readPKCS8PrivateKey(InputStream in) throws Exception {
        String key;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            key = reader.lines().reduce("", String::concat);
        }

        System.out.println(key);

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        System.out.println(new String(encoded));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        System.out.println("generate X509 private key");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
