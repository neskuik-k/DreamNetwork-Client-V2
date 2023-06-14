package be.alexandre01.dreamnetwork.core.gui.intro;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.core.gui.create.TestCreateTemplateConsole;

import static be.alexandre01.dreamnetwork.core.console.Console.getFromLang;

public class IntroMenu extends AccessibilityMenu {


    public IntroMenu(String name) {
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
        addValueInput(PromptText.create("emoji"),new AcceptOrRefuse(this, new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter(getFromLang("tutorial.emoji"));
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                Main.getGlobalSettings().setUseEmoji(true);
                Main.getGlobalSettings().save();
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
                Main.getCommandReader().getCommands().getCommandsManager().check(new String[]{"help"});
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
}
