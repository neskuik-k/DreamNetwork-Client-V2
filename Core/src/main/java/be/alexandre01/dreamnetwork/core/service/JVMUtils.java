package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.IJVMUtils;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 11:28
*/
public class JVMUtils implements IJVMUtils {
    @Override
    public IStartupConfig createStartupConfig(String pathname, String name, boolean b) {
        return new JVMStartupConfig(pathname,name,b);
    }

    @Override
    public IConfig createConfig() {
        return new JVMConfig();
    }
}
