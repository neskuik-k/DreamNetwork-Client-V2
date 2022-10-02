package be.alexandre01.dreamnetwork.core.console;

public class ConsolePath {

    public static String getFromScreen(String name){
        return "s:"+name;
    }

    public static class Main{
        public static final String DEFAULT= "m:default";
        public static final String SPIGET = "m:spiget";
        public static final String STATS ="m:stats";
    }
}
