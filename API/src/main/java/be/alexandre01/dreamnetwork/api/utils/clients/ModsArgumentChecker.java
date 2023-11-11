package be.alexandre01.dreamnetwork.api.utils.clients;

import be.alexandre01.dreamnetwork.api.service.IExecutor;

public class ModsArgumentChecker {
    public static boolean check(String arg){
        try {
            IExecutor.Mods.valueOf(arg);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }
}
