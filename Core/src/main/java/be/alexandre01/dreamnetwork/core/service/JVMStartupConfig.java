package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.service.*;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.installer.enums.InstallationLinks;
import be.alexandre01.dreamnetwork.api.service.bundle.BundleData;
import be.alexandre01.dreamnetwork.api.service.enums.ExecType;
import be.alexandre01.dreamnetwork.api.utils.files.yaml.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Optional;

import static be.alexandre01.dreamnetwork.api.console.Console.fine;

@Getter @Setter @Ignore @JsonIgnoreProperties(ignoreUnknown = true, allowGetters = false)
public class JVMStartupConfig extends JVMConfig implements IStartupConfig{
    @JsonIgnore
    boolean isConfig;
    @JsonIgnore long confSize = 0;
    @JsonIgnore boolean proxy = false;
    @JsonIgnore boolean fixedData = false;
    @JsonIgnore File fileRootDir;
   // String startup;

    public static ConfigBuilder builder(){
        return new ConfigBuilder();
    }
    public static ConfigBuilder builder(IConfig config){
        return new ConfigBuilder(config);
    }



    public JVMStartupConfig(String pathName,String name, JVMExecutor.Mods type, String xms, String xmx, int port, boolean proxy,boolean updateFile,String customName){
        super();
        config(new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/"+"network.yml"));
        this.startup = super.startup;
        this.name = name;
        this.type = type;
        this.xms = xms;
        this.xmx = xmx;
        this.port = port;
        this.proxy = proxy;
        this.pathName = pathName;
        this.fixedData = true;
        this.fileRootDir =  new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/");
        this.defaultName = customName;
        if(proxy){
            executable = "Proxy.jar";
        }else {
            executable = "Spigot.jar";
        }


        if(updateFile){
            updateConfigFile(pathName,name,type,xms,xmx,port,proxy,null,null,customName);
            Console.printLang("service.startupConfig.updatingFile");
        }

        if(startup != null){
            while (startup.charAt(0) == ' '){
                startup = startup.substring(1);
            }
            startup =  startup.replaceAll("%xms%",xms);

            startup =  startup.replaceAll("%xmx%",xmx);
        }


            isConfig = true;
    }

    public JVMStartupConfig(String pathName,String name,boolean isBuilded){
        //config(new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/network.yml"));
        this.name = name;
        this.pathName = pathName;
        this.fileRootDir =  new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/");
        if(isBuilded) return;
        saveFile();
        //System.out.println("Reading file ! Done !");
        //sout all class data fields with reflection
       // Field[] fields = JVMConfig.class.getDeclaredFields();
    }


    public void saveFile(){
       Optional<ConfigData> opt = super.getYmlFile().init(new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/network.yml"),true);

        if(!opt.isPresent()) return;

        ConfigData config = opt.get();


        // Copy all data from config to this class
        // get declaredfields and fields


        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Field field1 = ConfigData.class.getDeclaredField(field.getName());
                field1.setAccessible(true);
                if(field1.getAnnotation(Ignore.class) != null) continue;
                field1.set(this,field.get(config));
                //Console.printLang("service.startupConfig.settingField", field.getName(), field.get(config));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        //executable += ".jar";
        isConfig = true;
    }
    @Override
    public boolean changePort(String pathName, String finalname, int port, int defaultPort, IContainer.JVMType jvmType, JVMExecutor.Mods mods){
        fine("Changing port: " + finalname + ":" + defaultPort + " to " + port);
        String name = finalname.split("-")[0];
        String fileName = null;
        String checker = null;
        boolean proxy = false;

        if(jvmType.equals(IContainer.JVMType.SERVER)){
            fileName = "server.properties";
            checker = "server-port=";
        }else {
            InstallationLinks link;
            try {
                link = InstallationLinks.valueOf(getInstallInfo());
            }catch (Exception e){
                System.out.println("The server executable type cannot be determined");
                return false;
            }

            if(link.getExecType() == ExecType.BUNGEECORD){
                proxy = true;
                fileName = "config.yml";
                checker = "host: 0.0.0.0:";
            }

            if(link.getExecType() == ExecType.VELOCITY){
                proxy = true;
                fileName = "velocity.toml";
                checker = "bind = \"0.0.0.0:";
            }
        }
        File properties;
        if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
            properties = new File(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName));
        }else {
            properties = new File(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName));
        }

