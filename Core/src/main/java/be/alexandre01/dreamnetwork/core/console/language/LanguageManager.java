package be.alexandre01.dreamnetwork.core.console.language;

import be.alexandre01.dreamnetwork.api.console.language.ILanguageManager;
import be.alexandre01.dreamnetwork.core.console.language.factories.KeysManager;
import be.alexandre01.dreamnetwork.core.console.language.factories.LangLoader;
import be.alexandre01.dreamnetwork.core.Main;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LanguageManager implements ILanguageManager {
    @Getter private static final String[] availableLanguages = {"en_EN", "fr_FR"};
    @Getter @Setter private EmojiManager emojiManager;
    private File langFile;
    private File keysFile;
    private static HashMap<String, String> messages;
    private static List<String> settedKeys;
    @Getter private Language defaultLanguage;
    @Getter private Language actualLanguage;
    private static String actualLocalName = "en_EN";

    @Getter private KeysManager defaultKeysManager = new KeysManager();

    @Override
    public String[] getAvailableLangArray() {
        return availableLanguages;
    }

    @Override
    public boolean load(){
        emojiManager = new EmojiManager();
        emojiManager.load();
        InputStream en_EN = getInputFrom("en_EN");
        LangLoader langLoader = new LangLoader(this);
        defaultKeysManager = langLoader.loadKeys(en_EN);
        defaultLanguage = langLoader.load(en_EN,"en_EN");
        String lang = Main.getGlobalSettings().getLanguage();
        loadDifferentLanguage(lang);
       // return loadLanguage();
        return true;
    }

    @Override
    public void loadDifferentLanguage(String lang){
        if(lang.equals(defaultLanguage.getLocalizedName()) || !Arrays.asList(availableLanguages).contains(lang)){
            actualLanguage = defaultLanguage;
            return;
        }
        forceLoad(lang);
    }

    @Override
    public void forceLoad(String lang){
        InputStream searchIn = getInputFrom(lang);
        LangLoader searchLoader = new LangLoader(this);
        actualLanguage = searchLoader.load(searchIn,lang);
    }

    private InputStream getInputFrom(String localName){
        return LanguageManager.class.getClassLoader().getResourceAsStream("files/lang/" + localName + ".lang");
    }
    public static String getMessage(String key, Object... params) {
        if (Main.getLanguageManager().getActualLanguage().getMessages().containsKey(key)) {
            return Main.getLanguageManager().getActualLanguage().translateTo(key, params);
        }
        return "Key not found [ " + key + " ]";
    }


}
