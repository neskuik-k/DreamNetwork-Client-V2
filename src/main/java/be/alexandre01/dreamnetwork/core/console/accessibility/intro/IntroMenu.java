package be.alexandre01.dreamnetwork.core.console.accessibility.intro;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.accessibility.AcceptOrRefuse;
import be.alexandre01.dreamnetwork.core.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.core.console.accessibility.create.TestCreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;

public class IntroMenu extends AccessibilityMenu {


    public IntroMenu(String name) {
        super(name);


        addValueInput(PromptText.create("eula"),new AcceptOrRefuse(this,"accept", "refuse", new AcceptOrRefuse.AcceptOrRefuseListener() {

            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter("You can find the eula here : https://account.mojang.com/documents/minecraft_eula");
                infos.writing("Do you accept the terms of use of Mojang (Minecraft EULA) and DreamNetwork ? (accept/refuse) > ");
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                return skip();
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                infos.onEnter("You have refused the terms of use of Mojang (Minecraft EULA) and DreamNetwork");
                Config.removeDir("bundles");
                System.exit(0);
                return null;
            }
        }));

        addValueInput(PromptText.create("tutoEnter"),new AcceptOrRefuse("yes", "no", new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter("Do you want to see the tutorial ? (yes/no) > ");
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

        addValueInput(PromptText.create("emoji"),new AcceptOrRefuse("yes", "no", new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter("Do you see this emoji {(🥳)}... ?");
                infos.writing(Colors.WHITE_BOLD_BRIGHT+"Type yes or no > ");
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {
                Main.getGlobalSettings().setUseEmoji(true);
                Main.getGlobalSettings().save();
                Main.getLanguageManager().getEmojiManager().load();
                return skip();
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                return skip();
            }
        }));


        addValueInput(PromptText.create("help"), new ValueInput() {
            @Override
            public void onTransition(ShowInfos infos) {
                infos.onEnter("Let's the tutorial start... the most difficult command is... 'HELP'. Come on ! Let a try !\nPsst, I have a little tip, try your tab key to find the way !");
                infos.writing("Type the desired command: ");
            }

            @Override
            public Operation received(PromptText value, String[] args, ShowInfos infos) {
                if(args.length== 0){
                    infos.error("Not so fast ! I think you should have to type a command");
                    return errorAndRetry(infos);
                }

                if(!args[0].equalsIgnoreCase("help")){
                    infos.error("Hmm, read the text above, you have to write help.");
                    return errorAndRetry(infos);
                }
                Main.getCommandReader().getCommands().getCommandsManager().check(new String[]{"help"});
                Console.debugPrint(Colors.GREEN_BOLD+"TADAAA you've done it !");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return skip();
            }
        });

        addValueInput(PromptText.create("createProxy"),new AcceptOrRefuse("yes", "no", new AcceptOrRefuse.AcceptOrRefuseListener() {
            @Override
            public void transition(ShowInfos infos) {
                infos.onEnter("Do you want to create a proxy ? (yes/no) > ");
            }

            @Override
            public Operation accept(String value, String[] args, ShowInfos infos) {

                return switchTo(new TestCreateTemplateConsole("proxies","proxy","STATIC","256M","1024M","25565"));
            }

            @Override
            public Operation refuse(String value, String[] args, ShowInfos infos) {
                forceExit();
                return finish();
            }
        }));
    }
}
