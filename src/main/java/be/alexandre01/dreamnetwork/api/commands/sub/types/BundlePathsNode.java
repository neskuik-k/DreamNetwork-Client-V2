package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleManager;
import org.jline.builtins.Completers;

public class BundlePathsNode extends CustomType {
    public BundlePathsNode(){
        type = SubCommandCompletor.Type.SERVERS;
        BundleManager bundleManager = Core.getInstance().getBundleManager();
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
