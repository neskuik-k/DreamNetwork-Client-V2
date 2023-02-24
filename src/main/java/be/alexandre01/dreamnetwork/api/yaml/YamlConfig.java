package be.alexandre01.dreamnetwork.api.yaml;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YamlConfig {

    @Getter private ArrayList<String> extraLine = new ArrayList<>();
    @Getter private final Map<String, Object> defaults = new HashMap<>();
    @Getter private final Map<String,Object> objectMap = new HashMap<>();

    public void set(String path, Object value) {
        objectMap.put(path, value);
    }

    public void addExtraLine(String line){
        extraLine.add(line);
    }

    public void setDefault(String path, Object value) {
        defaults.put(path, value);
    }

    public Object get(String path) {
        return objectMap.get(path);
    }

    public <T> T get(String path, Class<T> clazz) {
        return clazz.cast(get(path));
    }

    public String getString(String path) {
        return (String) objectMap.get(path);
    }

    public int getInt(String path) {
        return (int) objectMap.get(path);
    }
}
