package be.alexandre01.dreamnetwork.client.installer.enums;

import java.util.HashMap;

public enum InstallationLinks {

    PAPER_SPIGOT_1_8_8("https://papermc.io/api/v2/projects/paper/versions/1.8.8/builds/443/downloads/paper-1.8.8-443.jar","1.8.8"),
    PAPER_SPIGOT_1_9_4("https://papermc.io/api/v2/projects/paper/versions/1.9.4/builds/773/downloads/paper-1.9.4-773.jar","1.9.4"),
    PAPER_SPIGOT_1_10_2("https://papermc.io/api/v2/projects/paper/versions/1.10.2/builds/916/downloads/paper-1.10.2-916.jar","1.10.2"),
    PAPER_SPIGOT_1_11_2("https://papermc.io/api/v2/projects/paper/versions/1.11.2/builds/1104/downloads/paper-1.11.2-1104.jar","1.11.2"),
    PAPER_SPIGOT_1_12_2("https://papermc.io/api/v2/projects/paper/versions/1.12.2/builds/1618/downloads/paper-1.12.2-1618.jar","1.12.2"),
    PAPER_SPIGOT_1_13_2("https://papermc.io/api/v2/projects/paper/versions/1.13.2/builds/655/downloads/paper-1.13.2-655.jar","1.13.2"),
    PAPER_SPIGOT_1_14_2("https://papermc.io/api/v2/projects/paper/versions/1.14.4/builds/243/downloads/paper-1.14.4-243.jar","1.14.2"),
    PAPER_SPIGOT_1_15_2("https://papermc.io/api/v2/projects/paper/versions/1.15.2/builds/391/downloads/paper-1.15.2-391.jar","1.15.2"),
    PAPER_SPIGOT_1_16_5("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/567/downloads/paper-1.16.5-567.jar","1.16.5"),

    BUNGEECORD("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar","BUNGEECORD"),
    WATERFALL("https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/403/downloads/waterfall-1.16-403.jar","WATERFALL");

    private String url;
    private String ver;
    private static HashMap<String,InstallationLinks> links = new HashMap<>();
    static {
        for (final InstallationLinks i : InstallationLinks.values()) {
            links.put(i.ver, i);
        }
    }

    InstallationLinks(String url,String v){
        this.url = url;
        this.ver = v;

    }

    public String getUrl() {
        return url;
    }
    public String getVer() {
        return ver;
    }

    public static InstallationLinks getInstallationLinks(String ver){
        return links.get(ver);
    }
}
