package be.alexandre01.dreamnetwork.core.commands.lists.sub.service;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.accessibility.create.CreateTemplateConsole;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import be.alexandre01.dreamnetwork.core.utils.clients.NumberArgumentCheck;
import be.alexandre01.dreamnetwork.core.utils.clients.RamArgumentsChecker;
import be.alexandre01.dreamnetwork.core.utils.clients.ModsArgumentChecker;
import lombok.NonNull;
import org.jline.builtins.Completers;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Create extends SubCommand {

    public Create() {
        NodeBuilder nodeBuilder = new NodeBuilder(
                create("service",
                        create("create",
                                create(new BundlesNode(false),
                                        create(Completers.AnyCompleter.INSTANCE,
                                                create("STATIC","DYNAMIC",
                                                        create("1G","2G",
                                                                create("1G","2G"))))))));
    }
    String[] illegalChars = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|","-"};
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        if(!when(sArgs -> {
            String bundle = sArgs[1];
            String name = sArgs[2];
            String mod = sArgs[3];
            String xms = sArgs[4];
            String xmx = sArgs[5];

            //illegal chars
            for(String illegalChar : illegalChars){
                if(name.contains(illegalChar)){
                    System.out.println(LanguageManager.getMessage("commands.service.create.invalidServerName").replaceFirst("%var%", illegalChar));
                    return false;
                }
            }

            if(!RamArgumentsChecker.check(xms)){
                System.out.println(LanguageManager.getMessage("commands.service.create.invalidXMS").replaceFirst("%var%", xms));
                return false;
            }
            if(!RamArgumentsChecker.check(xmx)){
                System.out.println(LanguageManager.getMessage("commands.service.create.invalidXMX").replaceFirst("%var%", xmx));
                return false;
            }

            if(!ModsArgumentChecker.check(mod)){
                System.out.println(LanguageManager.getMessage("commands.service.create.invalidMod"));
                return false;
            }
            IJVMExecutor.Mods mods = IJVMExecutor.Mods.valueOf(mod);
            BundleInfo bundleInfo;

           if(!Core.getInstance().getBundleManager().getBundleDatas().containsKey(bundle.toLowerCase())) {
               System.out.println(LanguageManager.getMessage("commands.service.create.nonExistentBundle"));
               return false;
           }
           BundleData bundleData = Core.getInstance().getBundleManager().getBundleDatas().get(bundle.toLowerCase());
            bundleInfo = bundleData.getBundleInfo();



            System.out.println("Bundle = " + bundle);


            int port = 0;
            if(args.length == 5){
                if(!NumberArgumentCheck.check(sArgs[4])) {
                    System.out.println(LanguageManager.getMessage("commands.service.create.invalidPort"));
                    return false;
                }
                 port = Integer.parseInt(sArgs[4]);
            }

            System.out.println(LanguageManager.getMessage("service.creation.addingServerOnBundle").replaceFirst("%var%", name).replaceFirst("%var%", bundle));

            IContainer.JVMType jvmType = bundleInfo.getType();

            boolean proxy = bundleInfo.getType() == IContainer.JVMType.PROXY;

            JVMExecutor jvmExecutor = (JVMExecutor) Core.getInstance().getJvmContainer().getJVMExecutor(name, bundleData);
            if (jvmExecutor == null) {
                System.out.println(LanguageManager.getMessage("service.creation.creatingServerOnBundle").replaceFirst("%var%", name).replaceFirst("%var%", bundle));
                Config.createDir("bundles/"+bundle+"/"+name);
                System.out.println("?");
                jvmExecutor = new JVMExecutor(bundle, name, mods, xms, xmx, port, proxy, true,bundleData);
                jvmExecutor.addConfigsFiles();
                Console.print(LanguageManager.getMessage("service.creation.serverConfigured"));
                CustomType.reloadAll(BundlePathsNode.class, BundlesNode.class);
                return true;
            }
            jvmExecutor.addConfigsFiles();
            jvmExecutor.updateConfigFile(args[1], args[2], mods, args[4], args[5], Integer.parseInt(args[6]), proxy, null, null, null);
            Console.print(LanguageManager.getMessage("service.creation.serverConfigured"));
            CustomType.reloadAll(BundlePathsNode.class, BundlesNode.class);

            return true;
        },args,"create","bundle","name","type","xms","xmx","[port]","[javaversion]")){
            System.out.println(LanguageManager.getMessage("commands.cantBeExecuted"));
            fail("service","create","bundle","name","type","xms","xmx","[port]","[javaversion]");

            Core.getInstance().getCreateTemplateConsole().show("", "", "", "", "", "auto", new CreateTemplateConsole.Future() {
                @Override
                public void onResponse() {

                }

                @Override
                public void finish() {
                    CustomType.reloadAll(BundlePathsNode.class, BundlesNode.class);
                    Console.setActualConsole("m:default");
                }
            });
            return true;
        };

     //   fail("service","create","bundle","name","type","xms","xmx","[port]","[javaversion]");


        return true;
    }
}
