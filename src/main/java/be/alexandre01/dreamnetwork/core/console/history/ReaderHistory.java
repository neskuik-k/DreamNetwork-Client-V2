package be.alexandre01.dreamnetwork.core.console.history;

import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.utils.json.JSONFileUtils;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ReaderHistory {
    private final File tokenFile = new File(System.getProperty("user.dir")+"/data/ReaderHistory.json");
   @Getter
   private ReaderHistoryIndex readerHistoryIndex;

    public void init(){
        if(!tokenFile.exists()){
            try {
                tokenFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(tokenFile.getAbsolutePath()));
            readerHistoryIndex = gson.fromJson(reader, ReaderHistoryIndex.class);
            if(readerHistoryIndex == null)
                readerHistoryIndex = new ReaderHistoryIndex();
            readerHistoryIndex.setIndexFile(tokenFile);

            if(readerHistoryIndex.isEmpty()){
                ArrayList<String> a = new ArrayList<>();
                a.add("help");
                readerHistoryIndex.put("history", Base64.getEncoder().encodeToString(convert(a).getBytes(StandardCharsets.UTF_8)));
                readerHistoryIndex.refreshFile();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String convert(List<String> s){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.size(); i++) {
            String w = s.get(i);
            sb.append(w.length());
            if(i == s.size()-1){
                sb.append(";");
            }else {
                sb.append("-");
            }
        }
        for (int i = 0; i < s.size(); i++) {
            String w = s.get(i);
            sb.append(w);
        }
        return sb.toString();
    }
    public static ArrayList<String> getFrom(String s){
        ArrayList<String> a = new ArrayList<>();
        String numbers = s.split(";")[0];
        String carac = s.substring(numbers.length()+1);

        String[] num = numbers.split("-");
        ArrayList<Integer> cutters = new ArrayList<>();
        for(String n : num){
            try {
                cutters.add(Integer.valueOf(n));
            }catch (Exception e){
                System.out.println(n);
                e.printStackTrace();
            }
        }

        for (int i = 0; i < cutters.size(); i++) {
            a.add(carac.substring(0,cutters.get(i)));
            carac = carac.substring(cutters.get(i));
        }
        return a;
    }
    public static class ReaderHistoryIndex extends JSONFileUtils{
        @Override
        public Object put(String key, Object value) {
            Object k = super.put(key, value);
            if(key.equalsIgnoreCase("history")){
                if(value instanceof String){
                    String s = (String) value;

                    s = new String(Base64.getDecoder().decode(s));
                    for(String h : getFrom(s)){
                        ConsoleReader.sReader.getHistory().add(h);
                    }

                }
            }
            return k;
        }
    }
}
