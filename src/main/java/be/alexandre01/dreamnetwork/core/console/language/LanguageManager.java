package be.alexandre01.dreamnetwork.core.console.language;

import be.alexandre01.dreamnetwork.core.config.Config;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class LanguageManager {
    @Getter private static final String[] availableLanguages = {"en_EN", "fr_FR", "de_DE"};

    private static final File langFile = Config.createFile("lang/language.lang");
    private static final File keysFile = Config.createFile("lang/keys");
    private static HashMap<String, String> messages;
    private static List<String> settedKeys;
    private static String actualLang = "en_EN";


    public static boolean load(){
        Config.createDir("lang");

        if(!writeFile("files/lang/keys", keysFile)){return false;}

        if(langFile.length() == 0L){
            if(!writeFile("files/lang/en_EN.lang", langFile)){return false;}
        }

        return loadLanguage();
    }

    public static boolean useLanguage(String lang){
        if(!isLanguageAvailable(lang)){return false;}


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

    private static boolean loadLanguage(){
        messages = new HashMap<>();
        settedKeys = new ArrayList<>();

        try {
            Scanner scan = new Scanner(keysFile);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if(line.equals("")){continue;}
                settedKeys.add(line);
            }

            scan = new Scanner(langFile);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if(line.startsWith("##") || line.equals("")){continue;}

                if(line.startsWith("lang=")){
                    actualLang = line.replace("lang=", "");
                    continue;
                }
                String[] info = line.split("=");
                if(info.length != 2){
                    System.out.println(info[0]);
                    return useLanguage(actualLang);
                }
                for(ColorsConverter color : ColorsConverter.values()){info[1] = info[1].replace("%" + color.toString().toLowerCase() + "%", color.getColor());}
                messages.put(info[0], info[1].replace("\\n", "\n").replace("\\r", "\r"));
            }
        }catch (FileNotFoundException fnfe){return false;}
        for(String key : settedKeys){
            if(!messages.containsKey(key)){
                System.out.println(key);
                return useLanguage(actualLang);
            }
        }
        return true;
    }

    private static boolean writeFile(String inputStringPath, File file){
        try{
            file.delete();
            file.createNewFile();
            InputStream in = LanguageManager.class.getClassLoader().getResourceAsStream(inputStringPath);
            Config.write(in, file);
            return true;
        }catch (IOException ioe){
            return false;
        }
    }

    public static String getMessage(String path){
        return messages.getOrDefault(path, "INVALID PATH");
    }
}
