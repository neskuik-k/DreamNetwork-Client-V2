package be.alexandre01.dreamnetwork.core.gui.ssl;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.core.websocket.ssl.setup.AutoConfigureSSL;

import java.util.HashSet;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 15:07
*/
public class CloudFlareMenu extends CoreAccessibilityMenu {
    final static HashSet<String> ports = new HashSet<>();

    static {
        ports.add("80");
        ports.add("443");
        ports.add("2052");
        ports.add("2082");
        ports.add("2083");
        ports.add("2086");
        ports.add("2087");
        ports.add("2095");
        ports.add("2096");
    }

    public CloudFlareMenu(){
        super("CloudFlare");

        addValueInput(PromptText.create("port"), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter("To use CloudFlare proxy you need to use a port between theses ports: (80, 443, 2052, 2082, 2083, 2086, 2087, 2095, 2096)");
                infos.writing("Enter the port you want to use:");
                setArgumentsBuilder(NodeBuilder.create("80", "443", "2052", "2082", "2083", "2086", "2087", "2095", "2096"));
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(ports.contains(args[0])){
                    return Operation.accepted(args[0]);
                }
                return retry();
            }
        });

        addValueInput(PromptText.create("domain"), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter("Enter the domain you want to use: (example: mydomain.org)");
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                String port = (String) getOperation("port").getReturnValue();
                if(!args[0].contains(".")){
                    infos.error("The domain must  contain a dot");
                    return errorAndRetry(infos);
                }
                AutoConfigureSSL.cloudflare(Integer.parseInt(port), args[0], false);
                return finish();
            }
        });
    }
}
