package be.alexandre01.dreamnetwork.core.libraries;

import be.alexandre01.dreamnetwork.core.Core;
import be.alexandre01.dreamnetwork.core.console.language.LanguageManager;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarFile;

public class CustomClassLoader extends URLClassLoader{
        public CustomClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
    public CustomClassLoader(URL[] urls) {
        super(urls, CustomClassLoader.class.getClassLoader());
    }
    public CustomClassLoader(URL url) {
        super(new URL[]{url}, CustomClassLoader.class.getClassLoader());
    }
        public void addURL(URL url) {
            super.addURL(url);
        }
        @Override
        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException e) {
                return Class.forName(name, resolve, Core.class.getClassLoader());
            }
        }
        @Override
        public void close() {
            try {
                System.out.println(LanguageManager.getMessage("libraries.customClass.close"));
                Class clazz = java.net.URLClassLoader.class;
                Field ucp = clazz.getDeclaredField("ucp");
                ucp.setAccessible(true);
                Object sunMiscURLClassPath = ucp.get(this);
                Field loaders = sunMiscURLClassPath.getClass().getDeclaredField("loaders");
                loaders.setAccessible(true);
                Object collection = loaders.get(sunMiscURLClassPath);
                for (Object sunMiscURLClassPathJarLoader : ((Collection) collection).toArray()) {
                    try {
                        Field loader = sunMiscURLClassPathJarLoader.getClass().getDeclaredField("jar");
                        loader.setAccessible(true);
                        Object jarFile = loader.get(sunMiscURLClassPathJarLoader);
                        ((JarFile) jarFile).close();
                    } catch (Throwable t) {
                        // if we got this far, this is probably not a JAR loader so skip it
                    }
                }
            } catch (Throwable t) {
                // probably not a SUN VM
            }
            return;
        }
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            System.out.println(LanguageManager.getMessage("libraries.customClass.finalized").replaceFirst("%var%", this.toString()));
        }

}
