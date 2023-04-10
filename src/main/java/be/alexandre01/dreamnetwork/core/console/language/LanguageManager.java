package be.alexandre01.dreamnetwork.core.console.language;

import be.alexandre01.dreamnetwork.core.Main;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.language.factories.KeysManager;
import be.alexandre01.dreamnetwork.core.console.language.factories.LangLoader;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class LanguageManager {
    @Getter private static final String[] availableLanguages = {"en_EN", "fr_FR", "de_DE"};

    private File langFile;
    private File keysFile;
    private static HashMap<String, String> messages;
    private static List<String> settedKeys;
    @Getter private Language defaultLanguage;
    private static String actualLocalName = "en_EN";

    @Getter private KeysManager defaultKeysManager = new KeysManager();

    public boolean load(){
        Config.createDir("lang");
        System.out.println("Je m'amuse");
        InputStream en_EN = getInputFrom("en_EN");
        System.out.println(en_EN);
        LangLoader langLoader = new LangLoader(this);
        defaultKeysManager = langLoader.loadKeys(en_EN);
        defaultLanguage = langLoader.load(en_EN,"en_EN");
        InputStream fr_FR = getInputFrom("fr_FR");
        LangLoader french = new LangLoader(this);
        defaultLanguage = french.load(fr_FR,"fr_FR");


       // return loadLanguage();
        return true;
    }

    private InputStream getInputFrom(String localName){
        return LanguageManager.class.getClassLoader().getResourceAsStream("files/lang/" + localName + ".lang");
    }
    public static String getMessage(String key,Object... params){
        if(Main.getLanguageManager().getDefaultLanguage().getMessages().containsKey(key)){
            return Main.getLanguageManager().getDefaultLanguage().translateTo(key,params);
        }
        return "Key not found";
    }

    /*public static boolean useLanguage(String lang){
        if(!isLanguageAvailable(lang)){return false;}
        InputStream in = Language2Manager.class.getClassLoader().getResourceAsStream("");

        if(writeFile("files/lang/" + lang + ".lang", langFile)){
            actualLang = lang;
            return loadLanguage();
        }
        return false;
    }

    public static boolean isLanguageAvailable(String lang){
        for(String l : availableLanguages){
            if(l.equals(lang)){
                return true;
            }
        }
        return false;
    }

    public void loadLanguage(String localName){
        InputStream in = getInputFrom(localName);
        LangLoader langLoader = new LangLoader(in);
    }

    private static boolean loadLanguage(){
        messages = new HashMap<>();
        settedKeys = new ArrayList<>();

        try {
            scan = new Scanner(langFile);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                System.out.println(line);
                if(line.startsWith("##") || line.equals("")){continue;}

                if(line.startsWith("lang=")){
                    actualLang = line.replace("lang=", "");
                    continue;
                }
                String[] info = line.split("=");
                if(info.length != 2){
                    System.out.println(line);
                    System.out.println(info[0]);
                    return useLanguage(actualLang);
                }
                for(ColorsConverter color : ColorsConverter.values()){info[1] = info[1].replace("%" + color.toString().toLowerCase() + "%", color.getColor());}
                messages.put(info[0], info[1].replace("\\n", "\n").replace("\\r", "\r"));
            }
        }catch (FileNotFoundException fnfe){return false;}
        for(String key : settedKeys){
            if(!messages.containsKey(key)){
                return useLanguage(actualLang);
            }
        }
        return true;
    }

    private static boolean writeFile(String inputStringPath, File file){
        try{
            file.delete();
            file.createNewFile();
            InputStream in = Language2Manager.class.getClassLoader().getResourceAsStream(inputStringPath);
            Config.write(in, file);
            return true;
        }catch (IOException ioe){
            return false;
        }
    }

    public static String getMessage(String path){
        return messages.getOrDefault(path, "INVALID PATH");
    }*/
}
