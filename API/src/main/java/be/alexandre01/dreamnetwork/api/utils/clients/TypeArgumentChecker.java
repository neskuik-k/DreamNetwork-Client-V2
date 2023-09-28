package be.alexandre01.dreamnetwork.api.utils.clients;

import be.alexandre01.dreamnetwork.api.service.IContainer;

public class TypeArgumentChecker {
    public static boolean check(String arg){
        try {
            IContainer.JVMType.valueOf(arg);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }
}
