package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import org.jline.builtins.Completers;

public class TemplatesNode extends CustomType{
    public TemplatesNode(String bundle){
        type = SubCommandCompletor.Type.SERVERS;
        if(bundle == null){
            setCustomType(() -> {
                //Completers.TreeCompleter

                String[] bundles = DNCoreAPI.getInstance().getContainer().getJVMExecutors().toArray(new String[0]);

                if(bundles.length == 0){
                    return new Object[]{Completers.AnyCompleter.INSTANCE};
                }
                return bundles;
            });
            return;
        }
        setCustomType(() -> {
            //Completers.TreeCompleter
            BundleData bundleData = DNCoreAPI.getInstance().getBundleManager().getBundleData(bundle);
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
