package be.alexandre01.dreamnetwork.core.websocket.ssl.setup;

import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.core.websocket.ssl.read.LocalHostSSL;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 16:05
*/
public abstract class AutoConfigureSSL {

    public abstract void configure(Object... o);
    public static void cloudflare(int port, String domain, boolean isAutoSigned){
        new CloudFlareSSLConfigure().configure(new Object[]{port, domain, isAutoSigned});
    }

    public static void letsEncrypt(int port,String domain, boolean isStaging,boolean approved){
        if(!approved){
            System.out.println("You need to approve the terms of service of lets encrypt");
            return;
        }
        new LetsEncryptSSLConfigure().configure(new Object[]{isStaging, new String[]{domain}});
    }
    public static void letsEncrypt(int port,String[] domains, boolean isStaging, boolean approved){
        if(!approved){
            System.out.println("You need to approve the terms of service of lets encrypt");
            return;
        }
        new LetsEncryptSSLConfigure().configure(new Object[]{isStaging, domains});
    }

    /*public static void customSSL(String key, String cert){

    }*/

    public static void localhost(){
        YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(settings -> {
            settings.clear();
            settings.setMethod(WSSettings.Method.LOCALHOST);
            WSSettings.getYml().saveFile();
        });
    }

    public static void selfSigned(){
        YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(settings -> {
            settings.clear();
            settings.setMethod(WSSettings.Method.AUTO_SELF_SIGNED);
            WSSettings.getYml().saveFile();
        });
    }

    public static void tunnel(){
        YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(settings -> {
            settings.clear();
            settings.setTunnelEnabled(true);
            WSSettings.getYml().saveFile();
        });
    }

    public static void saveCert(KeyPair cert) throws IOException {
        JcaPEMWriter jw = new JcaPEMWriter(new FileWriter("data/certs/certfile.crt"));
        try {
            jw.writeObject(cert);
        } catch (Throwable e) {
            try {
                jw.close();
            } catch (Throwable t) {
                e.addSuppressed(t);
            }

            throw e;
        }

        jw.close();
    }


    public static KeyPair readKey() throws IOException {
        try {
            PEMParser parser = new PEMParser(new FileReader("data/certs/privateKey.pem"));

            KeyPair kp;
            try {
                PEMKeyPair keyPair = (PEMKeyPair)parser.readObject();
                kp = (new JcaPEMKeyConverter()).getKeyPair(keyPair);
            } catch (Throwable throwable) {
                try {
                    parser.close();
                } catch (Throwable v) {
                    throwable.addSuppressed(v);
                }

                throw throwable;
            }

            parser.close();
            return kp;
        } catch (PEMException var6) {
            throw new IOException("Could not read PEM file: " + var6.getMessage(), var6);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
