package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import org.apache.commons.lang.ArrayUtils;
import org.jline.builtins.Completers;

public class BundlesNode extends CustomType {

    public BundlesNode(boolean recursive){
        type = SubCommandCompletor.Type.SERVERS;
        BundleManager bundleManager = Core.getInstance().getBundleManager();
        if(recursive){
            setCustomType(() -> {
                //Completers.TreeCompleter

                Object[] bundles = new Object[0];
                for(String bundle : bundleManager.getBundleDatas().keySet()){
                    bundles = ArrayUtils.add(bundles,bundle);
                    for(String executor : bundleManager.getBundleData(bundle).getExecutors().keySet()){
                        bundles = ArrayUtils.add(bundles,bundle+"/"+executor);
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
