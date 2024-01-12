package be.alexandre01.dreamnetwork.core.gui.ssl;

import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 11/01/2024 at 16:59
*/
public class DomainMenu extends CoreAccessibilityMenu {
    public DomainMenu() {
        super("Domain");

        addValueInput(PromptText.create("ssl"), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter("Choose the ssl you want to use: (1: CloudFlare + Proxy (The more secure), 2: LetsEncrypt (Auto generated))");
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(value.getValue().equalsIgnoreCase("1")){
                    return switchTo(new CloudFlareMenu());
                }
                if(value.getValue().equalsIgnoreCase("2")){
                   return switchTo(new LetsEncryptMenu());
                }
                return retry();
            }
        });
    }
}
