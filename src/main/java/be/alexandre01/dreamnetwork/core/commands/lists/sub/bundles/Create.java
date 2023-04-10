package be.alexandre01.dreamnetwork.core.commands.lists.sub.bundles;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommand;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlePathsNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.BundlesNode;
import be.alexandre01.dreamnetwork.api.commands.sub.types.CustomType;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.service.bundle.BService;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo;
import be.alexandre01.dreamnetwork.core.utils.clients.TypeArgumentChecker;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder.create;

public class Create extends SubCommand {
    public Create(){
        NodeBuilder nodeBuilder = new NodeBuilder(
                create("bundle",
                    create("create",
                            create(new BundlePathsNode()))));
    }
    @Override
    public boolean onSubCommand(@NonNull String[] args) {
        boolean b = when(sArgs -> {

            String[] nArgs = sArgs;

            if(sArgs[1].endsWith("/")){
                if(args.length < 4){
                    System.out.println(LanguageManager.getMessage("commands.bundle.create.needSpecifyType"));
                    return true;
                }
                nArgs = new String[]{sArgs[0],sArgs[1]+sArgs[2],sArgs[3]};
            }



            if(!TypeArgumentChecker.check(nArgs[2])){
                System.out.println(LanguageManager.getMessage("commands.bundle.create.typeNotFound"));
                return true;
            }
            Config.createDir("bundles/"+nArgs[1]);
            File file = new File(Config.getPath("bundles/"+nArgs[1]+"/this-info.yml"));
            try {
                file.createNewFile();
               /* Yaml yaml = new Yaml(new Constructor(BundleFileInfo.class));
                yaml.dump(new BundleFileInfo(sArgs[1], IContainer.JVMType.valueOf(sArgs[2])),new PrintWriter(file));*/


               BundleInfo bundleInfo =  new BundleInfo(nArgs[1], IContainer.JVMType.valueOf(nArgs[2]));
               bundleInfo.getServices().add(new BService("test",1,2));
               BundleInfo.updateFile(file, bundleInfo);
               BundleData bundleData = new BundleData(nArgs[1],bundleInfo);
               Main.getBundleManager().addBundleData(bundleData);
               if(nArgs[1].endsWith("/")){
                   Main.getBundleManager().addPath(nArgs[1]);
               }else {
                   Main.getBundleManager().addPath(nArgs[1]+"/");
               }

               CustomType.reloadAll(BundlePathsNode.class);
               CustomType.reloadAll(BundlesNode.class);

               //System.out.println(yaml.dumpAsMap(new BundleFileInfo(sArgs[1], IContainer.JVMType.valueOf(sArgs[2]))));
               System.out.println(LanguageManager.getMessage("commands.bundle.create.writingInfo"));
            } catch (IOException e) {
                System.out.println(LanguageManager.getMessage("commands.bundle.create.errorWritingInfo"));
                System.out.println(e.getMessage());
                System.out.println(Arrays.toString(e.getStackTrace()));
                throw new RuntimeException(e);
            }

            if(!Config.isWindows()){
                  //Linux create symbolic link on root
                //Files.createSymbolicLink()
            }
            return true;
        },args,"create","[bundle]","[type]");
        return b;
    }

}
