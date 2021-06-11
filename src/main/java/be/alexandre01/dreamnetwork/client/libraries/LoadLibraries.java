package be.alexandre01.dreamnetwork.client.libraries;

import be.alexandre01.dreamnetwork.client.Client;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Objects;
import java.util.jar.JarFile;

public class LoadLibraries{
    private File dir;
    public void init(){
        try {


                dir = new File("libs");
                for(File file : Objects.requireNonNull(dir.listFiles())){
                    if(file.isDirectory())
                        continue;
                    CustomClassLoader child = new CustomClassLoader(
                            file.toURI().toURL(),
                            this.getClass().getClassLoader()
                    );
                    URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
                    Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(classLoader, file.toURI().toURL());

                    System.out.println("loaded "+file.getName());
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static class CustomClassLoader extends URLClassLoader {
        public CustomClassLoader(URL url, ClassLoader parent) {
            super(new URL[] { url }, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException e) {
                return Class.forName(name, resolve, Client.class.getClassLoader());
            }
        }
        @Override
        public void close() {
            try {
                System.out.println("Custom close");
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
            System.out.println(this.toString() + " - CL Finalized.");
        }
    }
}
