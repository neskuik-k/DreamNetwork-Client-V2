package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_FILE_PATH = "data/user.yml";
    private Map<String, User> users;

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    public void loadUsers() {
        File file = new File(USER_FILE_PATH);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Yaml yaml = new Yaml(new Constructor(Map.class));
                users = yaml.load(fis);
                if (users == null) {
                    users = new HashMap<>();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveUsers() {
        File file = new File(USER_FILE_PATH);
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            yaml.dump(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.put(user.getName(), user);
        saveUsers();
    }

    public void removeUser(String name) {
        users.remove(name);
        saveUsers();
    }

    public User getUser(String name) {
        return users.get(name);
    }
}