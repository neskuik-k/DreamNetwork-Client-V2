package be.alexandre01.dreamnetwork.core.service.bundle;

import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.bundle.BService;
import be.alexandre01.dreamnetwork.api.service.bundle.IBundleInfo;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlPreLoader;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
public class BundleInfo implements IBundleInfo, YamlPreLoader {
    public ArrayList<BService> services;
    public String name;
    public IContainer.JVMType type;
    public ExecType execType = ExecType.SERVER;
    public boolean mergeBundle = false;
    @Ignore transient private File file;
    @Setter @Ignore transient private YamlFileUtils<BundleInfo> yaml;

    public BundleInfo(File file,String name, ExecType execType){
            this.file = file;
            this.name = name;
            this.type = execType.isProxy() ? IContainer.JVMType.PROXY : IContainer.JVMType.SERVER;
            this.execType = execType;

            this.services = new ArrayList<>();
    }

    public BundleInfo(){
        this.services = new ArrayList<>();
    }

    @Override
    public void whenLoaded() {
        if(execType == null){
            if(type == IContainer.JVMType.PROXY){
                execType = ExecType.ANY_PROXY;
            }else{
                execType = ExecType.SERVER;
            }
        }
    }
    @Override
    public ArrayList<BService> getBServices(){
        //list files directories stream
        File[] directories = new File(Config.getPath("bundles/server/")).listFiles(File::isDirectory);
        ArrayList<BService> bServices = new ArrayList<>();
        return null;
    }


    /*public static void updateFile(File file, BundleInfo bundleInfo) {


        try {
            if (!file.exists())
                file.createNewFile();

        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture de .info");
            throw new RuntimeException(e);
        }
    }*/

    public static BundleInfo loadFile(File file) {
        Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
        try {

            LinkedHashMap<String, Object> map = yaml.load(new FileInputStream(file));
            if (map.isEmpty()) {
                return null;
            }
            Gson gson = new Gson();
            BundleInfo t = gson.fromJson(gson.toJsonTree(map), BundleInfo.class);
            t.file = file;
            //  YamlFileUtils.this.readFile();
            return t;
        } catch (FileNotFoundException e) {
            System.out.println(Colors.RED + "Error while loading bundle " + file.getName() + Colors.RESET);
            e.printStackTrace();
            return null;
        }
    }


}
