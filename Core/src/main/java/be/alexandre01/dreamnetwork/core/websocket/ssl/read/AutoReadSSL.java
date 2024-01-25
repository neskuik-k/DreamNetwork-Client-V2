package be.alexandre01.dreamnetwork.core.websocket.ssl.read;

import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import io.netty.handler.ssl.SslContext;

import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 18:31
*/
public abstract class AutoReadSSL {
    public abstract SslContext read() throws RuntimeException;


    public static Optional<AutoReadSSL> getCurrentSSL(){
        Optional<WSSettings> settings = YamlFileUtils.getStaticFile(WSSettings.class);
        if(settings.isPresent()){
            switch (settings.get().getMethod()){
                case LETSENCRYPT:
                    return Optional.of(new LetsEncryptSecure());
                case CUSTOM:
                    return Optional.of(new CustomSSL());
                case LOCALHOST:
                    return Optional.of(new LocalHostSSL());
            }
            if (settings.get().isSigned()){
                return Optional.of(new AutoSelfSignedSSL());
            }
            return Optional.empty();
        }
        throw new RuntimeException("No settings found");
    }

}