        if(!properties.exists()){
            return false;
        }
        try {
            BufferedReader file;

            file = new BufferedReader( new FileReader(properties));

         //   System.out.println(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName));

            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                if(line.contains(checker)){
                    fine("Checking line : "+line);
                    line = line.replace(String.valueOf(defaultPort),String.valueOf(port));
                    fine("New line : "+line);
                }
                inputBuffer.append(line);

                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut;
            if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
                String out = System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName);
                fileOut = new FileOutputStream(out);
                fine("Writing file : "+out);
            }else {
                String out = System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName);
                fileOut = new FileOutputStream(out);
                fine("Writing file : "+out);
            }
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
            return true;
        } catch (Exception e) {
            Console.printLang("service.startupConfig.readingProblem");
            return false;
        }

    }

    @Override
    public Integer getCurrentPort(String pathName, String finalname, IContainer.JVMType jvmType, JVMExecutor.Mods mods){
        String fileName = null;
        String checker = null;
        boolean proxy = false;
        if(jvmType == IContainer.JVMType.SERVER){
            fileName = "server.properties";
            checker = "server-port=";
        }else {
            if(getInstallInfo() == null) {
                System.out.println("The server executable type cannot be determined");
                return null;
            }
            InstallationLinks link;
            try {
                link = InstallationLinks.valueOf(getInstallInfo());
            }catch (Exception e){
                System.out.println("The server executable type cannot be determined");
                return null;
            }

            if(link.getExecType() == ExecType.BUNGEECORD){
                proxy = true;
                fileName = "config.yml";
                checker = "host: 0.0.0.0:";
            }

            if(link.getExecType() == ExecType.VELOCITY){
                proxy = true;
                fileName = "velocity.toml";
                checker = "bind = \"0.0.0.0:";
            }

            if(checker == null){
                System.out.println("The server executable type cannot be determined");
                return null;
            }


        }
        String name = finalname.split("-")[0];
        File properties;
        if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
            properties = new File(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName));
        }else {
            properties= new File(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName));
        }

        if(!properties.exists()){
            System.out.println("Properties file does not exist + "+properties.getAbsolutePath());
            return null;
        }
        try {

            BufferedReader file = new BufferedReader( new FileReader(properties));


            StringBuffer inputBuffer = new StringBuffer();

            String line;
            Integer port = null;

            while ((line = file.readLine()) != null) {

                if(line.contains(checker)){


                    String readline = line.replace(checker,"").replace("\"","").replaceAll(" ","");
                    //  System.out.println(readline);
                    port = Integer.parseInt(readline);
                    //  System.out.println(port);

                }
                inputBuffer.append(line);

                inputBuffer.append('\n');
            }
            file.close();

            FileOutputStream fileOut;
            if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
                fileOut = new FileOutputStream(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName));
            }else {
                fileOut = new FileOutputStream(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName));
            }
            // write the new string with the replaced line OVER the same file

            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
            return port;

        } catch (Exception e) {
            Console.printLang("service.startupConfig.readingProblem");
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String getLine(String finalname){
        String name = finalname.split("-")[0];
        File properties = new File(System.getProperty("user.dir")+ Config.getPath("/runtimes/server/"+name+"/"+finalname+"/logs/latest.log"));
        if(!properties.exists()){
            return null;
        }
        try {

            BufferedReader file = new BufferedReader( new FileReader(System.getProperty("user.dir")+ Config.getPath("/runtimes/server/"+name+"/"+finalname+"/logs/latest.log")));

            String line = null;

            while ((line = file.readLine()) != null){
                if(line.toLowerCase().startsWith(finalname.toLowerCase())){
                    StringBuilder f = new StringBuilder(line.replace(finalname,""));
                    int c = 0;
                    while (f.charAt(c) == ':' || f.charAt(c) == ' '){
                         f.deleteCharAt(c);
                         c++;
                    }

                    return f.toString();
                }
            }

            file.close();

            // write the new string with the replaced line OVER the same file



        } catch (Exception e) {
            Console.printLang("service.startupConfig.readingProblem");
        }
        return null;
    }
    @Override
    public void addConfigsFiles(){
       // System.out.println("PROCESS ADD CONFIG");
        InputStream isp = getClass().getClassLoader().getResourceAsStream("files/universal/DreamNetwork-Plugin.jar");
        try {
            assert isp != null;
            Config.createDir(Config.getPath(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/"+"plugins"),false);
            Config.write(isp,new File(Config.getPath(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/plugins/DreamNetwork-Plugin.jar")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(proxy){
            if(getInstallInfo() == null) {
                System.out.println("The server executable type cannot be determined");
                return;
            }

            InstallationLinks link;
            try {
                link = InstallationLinks.valueOf(getInstallInfo());
            }catch (Exception e){
                System.out.println("The server executable type cannot be determined");
                return;
            }

            System.out.println("CONFIG " + link.getExecType());
            if(link.getExecType() == ExecType.BUNGEECORD || link.getExecType() == ExecType.ANY_PROXY){
                updateFile("config.yml",getClass().getClassLoader().getResourceAsStream("files/bungeecord/config.yml"));
                updateFile("server-icon.png",getClass().getClassLoader().getResourceAsStream("files/bungeecord/server-icon.png"));
                return;
            }
            if(link.getExecType() == ExecType.VELOCITY){
                updateFile("velocity.toml",getClass().getClassLoader().getResourceAsStream("files/velocity/velocity.toml"));
                updateFile("server-icon.png",getClass().getClassLoader().getResourceAsStream("files/velocity/server-icon.png"));
                //generate random string password with 32 characters for forwarding secret
                File file = new File(Config.getPath(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/forwarding.secret"));
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(file));
                    //generate random string password with 32 characters
                    String key = RandomStringUtils.randomAlphanumeric(32);
                    writer.write(key);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            return;
        }
        updateFile("eula.txt",getClass().getClassLoader().getResourceAsStream("files/spigot/eula.txt"));
        updateFile("server.properties",getClass().getClassLoader().getResourceAsStream("files/spigot/server.properties"));
        updateFile("bukkit.yml",getClass().getClassLoader().getResourceAsStream("files/spigot/bukkit.yml"));
        BundleData b = DNCoreAPI.getInstance().getBundleManager().getBundleData("proxies");
        ExecType proxyType = ExecType.BUNGEECORD;
        if(b != null){
            if(b.getBundleInfo() != null){
                proxyType = b.getBundleInfo().getExecType();
            }
        }
        String path = proxyType == ExecType.BUNGEECORD ? "files/spigot/spigot4bungee.yml" : "files/spigot/spigot4velo.yml";
        updateFile("spigot.yml",getClass().getClassLoader().getResourceAsStream(path));
    }

    private void updateFile(String fileName, InputStream in){
        assert in != null;
        try {
            Config.write(in,new File(Config.getPath(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/"+fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateConfigFile(){

        updateConfigFile(pathName,name,type,xms,xmx,port,proxy,executable,javaVersion,defaultName);
    }
    @Override
    public void updateConfigFile(String pathName, String finalName, JVMExecutor.Mods type, String Xms, String Xmx, int port, boolean proxy, String exec, String javaVersion,String customName){
        this.type = type;
        this.xms = Xms;
        this.xmx = Xmx;
        this.port = port;
        this.proxy = proxy;
        this.javaVersion = javaVersion;
        this.executable = exec;
        this.pathName = pathName;
        this.name = finalName;
        this.defaultName = customName;
        fine(this.installInfo);
        /*Console.print("PN>"+pathName, Level.FINE);
        Console.print("FN>"+finalName,Level.FINE);
        Console.print("MODS>"+type.name(),Level.FINE);
        Console.print("XMS>"+Xms,Level.FINE);
        Console.print("XMX>"+Xmx,Level.FINE);
        Console.print("PORT>"+port,Level.FINE);
        Console.print("PROXY>"+proxy,Level.FINE);
        Console.print("STARTUP>"+startup,Level.FINE);
        // Client.getLogger().
        Config.createFile((System.getProperty("user.dir")+"/bundles/"+pathName+"/"+finalName+"/network.yml"));
        String cTypeName = getLine("type");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(System.getProperty("user.dir")+ Config.getPath("/bundles/"+pathName+"/"+finalName+"/network.yml"),"utf-8");

            //  System.out.println(type.name());
            writer.println("# "+finalName+"'s configuration of the startup -|- DreamNetwork™ "+ (new Date().getYear()+1900));
            if(type != null){
                writer.println("type: "+type.name());
            }
            if(Xms != null){
                writer.println("xms: "+Xms);
            }
            if(Xmx != null){
                writer.println("xmx: "+Xmx);
            }
            if(port != 0){
                writer.println("port: "+port);
            }
            if(exec != null){
                writer.println("executable: "+exec);
            }
            if(startup != null){
                writer.println("startup: "+startup);
            }
            if(javaVersion != null){
                writer.println("java-version: "+javaVersion);
            }
            writer.println("proxy: "+proxy);
            writer.close();

            confSize = getConfigSize();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        super.getYmlFile().saveFile(ConfigData.class.cast(this));
        confSize = getConfigSize();

    }
    @Override
    public long getConfigSize(){
        return  Config.createFile((System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/network.yml")).length();
    }
    @Override
    public boolean hasExecutable(){
        if(Config.contains(System.getProperty("user.dir")+Config.getPath("/bundles/"+pathName+"/"+name+"/"+ executable))){
            return true;
        }
        return false;
    }

}
