package be.alexandre01.dreamnetwork.core.websocket.ssl.setup;

import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 16:13
*/
public class LetsEncryptSSLConfigure extends AutoConfigureSSL {

    public static void main(String[] args) {
      //  WSSettings.load();
        AutoConfigureSSL.letsEncrypt(443,"dreamnetwork.cloud",true,true);
    }
    @Override
    public void configure(Object[] o) throws RuntimeException{
        boolean isStaging = (boolean) o[0];
        String[] domains = (String[]) o[1];


        String url = "acme://letsencrypt.org/staging";
        if(!isStaging){
            url = "acme://letsencrypt.org";
        }
        Session session = new Session(url);
        KeyPair accountKeyPair;
        new File("data/certs").mkdirs();
        File accountFile = new File("data/certs/lets-encrypt-account.pem");
        boolean created = accountFile.exists();
        try {
            if(!accountFile.exists()){
                accountFile.createNewFile();
                accountKeyPair = KeyPairUtils.createKeyPair(2048);
                KeyPairUtils.writeKeyPair(accountKeyPair, new FileWriter("data/certs/lets-encrypt-account.pem"));
            }else {
                accountKeyPair = KeyPairUtils.readKeyPair(new FileReader("data/certs/lets-encrypt-account.pem"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Account account = null;
            if(created){
                account = new AccountBuilder()
                        .useKeyPair(accountKeyPair)
                        .onlyExisting()
                        .create(session);

            }else {
                account = new AccountBuilder()
                        .agreeToTermsOfService()
                        .useKeyPair(accountKeyPair)
                        .create(session);
            }

            Optional<WSSettings> optSettings  = YamlFileUtils.getStaticFile(WSSettings.class);
            if(!optSettings.isPresent()){
               throw new RuntimeException("WSSettings not found");
            }
            optSettings.get().setLetsEncryptLocation(account.getLocation().toString());
            optSettings.get().setForceURL(domains[0]);
            WSSettings.getYml().saveFile();


            Order order = account.
                    newOrder().
                    domains(domains).
                    //notAfter(Instant.now().plus(Duration.ofDays(30))).
                    create();



            System.out.println("Waiting for authorization...");
            new Thread(() -> {
                for (int i = 0; i < order.getAuthorizations().size(); i++) {
                    Authorization auth = order.getAuthorizations().get(i);

                    if(auth.getStatus() == Status.VALID){
                        System.out.println("Valid status");
                        /*try {
                            auth.deactivate();
                        } catch (AcmeException e) {
                            throw new RuntimeException(e);
                        }*/
                        continue;
                    }
                    if (auth.getStatus() == Status.PENDING) {
                        // Block until the server finished processing the authorization
                        blockingProcessAuth(auth);
                        System.out.println(i+1+"/"+order.getAuthorizations().size() + " authorization completed...");
                    }
                }
                System.out.println("All authorization completed...");

                KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
                try {
                    KeyPairUtils.writeKeyPair(domainKeyPair, new FileWriter("data/certs/privateCert.pem"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                CSRBuilder csrb = new CSRBuilder();

                csrb.addDomains(domains);
                try {
                    csrb.sign(domainKeyPair);
                    csrb.write(new FileWriter("data/certs/renew-letsEncrypt.csr"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                while (order.getStatus() != Status.READY) {
                    try {
                        System.out.println("Waiting for certificate... " + order.getStatus());
                        if(order.getStatus() == Status.INVALID){
                            System.out.println(order.getError().toString());
                           // throw new RuntimeException("Invalid domain");
                        }
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        order.update();
                    } catch (AcmeException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    order.execute(csrb.getEncoded());
                } catch (AcmeException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                while (order.getStatus() != Status.VALID) {
                    try {
                        System.out.println("Waiting for certificate... " + order.getStatus());
                        if(order.getStatus() == Status.INVALID){
                            System.out.println(order.getError().toString());
                            // throw new RuntimeException("Invalid domain");
                        }
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        order.update();
                    } catch (AcmeException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    File certFile = new File("data/certs/lets-encrypt.crt");
                    certFile.createNewFile();
                    System.out.println("Certificate generated");
                    System.out.println(order.getCertificate());
                    System.out.println(order.getCertificate().getCertificate());
                    System.out.println(order.getCertificate().getLocation());
                    System.out.println("Write certificate");
                    FileWriter fileWriter = new FileWriter(certFile);
                    order.getCertificate().writeCertificate(fileWriter);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while writing certificate");
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }

                System.out.println("Certificate generated");
                System.out.println("Please restart your server");
            }).start();

        } catch (AcmeException e) {
            throw new RuntimeException(e);
        }
    }

    private void blockingProcessAuth(Authorization auth){

        Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE); // by name

        System.out.println("Please create a file in your web server directory, and insert the following challenge:");
        System.out.println(challenge.getToken());
        System.out.println(challenge.getAuthorization());
        System.out.println("Press enter when done");
        CompletableFuture<String[]> future = Console.getFutureInput();
        while (!future.isDone()){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        future.whenComplete((strings, throwable) -> {

            try {
                challenge.trigger();
            } catch (AcmeException e) {
                throw new RuntimeException(e);
            }
            while (auth.getStatus() != Status.VALID) {
                if(auth.getStatus() == Status.INVALID){
                    System.out.println("Invalid domain");
                    System.out.println("Please check your dns records");
                    if(challenge.getError() != null)
                        System.out.println(challenge.getError().toString());
                    int i = 0;
                    auth.getChallenges().forEach(challenge1 -> {

                        if(challenge1.getStatus() == Status.INVALID){
                            System.out.println("Challenge "+i+" is invalid");

                            if(challenge1.getError() != null)
                                System.out.println(challenge1.getError().toString());
                        }else {
                            System.out.println("Challenge "+i+" is valid");
                        }
                    });
                    // throw new RuntimeException("Invalid domain");
                }
                if(auth.getStatus() == Status.EXPIRED){
                    System.out.println("Expired domain");
                    // throw new RuntimeException("Expired domain");
                }
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    auth.update();
                } catch (AcmeException e) {
                    throw new RuntimeException(e);
                }
        }

    });
    }
}
