package be.alexandre01.dreamnetwork.core.console.language.factories;

import be.alexandre01.dreamnetwork.core.utils.files.FileScan;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeysManager {

    @Getter private final List<String> keys = new ArrayList<>();

    public void loadKeys(FileScan scan){
        scan.scan(new FileScan.LangScanListener() {
            @Override
            public void onScan(String line) {
                if(line.startsWith("##") || line.equals("")){return;}
                String key = line.split("=")[0];
                keys.add(key);
            }
        });
    }
}
