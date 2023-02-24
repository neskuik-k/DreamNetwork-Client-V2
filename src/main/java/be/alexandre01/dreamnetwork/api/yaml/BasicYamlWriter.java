package be.alexandre01.dreamnetwork.api.yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BasicYamlWriter {
    YamlFile yamlFile;
    File file;

    final ArrayList<String> lines = new ArrayList<>();


    public BasicYamlWriter(File file, YamlConfig yamlConfig, YamlFile yamlFile) {
        this.file = file;
        this.yamlFile = yamlFile;
    }

    public void buildingCache() {
        lines.addAll(yamlFile.getYamlConfig().getExtraLine());
        for (String key : yamlFile.getYamlConfig().getObjectMap().keySet()) {
            lines.add(key + ": " + yamlFile.getYamlConfig().getObjectMap().get(key));
        }
    }

    public void addCache(String line) {
        lines.add(line);
    }

    public void setCache(String line, int index) {
        lines.add(index, line);
    }

    public void saving() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                if (line.contains(".")) {
                    String[] split = line.split("\\.");
                    for (int i = 0; i < split.length; i++) {
                        for (int j = 0; j < i; j++) {
                            bufferedWriter.write("  ");
                        }
                        bufferedWriter.write(split[i]);
                        bufferedWriter.newLine();
                    }
                    return;
                }

                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
