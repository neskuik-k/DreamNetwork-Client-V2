package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.service.JVMContainer;
import org.apache.commons.lang.ArrayUtils;
import org.jline.builtins.Completers;

public class AllServersNode extends CustomType {
    public AllServersNode(){
        type = SubCommandCompletor.Type.SERVERS;
        JVMContainer jvmContainer = Core.getInstance().getJvmContainer();
        setCustomType(() -> {
            //Completers.TreeCompleter
            String[] servers = jvmContainer.getJVMExecutorsServers().keySet().toArray(new String[0]);
            String[] proxies = jvmContainer.getJVMExecutorsProxy().keySet().toArray(new String[0]);

            if(servers.length == 0 && proxies.length == 0){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return ArrayUtils.addAll(servers,proxies);
        });
    }
}
