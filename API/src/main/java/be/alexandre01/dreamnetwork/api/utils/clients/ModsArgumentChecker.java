package be.alexandre01.dreamnetwork.api.utils.clients;

import be.alexandre01.dreamnetwork.api.service.IJVMExecutor;

public class ModsArgumentChecker {
    public static boolean check(String arg){
        try {
            IJVMExecutor.Mods.valueOf(arg);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }
}
