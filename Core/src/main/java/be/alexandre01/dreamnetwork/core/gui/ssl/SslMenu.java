package be.alexandre01.dreamnetwork.core.gui.ssl;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.config.GlobalSettings;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.core.gui.create.TestCreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;

import java.util.ArrayList;

import static be.alexandre01.dreamnetwork.api.console.Console.getFromLang;

public class SslMenu extends CoreAccessibilityMenu {


    public SslMenu(String name) {
        super(name);



        addValueInput(PromptText.create("domain"), new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter("Do you have a domain name ? (y/n)");
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                return switchTo(new DomainMenu());
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                return switchTo(new NoDomainMenu());
            }
            }


        ));

    }

   /* @Override
    public void buildAndRun() {
        super.buildAndRun();
        console.setKillListener(reader -> {
            //Shutdown other things
            console.addOverlay(new Console.Overlay() {
                @Override
                public void on(String data) {
                    disable();
                    if(data.equalsIgnoreCase("y") ||data.equalsIgnoreCase("yes")){
                        // quit
                        Config.removeDir("bundles");
                        System.exit(0);
                    }else {
                        Console.debugPrint(Console.getFromLang("menu.cancel"));
                    }
                }
            }, Console.getFromLang("menu.cancelWriting"));
            return true;
        });
    }*/
}
