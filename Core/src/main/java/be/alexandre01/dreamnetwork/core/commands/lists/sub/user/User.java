package be.alexandre01.dreamnetwork.core.commands.lists.sub.user;

public class User {
    private String name;
    private String passwordHash;
    private String serverAccess;

    public User(String name, String passwordHash, String serverAccess) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.serverAccess = serverAccess;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getServerAccess() {
        return serverAccess;
    }

    public void setServerAccess(String serverAccess) {
        this.serverAccess = serverAccess;
    }
}