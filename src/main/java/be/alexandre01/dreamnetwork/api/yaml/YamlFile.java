package be.alexandre01.dreamnetwork.api.yaml;

import lombok.Getter;

import java.io.File;

public class YamlFile {
    @Getter private BasicYamlReader reader;
    @Getter private BasicYamlWriter writer;
    @Getter private YamlConfig yamlConfig;
    public YamlFile(File file,YamlConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
        writer = new BasicYamlWriter(file,yamlConfig,this);
        reader = new BasicYamlReader(file,yamlConfig,this);
    }
}
