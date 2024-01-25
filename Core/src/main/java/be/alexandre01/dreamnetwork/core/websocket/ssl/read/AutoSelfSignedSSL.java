package be.alexandre01.dreamnetwork.core.websocket.ssl.read;

import be.alexandre01.dreamnetwork.utils.Tuple;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/01/2024 at 16:23
*/
public class AutoSelfSignedSSL extends AutoReadSSL {
    @Override
    public SslContext read() throws RuntimeException {
        try {
            // try with netty
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (Exception e) {
            // try with bouncy castle
            Tuple<X509Certificate, PrivateKey> selfSignedCertificate = selfSignedCertificate();
            try {
                return SslContextBuilder.forServer(selfSignedCertificate.b(), selfSignedCertificate.a()).build();
            } catch (SSLException sslException) {
                throw new RuntimeException(sslException);
            }
        }
    }

    private Tuple<X509Certificate, PrivateKey> selfSignedCertificate() {
        Security.removeProvider("BC");
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        final SecureRandom random = new SecureRandom();
        final KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        System.out.println("->"+keyGen);
        //keyGen.initialize(2048, random);
        keyGen.initialize(4096);
        final KeyPair keypair = keyGen.generateKeyPair();
        final PrivateKey key = keypair.getPrivate();
        final X509Certificate cert;
        try {
            final X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(  new X500Name("CN=CA DreamCert"), new BigInteger(64, random), new Date(System.currentTimeMillis() - 86400000L * 365), new Date(253402300799000L),   new X500Name("CN=CA DreamCert"), keypair.getPublic());



            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);


            //System.out.println("CERT GEN : "+certGen);
            //BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity
            //System.out.println("BASIC CONSTRAINTS : "+basicConstraints);

            // certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.
            X509CertificateHolder certHolder = builder.build(new JcaContentSignerBuilder("SHA1withRSA").build(keypair.getPrivate()));
            System.out.println("CERT HOLDER : "+certHolder);
            //final X509CertificateHolder certHolder = builder.build(signer);
            cert = new JcaX509CertificateConverter().setProvider(provider).getCertificate(certHolder);
            System.out.println("->"+cert);

            cert.verify(keypair.getPublic());
            System.out.println("Verify cert");
        } catch (Exception t) {
            System.out.println("Wut ? -> "+t.getMessage());
            System.out.println("Wut ? -> "+t.getCause());
            System.out.println("Wut ? -> "+t.getStackTrace());
            return null;
        }
        System.out.println("->"+cert);
        System.out.println("->"+key);
        return new Tuple<>(cert, key);
    }

}
