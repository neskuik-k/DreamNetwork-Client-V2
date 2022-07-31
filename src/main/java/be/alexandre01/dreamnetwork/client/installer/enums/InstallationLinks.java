package be.alexandre01.dreamnetwork.client.installer.enums;

import java.util.ArrayList;
import java.util.HashMap;

public enum InstallationLinks {

    PAPER_SPIGOT_1_8_8("https://papermc.io/api/v2/projects/paper/versions/1.8.8/builds/445/downloads/paper-1.8.8-445.jar","1.8.8",8),
    PAPER_SPIGOT_1_9_4("https://papermc.io/api/v2/projects/paper/versions/1.9.4/builds/775/downloads/paper-1.9.4-775.jar","1.9.4",8),
    PAPER_SPIGOT_1_10_2("https://papermc.io/api/v2/projects/paper/versions/1.10.2/builds/918/downloads/paper-1.10.2-918.jar","1.10.2",to(8,17)),
    PAPER_SPIGOT_1_11_2("https://papermc.io/api/v2/projects/paper/versions/1.11.2/builds/1106/downloads/paper-1.11.2-1106.jar","1.11.2",to(8,17)),
    PAPER_SPIGOT_1_12_2("https://papermc.io/api/v2/projects/paper/versions/1.12.2/builds/1620/downloads/paper-1.12.2-1620.jar","1.12.2",to(8,17)),
    PAPER_SPIGOT_1_13_2("https://papermc.io/api/v2/projects/paper/versions/1.13.2/builds/657/downloads/paper-1.13.2-657.jar","1.13.2",to(8,17)),
    PAPER_SPIGOT_1_14_4("https://papermc.io/api/v2/projects/paper/versions/1.14.4/builds/245/downloads/paper-1.14.4-245.jar","1.14.2",to(8,17)),
    PAPER_SPIGOT_1_15_2("https://papermc.io/api/v2/projects/paper/versions/1.15.2/builds/393/downloads/paper-1.15.2-393.jar","1.15.2",to(8,17)),
    PAPER_SPIGOT_1_16_5("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/794/downloads/paper-1.16.5-794.jar","1.16.5",to(8,17)),
    PAPER_SPIGOT_1_17_1("https://papermc.io/api/v2/projects/paper/versions/1.17.1/builds/397/downloads/paper-1.17.1-397.jar","1.17.1",to(16,17)),
    PAPER_SPIGOT_1_18_2("https://api.papermc.io/v2/projects/paper/versions/1.18.2/builds/387/downloads/paper-1.18.2-387.jar","1.18.2",to(17,17)),
    PAPER_SPIGOT_1_19("https://api.papermc.io/v2/projects/paper/versions/1.19/builds/65/downloads/paper-1.19-65.jar","1.19",to(17,18)),

    BUNGEECORD("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar","BUNGEECORD",to(8,17)),
    WATERFALL("https://api.papermc.io/v2/projects/waterfall/versions/1.19/builds/498/downloads/waterfall-1.19-498.jar","WATERFALL",to(8,17)),
    FLAMECORD("https://www.dropbox.com/s/3hknjx4k0ezto1y/FlameCord.jar?dl=1","FLAMECORD",to(8,17));

    private String url;
    private String ver;
    private Integer[] javaVersion;
    private static HashMap<String,InstallationLinks> links = new HashMap<>();
    static {
        for (final InstallationLinks i : InstallationLinks.values()) {
            links.put(i.ver, i);
        }
    }

    InstallationLinks(String url,String v,Integer... javaVersion){
        this.url = url;
        this.ver = v;
        this.javaVersion = javaVersion;
    }

    public String getUrl() {
        return url;
    }
    public String getVer() {
        return ver;
    }

    public Integer[] getJavaVersion() {
        return javaVersion;
    }

    public static InstallationLinks getInstallationLinks(String ver){
        return links.get(ver);
    }

    static Integer[] to(Integer i1,Integer i2){
        ArrayList<Integer> is = new ArrayList<>();
        for (int i = i1; i < i2; i++) {
            is.add(i);
        }
        return is.toArray(new Integer[0]);
    }
}
