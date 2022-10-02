package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import org.jline.builtins.Completers;

public class ProxiesNode extends CustomType {
    public ProxiesNode(){
        type = SubCommandCompletor.Type.SERVERS;
        JVMContainer jvmContainer = Core.getInstance().getJvmContainer();
        setCustomType(() -> {
            //Completers.TreeCompleter

            String[] proxies = jvmContainer.getJVMExecutorsProxy().keySet().toArray(new String[0]);

            if(proxies.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return proxies;
        });
    }
}
