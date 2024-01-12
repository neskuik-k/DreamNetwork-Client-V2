package be.alexandre01.dreamnetwork.core.websocket.ssl.read;

import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 18:32
*/
public class LetsEncryptSecure extends AutoReadSSL{
    @Override
    public SslContext read() throws RuntimeException {
        AtomicReference<URL> url = new AtomicReference<>();
        YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(settings -> {
            try {
                url.set(new URL(settings.getLetsEncryptLocation()));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        Session session = new Session("acme://letsencrypt.org/staging");
        File file = new File("data/certs/lets-encrypt-account.pem");
        try {
            KeyPair accountKeyPair = KeyPairUtils.readKeyPair(new FileReader(file));
            assert url != null;
            Login login = session.login(url.get(),accountKeyPair);
            Account account = login.getAccount();

            File certFile = new File("data/certs/lets-encrypt.crt");
            CertificateFactory certificateFactory = new CertificateFactory();
            try {

               /* FileReader fileReader = new FileReader(certFile);
                PemReader pemReader = new PemReader(fileReader);
                Object obj = pemReader.readPemObject();
                pemReader.close(); // sloppy IO handling, be thorough in production code
                X509CertificateObject certObj = (X509CertificateObject) obj;*/
                X509Certificate certificate = (X509Certificate) certificateFactory.engineGenerateCertificate(Files.newInputStream(certFile.toPath()));
                if (certificate.getNotAfter().toInstant().isBefore(Instant.now())) {
                    throw new RuntimeException("Certificate of letsencrypt is expired you need to renew it !");
                }
                File keyFile = new File("data/certs/privateCert.pem");
                KeyPair kp = KeyPairUtils.readKeyPair(new FileReader(keyFile));

                return SslContextBuilder.forServer(kp.getPrivate(),certificate).build();
            } catch (CertificateException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
