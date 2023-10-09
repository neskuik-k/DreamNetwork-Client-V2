package be.alexandre01.dreamnetwork.core.console.language;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.console.colors.ColorsData;
import be.alexandre01.dreamnetwork.api.service.ConfigData;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.CustomRepresenter;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.api.yaml.YamlFile;
import be.alexandre01.dreamnetwork.core.service.JVMProfiles;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/10/2023 at 22:08
*/
public class ThemesMap {
    public YamlFileUtils<ColorsData> yamlFileUtils;

    public ThemesMap(){
        yamlFileUtils = new YamlFileUtils<>(ColorsData.class);
        File file = new File("data");
        if(!file.exists()){
            file.mkdir();
        }
        yamlFileUtils.addTag(String.class, Tag.MAP);


        yamlFileUtils.representer = new Representer(new DumperOptions()){
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                if (propertyValue == null) {
                    return null;
                }

                if(property.getType().equals(String.class)){
                    String value = (String) propertyValue;
                    value = value.replace("\u001B","\\u001B");
                    value = value.replace("\033","\\033");

                    propertyValue = value;
                }
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }


        };


        //addTag(JVMConfig2.class,new Tag("!be.alexandre01.dreamnetwork.core.service.JVMConfig2"));
        Optional<ColorsData> colors = yamlFileUtils.init(new File("data/Theme.yml"),true);

        if(!colors.isPresent()){
            System.out.println("Error while loading themes.yml");
            return;
        }

        ColorsData colorsData = colors.get();
        for (Field field : colorsData.getClass().getDeclaredFields()){
            try {
                if(field.getType() == String.class){
                    String value = (String) field.get(colorsData);
                    value = value.replace("\\u001B","\u001B");
                    value = value.replace("\\033","\033");
                    field.set(colorsData,value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Themes.yml loaded");
        DNUtils.get().setColorsData(colors.get());
    }
}
