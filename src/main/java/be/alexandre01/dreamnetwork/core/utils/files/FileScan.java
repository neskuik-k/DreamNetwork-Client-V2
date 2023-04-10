package be.alexandre01.dreamnetwork.core.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class FileScan {

    private String[] lines = new String[0];

    private boolean running = false;

    public FileScan(File file){
        try {
            scan(new LangScanListener() {
                @Override
                public void onScan(String line) {
                    //add line to array lines
                    lines = Arrays.copyOf(lines, lines.length+1);
                    lines[lines.length-1] = line;
                }
            },new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public FileScan(InputStream in){
            scan(new LangScanListener() {
                @Override
                public void onScan(String line) {
                    //add line to array lines
                    lines = Arrays.copyOf(lines, lines.length+1);
                    lines[lines.length-1] = line;
                }
            },in);
    }



    private void scan(LangScanListener listener, InputStream in){
        Scanner scan = new Scanner(in, "UTF-8");
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            listener.onScan(line);
        }
        scan.close();
    }

    public void scan(LangScanListener listener){
        running = true;
        for(String line : lines){
            if(!running){break;}
            listener.onScan(line);
        }
    }

    public void stop(){
        running = false;
    }

    public interface LangScanListener{
        void onScan(String line);
    }
}
