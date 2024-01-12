package be.alexandre01.dreamnetwork.core.gui.ssl;

import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.config.WSSettings;
import be.alexandre01.dreamnetwork.api.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.core.websocket.ssl.setup.AutoConfigureSSL;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/01/2024 at 16:59
*/
public class NoDomainMenu extends CoreAccessibilityMenu {
    public NoDomainMenu(){
        super("NoDomain");
        addValueInput(PromptText.create("warning"), new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter("If you have later a domain name, don't forget to change the ssl settings, the method you choose is not the most recommended for production.");
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                return skip();
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                return finish();
            }
        }));

        if(Config.isWindows()){
            addValueInput(PromptText.create("localhost"), new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
                @Override
                public void transition(ShowInfos infos) {
                    infos.onEnter("We detected that you are on windows, do you use it uniquely on localhost for development or test ? (y/n)");
                }

                @Override
                public Operation accept(String value, String[] args, ShowInfos infos) {
                    AutoConfigureSSL.localhost();
                    return finish();
                }

                @Override
                public Operation refuse(String value, String[] args, ShowInfos infos) {
                    return skip();
                }
            }));
        }
                addValueInput(PromptText.create("ssl"), new ValueInput() {
                    @Override
                    public void onTransition(ShowInfos infos) {
                        infos.onEnter("Choose the ssl you want to use: (1: Tunnel (Expiremental), 2: Auto signed + Add manually certificate in your browser), 3: Auto localhost (Only for localhost - Expiremental)");
                    }

                    @Override
                    public Operation received(PromptText value, String[] args, ShowInfos infos) {
                        if(value.getValue().equalsIgnoreCase("1")){
                            AutoConfigureSSL.tunnel();
                            return finish();
                        }
                        if(value.getValue().equalsIgnoreCase("2")) {
                            AutoConfigureSSL.selfSigned();
                            return finish();
                        }

                        if(value.getValue().equalsIgnoreCase("3")){
                            AutoConfigureSSL.localhost();
                            return finish();
                        }

                        return retry();
                    }
                });
    }
}
