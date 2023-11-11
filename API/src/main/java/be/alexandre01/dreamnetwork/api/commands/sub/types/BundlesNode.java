package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IExecutor;
import be.alexandre01.dreamnetwork.api.service.bundle.IBundleManager;
import org.apache.commons.lang3.ArrayUtils;
import org.jline.builtins.Completers;

import java.util.Map;

public class BundlesNode extends CustomType {

    public BundlesNode(boolean recursive,boolean withSimplifiedName,boolean withShortcutName) {

        type = SubCommandCompletor.Type.SERVERS;
        IBundleManager bundleManager = DNCoreAPI.getInstance().getBundleManager();
        if(recursive){
            setCustomType(() -> {
                //Completers.TreeCompleter
               // Console.fine("BundlesNode INIT");
                Object[] bundles = new Object[0];
                for(String bundle : bundleManager.getBundleDatas().keySet()){
                    //Console.fine("+Bundle : "+bundle);
                    //bundles = ArrayUtils.add(bundles,bundle);
                    for(Map.Entry<String, IExecutor> executor : bundleManager.getBundleData(bundle).getExecutors().entrySet()){
                        //Console.fine("+Executor : "+executor);
                        String color;
                        if(executor.getValue().isProxy()){
                            color = Colors.RED;
                        }else {
                            color = Colors.CYAN;
                        }
                        bundles = ArrayUtils.add(bundles,color+bundle+Colors.YELLOW_BOLD+"/"+Colors.WHITE_BOLD_BRIGHT+executor.getKey());
                    }
                }
                if(withSimplifiedName){
                    for(IExecutor exec: DNCoreAPI.getInstance().getContainer().getJVMExecutors()){
                        if(exec.getBundleData() != null){
                            //Check if the executor is the only one with this name
                            if(DNCoreAPI.getInstance().getContainer().getJVMExecutorsFromName(exec.getName()).length == 1){
                                bundles = ArrayUtils.add(bundles, Colors.WHITE_BOLD_BRIGHT_UNDERLINED+exec.getName());
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
