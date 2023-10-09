package be.alexandre01.dreamnetwork.core.gui.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.config.GlobalSettings;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.core.console.accessibility.CoreAccessibilityMenu;
import be.alexandre01.dreamnetwork.core.gui.create.TestCreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;

import java.util.ArrayList;

import static be.alexandre01.dreamnetwork.api.console.Console.getFromLang;

public class IntroMenuCore extends CoreAccessibilityMenu {


    public IntroMenuCore(String name) {
        super(name);





        addValueInput(PromptText.create("eula"),new AcceptOrRefuse(this,"accept", "refuse", new AcceptOrRefuse.AcceptOrRefuseListener() {

            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.eula.info"));
                infos.writing(getFromLang("tutorial.eula.ask"));
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                return skip();
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.eula.refused"));
                Config.removeDir("bundles");
                System.exit(0);
                return null;
            }
        }));
        ArrayList<String> types = new ArrayList<>();
        for(ExecType s : ExecType.values()) {
            if(s.isProxy()){
                types.add(s.name());
            }
        }
        insertArgumentBuilder("proxyType", NodeBuilder.create(types.toArray()));
        addValueInput(PromptText.create("proxyType"), new ValueInput() {
                    @Override
                    public void onTransition(ShowInfos infos) {
                        infos.writing(getFromLang("tutorial.proxyType.ask"));
                    }

                    @Override
                    public Operation received(PromptText value, String[] args, ShowInfos infos) {
                        try {
                            ExecType execType = ExecType.valueOf(args[0].toUpperCase());
                            if(!execType.isProxy()){
                                infos.error(getFromLang("tutorial.proxyType.notGood"));
                                return errorAndRetry(infos);
                            }
                            BundleData bundleData = Main.getBundleManager().getBundleData("proxies");
                            System.out.println("bundleData = " + bundleData);
                            infos.error("bundleData = " + bundleData);
                            ((BundleInfo)bundleData.getBundleInfo()).execType = execType;
                            try {
                                ((BundleInfo) bundleData.getBundleInfo()).getYaml().saveFile();
                            }catch (Exception e){
                                Console.bug(e);
                            }

                            return skip();
                        }catch (Exception e) {
                            //infos.error(getFromLang("tutorial.proxyType.notGood"));
                            return errorAndRetry(infos);
                        }
                    }
        });
                addValueInput(PromptText.create("emoji"), new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
                    @Override
                    public void transition(ShowInfos infos) {
                        infos.onEnter(getFromLang("tutorial.emoji"));
                    }

                    @Override
                    public Operation accept(String value, String[] args, ShowInfos infos) {
                        Main.getGlobalSettings().setUseEmoji(true);
                        GlobalSettings.getYml().saveFile();
                        Main.getLanguageManager().getEmojiManager().load();
                        Main.getLanguageManager().forceLoad(Main.getLanguageManager().getActualLanguage().getLocalizedName());
                        return skip();
                    }

                    @Override
                    public Operation refuse(String value, String[] args, ShowInfos infos) {
                        return skip();
                    }
                }));
        addValueInput(PromptText.create("tutoEnter"),new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.asking"));
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                return skip();
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                forceExit();
                return finish();
            }
        }));




        addValueInput(PromptText.create("help").setSuggestions(NodeBuilder.create("help")), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.help"));
                infos.writing(getFromLang("tutorial.help.asking"));
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(args.length == 0){
                    infos.error("Not so fast ! I think you should have to type a command");
                    return errorAndRetry(infos);
                }

                if(!args[0].equalsIgnoreCase("help")){
                    infos.error(getFromLang("tutorial.help.notGood"));
                    return errorAndRetry(infos);
                }
                Main.getCommandReader().getCommands().check(new String[]{"help"});
                Console.debugPrint(getFromLang("tutorial.help.tada"));
                try {
                    Thread.sleep(2700);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return skip();
            }
        });

        addValueInput(PromptText.create("createProxy"),new AcceptOrRefuse(this,getFromLang("menu.yes"), Console.getFromLang("menu.no"), new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.service.ask.proxy"));
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                TestCreateTemplateConsole t = new TestCreateTemplateConsole("proxies","proxy","STATIC","256M","1024M","25565");
                t.addFinishCatch(() -> {
                        show(false);
                        injectOperation(Operation.set(Operation.OperationType.SKIP));
                });
                return switchTo(t);
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                forceExit();
                return finish();
            }
        }));
        addValueInput(PromptText.create("createLobby"),new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.service.ask.server"));
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                TestCreateTemplateConsole t = new TestCreateTemplateConsole("main","lobby","STATIC","256M","1024M","auto");
                t.addFinishCatch(() -> {
                    forceExit();
                    injectOperation(Operation.set(Operation.OperationType.FINISH));
                });
                return switchTo(t);

            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                forceExit();
                return finish();
            }
        }));
    }

    @Override
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
    }
}
