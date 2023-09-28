package be.alexandre01.dreamnetwork.core.console.language;

import be.alexandre01.dreamnetwork.api.console.language.IEmojiManager;
import be.alexandre01.dreamnetwork.core.Main;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class EmojiManager implements IEmojiManager {
    public static HashMap<String, String> emojis = new HashMap<>();
    private boolean isLoaded = false;

    @Override
    @SneakyThrows
    public void load(){
        if(!Main.getGlobalSettings().isUseEmoji() || isLoaded){
            return;
        }
        // read emojis.json with ressource
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("files/lang/emoji.json");

        // is to String
        String data;
        //UTF8 String
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));


        StringBuilder sb = new StringBuilder();
        while((data = reader.readLine()) != null){
            sb.append(new String(data.getBytes(), StandardCharsets.UTF_8));
        }
        data = sb.toString();
        //System.out.println(data);


        //is to reader
        if(is == null){
            System.out.println("Emoji file not found");
            return;
        }
// convert JSON file to map
        Emoji[] emojis = new GsonBuilder().setLenient().create().fromJson(data, Emoji[].class);

        // print map entries
        for (Emoji emo : emojis) {
            //System.out.println(emo.emoji + " => " + Arrays.toString(emo.aliases));
            if(emo == null || emo.aliases == null || emo.emoji == null) continue;
            for (String alias : emo.aliases) {
                this.emojis.put(alias, emo.emoji);
            }
        }

        isLoaded = true;

        // close reader
        try {
            data = null;
            emojis = null;
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getEmoji(String key, String ifNot, String... ifYes){
        if(Main.getGlobalSettings().isUseEmoji() || isLoaded){
            String beforeIfYes = "";
            String afterIfYes = "";
            if(ifYes.length == 1){
                beforeIfYes = ifYes[0];
            }
            if(ifYes.length == 2){
                afterIfYes = ifYes[1];
            }
            return beforeIfYes+emojis.get(key)+afterIfYes;
        }
        return ifNot;
    }

    @Override
    public String getEmoji(String key, String ifNot){
        if(Main.getGlobalSettings().isUseEmoji()){
            return emojis.get(key);
        }
        return ifNot;
    }

    @Override
    public String getEmoji(String key){
        return getEmoji(key, "");
    }

    public static class Converter {
        public static String convert(String line) {

            StringBuilder builder = new StringBuilder();
            if (line.contains("(")) {
                // check text between : and :
                String[] parts = line.split(":");
                if(parts.length == 1) return line;
                builder = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    if (i % 2 == 1) {
                        // System.out.println(i);
                        // part between : and :
                        String part = parts[i];
                        // System.out.println(part);
                        //check in part ( and )

                        String subText = "";
                        if (part.contains("(")) {
                            String[] subParts = part.split("\\(");
                            subText = subParts[1].substring(0, subParts[1].length() - 1);
                            //   System.out.println("SubText > " + subText);
                        }
                        //subtext lenght + 2 equals (+aliase+)
                        String emojiText = part.substring(0, part.length() - (subText.length() + 2));
                        // System.out.println("Emoji > " + emojiText);
                        // check if emoji exists
                        //  System.out.println("Last Emoji > " + Main.getLanguageManager().getEmojiManager().getEmoji(emojiText, subText));

                        builder.append(Main.getLanguageManager().getEmojiManager().getEmoji(emojiText, subText));
                    } else {
                        builder.append(parts[i]);
                    }
                }


            } else {
                builder.append(line);
            }
            return builder.toString();
        }
    }

}
