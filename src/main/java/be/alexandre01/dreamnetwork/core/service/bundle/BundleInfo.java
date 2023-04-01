package be.alexandre01.dreamnetwork.core.service.bundle;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;

@Getter
public class BundleInfo {
    public ArrayList<BService> services;
    public String name;
    public IContainer.JVMType type;

    private File file;

    public BundleInfo(String name, IContainer.JVMType type){
            this.name = name;
            this.type = type;
            this.services = new ArrayList<>();
    }

    public BundleInfo(){
        this.services = new ArrayList<>();
    }


    public ArrayList<BService> getBServices(){
        //list files directories stream
        File[] directories = new File(Config.getPath("bundles/server/")).listFiles(File::isDirectory);
        ArrayList<BService> bServices = new ArrayList<>();
        return null;
    }

    public static void updateFile(File file, BundleInfo bundleInfo){
        try {
            file.createNewFile();
            Representer representer = new Representer() {
                @Override
                protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {


                    if (BundleInfo.class.equals(property.getType())) {
                        return null;
                    }
                    else {
                        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                    }
                }
            };
            Yaml yaml = new Yaml(new SafeConstructor(),representer);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(yaml.dumpAsMap(bundleInfo));
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture de .info");
            throw new RuntimeException(e);
        }
    }

    public static BundleInfo loadFile(File file){
        Yaml yaml = new Yaml(new Constructor(BundleInfo.class));
        try {
            return yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println(Colors.RED+"Error while loading bundle "+file.getName()+Colors.RESET);
            e.printStackTrace();
          return null;
        }
    }
}
