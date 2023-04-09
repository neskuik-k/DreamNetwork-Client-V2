package be.alexandre01.dreamnetwork.core.console.language;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LanguageReader {

    LanguageFile languageFile;
    public LanguageReader(LanguageFile languageFile) {
        this.languageFile = languageFile;
    }
    public void readLanguage(LanguageContainer container) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(languageFile.getInput()));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                //Split line by '='
                String[] split = line.split("=");
                //Get key and value
                String key = split[0];
                //Remove firsts spaces
                String value = split[1].replaceFirst("^\\s+", "");
                container.addLanguage(key, value);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
