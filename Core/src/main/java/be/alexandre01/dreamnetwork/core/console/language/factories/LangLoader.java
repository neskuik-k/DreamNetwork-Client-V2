package be.alexandre01.dreamnetwork.core.console.language.factories;

import be.alexandre01.dreamnetwork.api.utils.files.FileScan;
import be.alexandre01.dreamnetwork.api.console.language.ColorsConverter;
import be.alexandre01.dreamnetwork.core.console.language.EmojiManager;
import be.alexandre01.dreamnetwork.core.console.language.Language;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;

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
                if(line.startsWith("##") || line.equals(""))
                    return;
                String key = line.split("=")[0];
                String value = line.split("=")[1];
            //    System.out.println(key+"="+convert(value));
                language.getMessages().put(key, convert(value));
            }
        });

        if(keysManager == manager.getDefaultKeysManager())
            return language;
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

            //Console.fine("Replacing " + key + " by default version");
            language.getMessages().put(key, convert(manager.getDefaultLanguage().getMessages().get(key))+" (DefaultFT)");
        }

        return language;
    }

    private String convert(String line){
        line = EmojiManager.Converter.convert(line);
        for(ColorsConverter color : ColorsConverter.values()){line = line.replace("%" + color.toString().toLowerCase() + "%", color.getColor());}
        String newLine = line;
        if(line.contains("%var%")){
            newLine = "";
            // split by %var% and replace by var+number
            String[] split = line.split("%var%");

            for(int i = 0; i < split.length; i++){
                newLine += split[i];

               // System.out.println("Split " + line + " to " + split[i]);
                if(i != split.length-1 || line.replace(" ","").endsWith("%var%")){
                    newLine += "%var"+i+"%";
                }
            }
           // System.out.println("Converted " + line + " to " + newLine);
        }
        return newLine.replace("\\n", "\n").replace("\\r", "\r");
    }
}
