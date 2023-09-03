package be.alexandre01.dreamnetwork.api.utils.clients;

public class NumberArgumentCheck {
public static boolean check(String arg){
       try {
              Integer.parseInt(arg);
              return true;
       } catch (NumberFormatException e) {
           return false;
       }
}

}
