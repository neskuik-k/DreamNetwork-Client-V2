package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import org.jline.builtins.Completers;

public class AllServersNode extends CustomType {
    public AllServersNode(){
        type = SubCommandCompletor.Type.SERVERS;
        IContainer jvmContainer = DNCoreAPI.getInstance().getContainer();
        setCustomType(() -> {
            //Completers.TreeCompleter
            String[] servers = jvmContainer.getExecutors().toArray(new String[0]);
           // String[] proxies = jvmContainer.getJVMExecutorsProxy().keySet().toArray(new String[0]);

            if(servers.length == 0 /*&& proxies.length == 0*/){
                return new Object[]{Completers.AnyCompleter.INSTANCE};
            }
            return servers;//ArrayUtils.addAll(servers,proxies);
        });
    }
}
