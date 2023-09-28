package be.alexandre01.dreamnetwork.api.console.language;

import be.alexandre01.dreamnetwork.api.DNUtils;


/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 20:13
*/
public interface ILanguageManager {

    String[] getAvailableLangArray();

    boolean load();

    void loadDifferentLanguage(String lang);

    void forceLoad(String lang);

    IEmojiManager getEmojiManager();

    ILanguage getDefaultLanguage();

    ILanguage getActualLanguage();

    static String getMessage(String key, Object... params) {
        ILanguageManager languageManager = DNUtils.get().getConfigManager().getLanguageManager();
        if (languageManager.getActualLanguage().getMessages().containsKey(key)) {
            return languageManager.getActualLanguage().translateTo(key, params);
        }
        return "Key not found [ " + key + " ]";
    }
}
