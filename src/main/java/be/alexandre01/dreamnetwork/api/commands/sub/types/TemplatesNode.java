package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.bundle.BundleData;
import org.jline.builtins.Completers;

public class TemplatesNode extends CustomType{
    public TemplatesNode(String bundle){
        type = SubCommandCompletor.Type.SERVERS;
        if(bundle == null){
            setCustomType(() -> {
                //Completers.TreeCompleter

                String[] bundles = Core.getInstance().getJvmContainer().jvmExecutors.toArray(new String[0]);

                if(bundles.length == 0){
                    return new Object[]{Completers.AnyCompleter.INSTANCE};
                }
                return bundles;
            });
            return;
        }
        setCustomType(() -> {
            //Completers.TreeCompleter
            BundleData bundleData = Core.getInstance().getBundleManager().getBundleData(bundle);
            if(bundleData == null){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }


            String[] bundles =  bundleData.getExecutors().values().toArray(new String[0]);

            if(bundles.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return bundles;
        });
    }
}
