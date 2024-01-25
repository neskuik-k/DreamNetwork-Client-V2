package be.alexandre01.dreamnetwork.core.websocket.ssl.setup;

import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 16:13
*/
public class CloudFlareSSLConfigure extends AutoConfigureSSL {


    @Override
    public void configure(Object[] o) {
        YamlFileUtils.getStaticFile(WSSettings.class).ifPresent(settings -> {
            settings.setPort((int) o[0]);
            settings.setForceURL(o[1].toString());
            WSSettings.getYml().saveFile();
            if(o.length > 2){
                boolean isAutoSigned = (boolean) o[1];
                settings.setSigned(isAutoSigned);
                settings.setMethod(WSSettings.Method.CLOUDFLARE);
            }else {
                settings.setSigned(true);
                settings.setMethod(WSSettings.Method.CLOUDFLARE);
            }
            WSSettings.getYml().saveFile();
        });

    }
}
