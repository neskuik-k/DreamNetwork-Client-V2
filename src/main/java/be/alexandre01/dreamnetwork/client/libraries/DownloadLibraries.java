package be.alexandre01.dreamnetwork.client.libraries;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DownloadLibraries {

    private File dir;

    public void init() {
        try {

            dir = new File("libs");
            if (dir.exists())
                return;

            dir.mkdirs();

            List<EnumLibraries> libs = Arrays.asList(EnumLibraries.values());

            for (int i = 0; i < libs.size(); i++) {
                String[] split = libs.get(i).getUrl().split("/");

                try (BufferedInputStream in = new BufferedInputStream(new URL(libs.get(i).getUrl()).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream("libs/"+ split[split.length-1])) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    // handle exception
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
