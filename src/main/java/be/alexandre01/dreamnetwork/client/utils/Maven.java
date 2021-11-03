package be.alexandre01.dreamnetwork.client.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Maven {
    private static boolean loaded = false;
    private static String version;

    public static Optional<String> getVersionFromManifest(Class<?> clazz) {
        try {
            File file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.isFile()) {
                JarFile jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();
                Attributes attributes = manifest.getMainAttributes();
                final String version = attributes.getValue("Bundle-Version");
                return Optional.of(version);
            }
        } catch (Exception e) {
            // ignore
        }
        return Optional.empty();
    }

    private static void load(){
        version = getPrivateVersion();
        loaded = true;
    }

    public static String getVersion() {
        if(!loaded)
            load();
        return version;
    }

    private static String getPrivateVersion() {
        String version = null;

        // try to load from maven properties first
        try {
            Properties p = new Properties();
            InputStream is = Maven.class.getResourceAsStream("/META-INF/maven/be.alexandre01.inazuma.uhc/InazumaUHC/pom.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", "");
            }
        } catch (Exception e) {
            // ignore
        }

        // fallback to using Java API
        if (version == null) {
            Package aPackage = Maven.class.getPackage();
            if (aPackage != null) {
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    version = aPackage.getSpecificationVersion();
                }
            }
        }

        if (version == null) {
            version = "unknown-version";
        }

        return version;
    }
}
