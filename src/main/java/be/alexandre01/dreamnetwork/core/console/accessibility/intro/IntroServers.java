package be.alexandre01.dreamnetwork.core.console.accessibility.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.console.accessibility.create.CreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.utils.ASCIIART;

public class IntroServers extends IntroductionConsole {

    public IntroServers() {
        super("servers");

        NodeBuilder help = new NodeBuilder(NodeBuilder.create("yes"),console);
    }

    boolean hasCreatedProxy = false;

    @Override
    public void run() {
        console.setConsoleAction(new Console.IConsole() {
            boolean isHelp = false;
            @Override
            public void listener(String[] args) {
               clear();


                if(args[0].equalsIgnoreCase("yes")){
                    if(!hasCreatedProxy){
                        console.setWriting("");
                        Core.getInstance().getCreateTemplateConsole().show("proxies", "proxy", "STATIC", "512M", "1024M", "auto", new CreateTemplateConsole.Future() {
                            @Override
                            public void onResponse() {
                                hasCreatedProxy = true;
                                Console.setActualConsole("m:introservers",true);
                            }

                            @Override
                            public void finish() {
                                clear();
                                Console.debugPrint(Console.getFromLang("introduction.servers.oneLastThing"));
                                console.setWriting(Console.getFromLang("introduction.servers.helpToCreateServer"));
                                ConsoleReader.sReader.runMacro("yes");

                                //Console.reload();
                                ConsoleReader.sReader.getTerminal().flush();
                                ConsoleReader.sReader.getTerminal().writer().flush();
                                 Main.getBundleManager().getBundleDatas().forEach((s, bundleData) -> {
                                     Console.fine(Console.getFromLang("introduction.servers.finish.bundleName", bundleData.getBundleInfo().getName()));
                                     Console.fine(Console.getFromLang("introduction.servers.finish.bundleType", bundleData.getBundleInfo().getType()));
                                     Console.fine(Console.getFromLang("introduction.servers.finish.bundleExecutor", bundleData.getExecutors()));
                                });
                                CustomType.reloadAll(BundlePathsNode.class, BundlesNode.class);

                            }
                        });
                    }else {
                        Core.getInstance().getCreateTemplateConsole().show("main", "lobby", "STATIC", "1G", "2G", "auto", new CreateTemplateConsole.Future() {
                            @Override
                            public void onResponse() {

                            }

                            @Override
                            public void finish() {
                                Console.clearConsole();
                                ASCIIART.sendLogo();
                                ASCIIART.sendTitle();
                                CustomType.reloadAll(BundlePathsNode.class, BundlesNode.class);

                                Console.setActualConsole("m:default",true,false);
                            }
                        });
                    }
                }

                if(args[0].equalsIgnoreCase("no")){
                    Console.setActualConsole("m:default");
                }
            }

            @Override
            public void consoleChange() {
                ConsoleReader.sReader.getTerminal().flush();

                ConsoleReader.sReader.getTerminal().writer().flush();
                if(!hasCreatedProxy){
                    console.setWriting(Console.getFromLang("introduction.servers.helpToCreateProxy"));
                    ConsoleReader.sReader.runMacro("yes");
                }else {
                   console.setWriting("");
                }
                clear();
            }
        });
    }
}
