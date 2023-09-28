package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;


import be.alexandre01.dreamnetwork.api.service.bundle.IBundleManager;
import org.jline.builtins.Completers;

public class BundlePathsNode extends CustomType {
    public BundlePathsNode(){
        type = SubCommandCompletor.Type.SERVERS;
        IBundleManager bundleManager = DNCoreAPI.getInstance().getBundleManager();
        setCustomType(() -> {
            //Completers.TreeCompleter

            String[] bundles = bundleManager.getPaths().toArray(new String[0]);

            if(bundles.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return bundles;
        });
    }
}
