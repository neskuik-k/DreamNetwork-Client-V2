package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import be.alexandre01.dreamnetwork.core.service.JVMExecutor;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import org.apache.commons.lang.ArrayUtils;
import org.jline.builtins.Completers;

public class BundlesNode extends CustomType {

    public BundlesNode(boolean recursive,boolean withSimplifiedName,boolean withShortcutName) {

        type = SubCommandCompletor.Type.SERVERS;
        BundleManager bundleManager = Core.getInstance().getBundleManager();
        if(recursive){
            setCustomType(() -> {
                //Completers.TreeCompleter
                Console.fine("BundlesNode INIT");
                Object[] bundles = new Object[0];
                for(String bundle : bundleManager.getBundleDatas().keySet()){
                    Console.fine("+Bundle : "+bundle);
                    //bundles = ArrayUtils.add(bundles,bundle);
                    for(String executor : bundleManager.getBundleData(bundle).getExecutors().keySet()){
                        Console.fine("+Executor : "+executor);
                        bundles = ArrayUtils.add(bundles,Colors.CYAN+bundle+Colors.YELLOW_BOLD+"/"+Colors.WHITE_BOLD+executor);
                    }
                }
                if(withSimplifiedName){
                    for(IJVMExecutor exec: Core.getInstance().getJvmContainer().getJVMExecutors()){
                        if(exec.getBundleData() != null){
                            //Check if the executor is the only one with this name
                            if(Core.getInstance().getJvmContainer().getJVMExecutorsFromName(exec.getName()).length == 1){
                                bundles = ArrayUtils.add(bundles, Colors.WHITE_BOLD_UNDERLINED+exec.getName());
                            }
                        }
                    }
                }

                if(bundles.length == 0){
                    return new Object[]{Completers.AnyCompleter.INSTANCE};
                }
                return bundles;
            });
        }else {
            setCustomType(() -> {
                //Completers.TreeCompleter

                String[] bundles = bundleManager.getBundleDatas().keySet().toArray(new String[0]);

                if(bundles.length == 0){
                    return new Object[]{Completers.AnyCompleter.INSTANCE};
                }
                return bundles;
            });
        }

    }
}
