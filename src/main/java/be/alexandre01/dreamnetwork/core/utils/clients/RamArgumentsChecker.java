package be.alexandre01.dreamnetwork.core.utils.clients;

public class RamArgumentsChecker {
    public static boolean check(String arg){
        char[] args = arg.toCharArray();
        for (int i = 0; i < args.length; i++) {

            if(i != args.length - 1){
                if(!Character.isDigit(args[i])) {
                    return false;
                }
            }else {
                if(Character.isAlphabetic(args[i])){
                    return true;
                }
            }
        }
        return false;
    }
}

