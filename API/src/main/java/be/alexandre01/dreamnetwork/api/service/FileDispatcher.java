package be.alexandre01.dreamnetwork.api.service;

import be.alexandre01.dreamnetwork.api.DNCoreAPI;
import be.alexandre01.dreamnetwork.api.config.Config;
import be.alexandre01.dreamnetwork.api.console.Console;

import java.io.*;
import java.util.Collection;
import java.util.stream.Collectors;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 03/09/2023 at 10:41
*/
public class FileDispatcher {

    // It is not multithread safe, to use on startup only
    public void sendCustomsFileToProxies(InputStream in, String fileName){
        File[] dir;
        Collection<IExecutor> executors = DNCoreAPI.getInstance().getContainer().getProxiesExecutors();
        dir = executors.stream().map(IExecutor::getFileRootDir).collect(Collectors.toList()).toArray(dir = new File[executors.size()]);
        createCustomFiles(dir,in,fileName);
    }
    public void sendCustomsFileToServers(InputStream in,String fileName){
        File[] dir;
        Collection<IExecutor> executors = DNCoreAPI.getInstance().getContainer().getServersExecutors();
        dir = executors.stream().map(IExecutor::getFileRootDir).collect(Collectors.toList()).toArray(dir = new File[executors.size()]);
        createCustomFiles(dir,in,fileName);
    }
    public void createCustomFiles(File[] directory,InputStream in,String fileName){
        if(directory != null) {
            try {
                byte[] bytes = cloneInputStream(in);
                for (File dir : directory) {
                    String name = dir.getName();
                    //TRY TO LOAD COMPONENT
                    if (Config.contains( dir.getPath() + "/plugins")) {
                        File file = new File(dir.getPath()+"/plugins/"+fileName);
                        file.delete();
                        InputStream is = new ByteArrayInputStream(bytes);
                        replaceFile(is,dir.getPath()+"/plugins/",fileName);
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void replaceFile(InputStream in, String path, String fileName){

        try {
            assert in != null;
            Config.createDir(path,false);
            be.alexandre01.dreamnetwork.api.console.Console.fine(Console.getFromLang("bundle.replaceFile.writing", path, fileName));
            //  System.out.println(System.getProperty("user.dir")+Config.getPath(path+"/"+fileName));
            Config.write(in,new File(/*System.getProperty("user.dir")+*/Config.getPath(path+"/"+fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //clone inputstream without closing it
    private byte[] cloneInputStream(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        return out.toByteArray();
    }
}
