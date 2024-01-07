package be.alexandre01.dreamnetwork.core.websocket;

import be.alexandre01.dreamnetwork.core.connection.core.ByteCounting;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCountingInboundHandler;
import be.alexandre01.dreamnetwork.core.connection.core.ByteCountingOutboundHandler;
import be.alexandre01.dreamnetwork.utils.Tuple;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 20/11/2023 at 10:34
*/
public class HTTPInitializer extends ChannelInitializer<SocketChannel> {
    WebSocketServerInitializer initializer;
    String prefix = "wss://";
    SslContext sslContext = null;
    public HTTPInitializer(WebSocketServerInitializer initializer)   {
        this.initializer = initializer;
        File certChainFile = new File("data/certs/certfile.crt");
        File keyFile = new File("data/certs/private-key.pem");

       
        if(!certChainFile.exists() || !keyFile.exists()){
            //initializer.setPrefix("ws://");
            SelfSignedCertificate ssc = null;
            try {
                Tuple<X509Certificate, PrivateKey> selfSignedCertificate = selfSignedCertificate("CA CERT");
                System.out.println(selfSignedCertificate);
                System.out.println("Cert file not exist");
                try {
                    sslContext = SslContextBuilder.forServer(selfSignedCertificate.b(), selfSignedCertificate.a()).build();
                } catch (SSLException sslException) {
                    throw new RuntimeException(sslException);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }else {
            System.out.println("Cert file exist");
            try {
                sslContext  = SslContextBuilder.forServer(certChainFile, keyFile).build();
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
     //   SSLEngine sslEngine = SSLContext.getDefault().createSSLEngine();
        if(sslContext != null){
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }
        ByteCounting byteCounting = new ByteCounting();
        ByteCountingInboundHandler byteCountingInboundHandler = new ByteCountingInboundHandler(byteCounting);
        ByteCountingOutboundHandler byteCountingOutboundHandler = new ByteCountingOutboundHandler(byteCounting);
        pipeline.addLast("byteCountingIn", byteCountingInboundHandler);
        pipeline.addLast("byteCountingOut", byteCountingOutboundHandler);
        pipeline.addLast("httpServerCodec", new HttpServerCodec());

       // dreamRestAPI.checkup("eyJzZWNyZXQiOiJpdElLeHNlTGlDcm1scnB1bzZMWWV4R2c5dktCZUk0TDdOaGdoSmcxR0lSTndMamk2MGFnY0VqODR1Z1dBa29LQVVNa2ZVUVI5R1RpeURJZzVpMmhJeVdkMDBZOWFyT09nUWNXT3BFMFNBRlVMakJxMTR6dENybVBoa3hDUDV4N1U2aExQWUd6NkVQd3NVa0xJbUhvTVR2VjVSQXZMSVpyaHdndWdCWGFDdGxqdlN1NXFEcmtsc3AwdWNPb3VrMWc2bXd6N1RoOEx4NW80MWdDb3EydzdhRmtzcXBSSEtwYmNhZlVmQTB4bmdBd3NPQ1ZQREtVdzlacnJ1T0w5MWlmIiwidXVpZCI6ImY5YjRiMDA4LTJhOGQtNDJmNi05MDA5LThjOTgxZTcxMzIwZiJ9", String.valueOf(initializer.getPort()));
        pipeline.addLast("httpHandler", new HttpServerHandler(initializer.getDreamRestAPI(),initializer));
    }
    private Tuple<X509Certificate, PrivateKey> selfSignedCertificate(String fqdn) {
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
            final X500Name owner = new X500Name("CN=" + fqdn);
            //final X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(owner.asX500Principal(), new BigInteger(64, random), new Date(System.currentTimeMillis() - 86400000L * 365), new Date(253402300799000L), owner.asX500Principal(), keypair.getPublic());



            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);
            byte[] pk = keypair.getPublic().getEncoded();
            SubjectPublicKeyInfo bcPk = SubjectPublicKeyInfo.getInstance(pk);

            JcaX509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    new X500Name("CN=CA Cert"),
                    BigInteger.ONE,
                    new Date(),
                    cal.getTime(),
                    new X500Name("CN=CA Cert"),
                    bcPk
            );
            //System.out.println("CERT GEN : "+certGen);
            //BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity
            //System.out.println("BASIC CONSTRAINTS : "+basicConstraints);

           // certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.
            X509CertificateHolder certHolder = certGen.build(new JcaContentSignerBuilder("SHA1withRSA").build(keypair.getPrivate()));
            System.out.println("CERT HOLDER : "+certHolder);
            //final X509CertificateHolder certHolder = builder.build(signer);

            System.out.println("ADD EXTENSION");
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
