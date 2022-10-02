package be.alexandre01.dreamnetwork.core.console.language;

import java.util.HashMap;

public class LanguageContainer {
    private String index = "en";
    private HashMap<String,String> messages = new HashMap<>();

    public void addLanguage(String key, String value) {
        messages.put(key, value);
    }
}
