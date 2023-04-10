package be.alexandre01.dreamnetwork.core.service;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import be.alexandre01.dreamnetwork.api.service.IStartupConfig;
import be.alexandre01.dreamnetwork.api.service.IStartupConfigBuilder;
import be.alexandre01.dreamnetwork.core.config.Config;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.utils.files.yaml.Ignore;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Field;

@Getter @Setter @Ignore
public class JVMStartupConfig extends JVMConfig implements IStartupConfig{
    boolean isConfig;
    long confSize = 0;
    boolean proxy = false;
    boolean fixedData = false;
    File fileRootDir;

    public static Builder builder(){
        return new Builder();
    }



    public JVMStartupConfig(String pathName,String name, JVMExecutor.Mods type, String xms, String xmx, int port, boolean proxy,boolean updateFile){
        super();
        config(new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/"+"network.yml"));
        this.name = name;
        this.type = type;
        this.xms = xms;
        this.xmx = xmx;
        this.port = port;
        this.proxy = proxy;
        this.pathName = pathName;
        this.fixedData = true;
        this.fileRootDir =  new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/");

        if(proxy){
            exec = "BungeeCord.jar";
        }else {
            exec = "Spigot.jar";
        }


        if(updateFile){
            updateConfigFile(pathName,name,type,xms,xmx,port,proxy,null,null,null);
            Console.printLang("service.startupConfig.updatingFile");
        }


        try {
            for (String line : Config.getGroupsLines(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/network.yml")){
                if(line.startsWith("startup:")){
                    startup = line;
                    startup = startup.replace("startup:","");
                    while (startup.charAt(0) == ' '){
                        startup = startup.substring(1);
                    }
                    startup =  startup.replaceAll("%xms%",xms);

                    startup =  startup.replaceAll("%xmx%",xmx);
                }
                if(line.startsWith("executable:")){
                    exec = line;
                    exec = exec.replace("executable:","");
                    exec = exec+".jar";
                    while (exec.charAt(0) == ' '){
                        exec = exec.substring(1);
                    }
                }
                if(line.startsWith("java-version:")){
                    javaVersion = line;
                    javaVersion = javaVersion.replace("java-version:","");
                    while (javaVersion.charAt(0) == ' '){
                        this.javaVersion = javaVersion.substring(1);
                    }
                }
            }

            isConfig = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public JVMStartupConfig(String pathName,String name,boolean isBuilded){
        config(new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/"+"/network.yml"));
        this.name = name;
        this.pathName = pathName;
        this.fileRootDir =  new File(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/");
        System.out.println("Hello world !");
        if(isBuilded) return;
        System.out.println("Hello world  2!");
        readFile();
        Console.printLang("service.startupConfig.readingFile");
        //sout all class data fields with reflection
        Field[] fields = JVMConfig.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                System.out.println(field.getName() + " = " + field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    public void readFile(){
        JVMConfig config = read();

        // Copy all data from config to this class
        // get declaredfields and fields
        Field[] fields = config.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Field field1 = JVMConfig.class.getDeclaredField(field.getName());
                field1.setAccessible(true);
                if(field1.getAnnotation(Ignore.class) != null) continue;
                field1.set(this,field.get(config));
                Console.printLang("service.startupConfig.settingField", field.getName(), field.get(config));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        /*if(startup != null){
            while (startup.charAt(0) == ' '){
                startup = startup.substring(1);
            }
            startup =  startup.replaceAll("%xms%",xms);
            startup = startup.replaceAll("%xmx%",xmx);
        }*/
        exec += ".jar";
        isConfig = true;
       /* try {
            for (String line : Config.getGroupsLines(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/network.yml")){
                if(line.startsWith("type:")){
                    this.type = JVMExecutor.Mods.valueOf(line.replace("type:","").replaceAll(" ",""));
                }
                if(line.startsWith("xms:")){
                    this.xms = line.replace("xms:","").replaceAll(" ","");
                }
                if(line.startsWith("xmx:")){
                    this.xmx = line.replace("xmx:","").replaceAll(" ","");
                }
                if(line.startsWith("port:")){
                    this.port = Integer.parseInt(line.replace("port:","").replaceAll(" ",""));
                }
                if(line.contains("proxy: true")){
                    this.proxy = true;
                }
                if(proxy){
                    exec = "BungeeCord.jar";
                }else {
                    exec = "Spigot.jar";
                }

                if(line.startsWith("startup:")){
                    startup = line;
                    startup = startup.replace("startup:","");
                    while (startup.charAt(0) == ' '){
                        startup = startup.substring(1);
                    }
                    startup =  startup.replaceAll("%xms%",xms);
                    startup = startup.replaceAll("%xmx%",xmx);
                }
                if(line.startsWith("executable:")){
                    exec = line;
                    exec = exec.replace("executable:","");
                    while (exec.charAt(0) == ' '){
                        exec = exec.substring(1);
                    }
                }
                if(line.startsWith("java-version:")){
                    javaVersion = line;
                    javaVersion = javaVersion.replace("java-version:","");
                    while (javaVersion.charAt(0) == ' '){
                        this.javaVersion = javaVersion.substring(1);
                    }
                }
                isConfig = true;
            }
        }catch (IOException e){
            Console.print(Colors.ANSI_RED()+"The server in question is not yet configured",Level.SEVERE);
            isConfig = false;
            return;
        }*/
    }
    @Override
    public boolean changePort(String pathName, String finalname, int port, IContainer.JVMType jvmType, JVMExecutor.Mods mods){
        String name = finalname.split("-")[0];
        String fileName;
        String checker;
        boolean proxy = false;
        if(jvmType.equals(IContainer.JVMType.SERVER)){
            fileName = "server.properties";
            checker = "server-port=";
        }else {
            proxy = true;
            fileName = "files/bungeecord/config.yml";
            checker = "host: 0.0.0.0:";
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

            if(type.equals(JVMExecutor.Mods.DYNAMIC)){
                file = new BufferedReader( new FileReader(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName)));

            }else {
                file = new BufferedReader(new FileReader(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName)));
            }

            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                if(line.startsWith("server-port=")){
                    line = "server-port= "+ port;
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut;
            if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
                fileOut = new FileOutputStream(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName));
            }else {
                fileOut = new FileOutputStream(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName));
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
        String fileName;
        String checker;
        boolean proxy = false;
        if(jvmType == IContainer.JVMType.SERVER){
            fileName = "server.properties";
            checker = "server-port=";
        }else {
            proxy = true;
            fileName = "config.yml";
            checker = "host: 0.0.0.0:";
        }
        String name = finalname.split("-")[0];
        File properties;
        if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
            properties = new File(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName));
        }else {
            properties= new File(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name));
        }

        if(!properties.exists()){
            return null;
        }
        try {

            BufferedReader file;
            if(mods.equals(JVMExecutor.Mods.DYNAMIC)){
                file = new BufferedReader( new FileReader(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+finalname+"/"+fileName)));
            }else {
                file = new BufferedReader( new FileReader(System.getProperty("user.dir")+ Config.getPath(pathName+"/"+name+"/"+fileName)));
            }

            StringBuffer inputBuffer = new StringBuffer();

            String line;
            Integer port = null;

            while ((line = file.readLine()) != null) {

                if(line.contains(checker)){

                    String readline = line.replace(checker,"").replaceAll(" ","");
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
        System.out.println("PROCESS ADD CONFIG");
        InputStream isp = getClass().getClassLoader().getResourceAsStream("files/universal/DreamNetwork-Plugin.jar");
        try {
            assert isp != null;
            Config.createDir(Config.getPath(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/"+"plugins"));
            Config.write(isp,new File(Config.getPath(System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/plugins/DreamNetwork-Plugin.jar")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(proxy){
            updateFile("config.yml",getClass().getClassLoader().getResourceAsStream("files/bungeecord/config.yml"));
            updateFile("server-icon.png",getClass().getClassLoader().getResourceAsStream("files/bungeecord/server-icon.png"));
            return;
        }
        updateFile("eula.txt",getClass().getClassLoader().getResourceAsStream("files/spigot/eula.txt"));
        updateFile("server.properties",getClass().getClassLoader().getResourceAsStream("files/spigot/server.properties"));
        updateFile("bukkit.yml",getClass().getClassLoader().getResourceAsStream("files/spigot/bukkit.yml"));
        updateFile("spigot.yml",getClass().getClassLoader().getResourceAsStream("files/spigot/spigot.yml"));
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
    public void updateConfigFile(String pathName, String finalName, JVMExecutor.Mods type, String Xms, String Xmx, int port, boolean proxy, String exec, String startup, String javaVersion){
        this.type = type;
        this.xms = Xms;
        this.xmx = Xmx;
        this.port = port;
        this.proxy = proxy;
        this.startup = startup;
        this.javaVersion = javaVersion;
        this.exec = exec;
        this.pathName = pathName;
        this.name = finalName;
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
        super.readFile(JVMConfig.class.cast(this));
        confSize = getConfigSize();

    }
    @Override
    public long getConfigSize(){
        return  Config.createFile((System.getProperty("user.dir")+"/bundles/"+pathName+"/"+name+"/network.yml")).length();
    }
    @Override
    public boolean hasExecutable(){
        System.out.println(exec);
        System.out.println(System.getProperty("user.dir")+Config.getPath("/bundles/"+pathName+"/"+name+"/"+exec));
        if(Config.contains(System.getProperty("user.dir")+Config.getPath("/bundles/"+pathName+"/"+name+"/"+exec))){
            return true;
        }
        return false;
    }

    public static class Builder implements IStartupConfigBuilder {
        private String name;
        private String pathName;
        private JVMExecutor.Mods type;
        private String Xms;
        private String Xmx;
        private int port;
        private boolean proxy;

        private String exec;

        private String startup;

        private String javaVersion;




        @Override
        public Builder name(String name){
            this.name = name;
            return this;
        }
        @Override
        public Builder pathName(String pathName){
            this.pathName = pathName;
            return this;
        }

        @Override
        public Builder type(JVMExecutor.Mods type){
            this.type = type;
            return this;
        }
        @Override
        public Builder xms(String Xms){
            this.Xms = Xms;
            return this;
        }
        @Override
        public Builder xmx(String Xmx){
            this.Xmx = Xmx;
            return this;
        }
        @Override
        public Builder port(int port){
            this.port = port;
            return this;
        }
        @Override
        public Builder proxy(boolean proxy){
            this.proxy = proxy;
            return this;
        }

        @Override
        public Builder exec(String exec){
            this.exec = exec;
            return this;
        }

        @Override
        public Builder startup(String startup){
            this.startup = startup;
            return this;
        }

        @Override
        public Builder javaVersion(String javaVersion){
            this.javaVersion = javaVersion;
            return this;
        }


        @Override
        public IStartupConfig build(){
            JVMStartupConfig j =  new JVMStartupConfig(pathName,name,true);
            j.setType(type);
            j.setXms(Xms);
            j.setXmx(Xmx);
            j.setPort(port);
            j.setProxy(proxy);
            j.setJavaVersion(javaVersion);
            j.setExec(exec);
            j.setStartup(startup);
            return j;
        }

        @Override
        public IStartupConfig buildFrom(IStartupConfig config){
            IStartupConfig j = build();
            if(j.getType() == null)
                j.setType(config.getType());
            if(j.getXms() == null)
                j.setXms(config.getXms());
            if(j.getXmx() == null)
                j.setXmx(config.getXmx());
            if(j.getPort() == 0)
                j.setPort(config.getPort());
            if(j.getJavaVersion() == null)
                j.setJavaVersion(config.getJavaVersion());
            if(j.getExec() == null)
                j.setExec(config.getExec());
            if(j.getStartup() == null)
                j.setStartup(config.getStartup());

            j.setProxy(config.isProxy());
            return config;
        }
    }
}
