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
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import sun.security.x509.X500Name;

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
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        final SecureRandom random = new SecureRandom();
        final KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        keyGen.initialize(2048, random);
        final KeyPair keypair = keyGen.generateKeyPair();
        final PrivateKey key = keypair.getPrivate();
        final X509Certificate cert;
        try {
            final X500Name owner = new X500Name("CN=" + fqdn);
            final X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(owner.asX500Principal(), new BigInteger(64, random), new Date(System.currentTimeMillis() - 86400000L * 365), new Date(253402300799000L), owner.asX500Principal(), keypair.getPublic());
            final ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(key);
            final X509CertificateHolder certHolder = builder.build(signer);
            cert = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certHolder);
            cert.verify(keypair.getPublic());
        } catch (Throwable t) {
            return null;
        }
        System.out.println("->"+cert);
        System.out.println("->"+key);
        return new Tuple<>(cert, key);
    }

    public void test(){
        KeyPair accountKeyPair = KeyPairUtils.createKeyPair(2048);
        Session session = new Session("acme://letsencrypt.org/staging");
        try {
            Account account = new AccountBuilder()
                    .addContact("mailto:acme@example.com")
                    .agreeToTermsOfService()
                    .useKeyPair(accountKeyPair)
                    .create(session);
            URL accountLocationUrl = account.getLocation();
        } catch (AcmeException e) {
            throw new RuntimeException(e);
        }

    }
}
