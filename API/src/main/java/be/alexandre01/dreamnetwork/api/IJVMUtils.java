package be.alexandre01.dreamnetwork.api;

import be.alexandre01.dreamnetwork.api.console.accessibility.AccessibilityMenu;
import be.alexandre01.dreamnetwork.api.service.IConfig;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.api.service.tasks.TaskData;

import java.util.function.Supplier;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 00:50
*/
public interface IJVMUtils {
    public IStartupConfig createStartupConfig(String pathname,String name, boolean b);
    public IConfig createConfig();

    public Supplier<Void> createOperation(TaskData taskData);
}
