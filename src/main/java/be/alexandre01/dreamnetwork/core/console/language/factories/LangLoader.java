package be.alexandre01.dreamnetwork.core.console.language.factories;

import be.alexandre01.dreamnetwork.core.console.language.ColorsConverter;
import be.alexandre01.dreamnetwork.core.console.language.Language;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;
import be.alexandre01.dreamnetwork.core.utils.files.FileScan;

import java.io.InputStream;
import java.util.ArrayList;

public class LangLoader {

    LanguageManager manager;
    KeysManager keysManager;

    FileScan scan;
    public LangLoader(LanguageManager languageManager) {
        this.manager = languageManager;
    }

    public KeysManager loadKeys(InputStream in){
        scan = new FileScan(in);
        keysManager = new KeysManager();
        keysManager.loadKeys(scan);
        return keysManager;
    }

    public Language load(InputStream in, String localName){
        if(keysManager == null){
            loadKeys(in);
        }
        Language language = new Language(localName);

        scan.scan(new FileScan.LangScanListener() {
            @Override
            public void onScan(String line) {
                if(line.startsWith("##") || line.equals("")){return;}
                String key = line.split("=")[0];
                String value = line.split("=")[1];
                System.out.println(key+"="+value);
                language.getMessages().put(key, convert(value));
            }
        });

        if(keysManager == manager.getDefaultKeysManager()){return language;}
        //eliminate duplicate
        ArrayList<String> keys = new ArrayList<>(manager.getDefaultKeysManager().getKeys());


        for(int i = 0; i < keys.size(); i++){
            String key = keys.get(i);
            if(keysManager.getKeys().contains(key)){
                keys.remove(key);
                i--;
            }
        }

        for(String key : keys){
            System.out.println("Replacing " + key + " by default version");
            language.getMessages().put(key, convert(manager.getDefaultLanguage().getMessages().get(key))+" (DefaultFT)");
        }

        return language;
    }

    private String convert(String line){
        for(ColorsConverter color : ColorsConverter.values()){line = line.replace("%" + color.toString().toLowerCase() + "%", color.getColor());}
        return line.replace("\\n", "\n").replace("\\r", "\r").replace("%var%","%s");
    }
}
