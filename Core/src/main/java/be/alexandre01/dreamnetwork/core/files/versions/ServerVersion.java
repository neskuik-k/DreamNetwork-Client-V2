package be.alexandre01.dreamnetwork.core.files.versions;

import be.alexandre01.dreamnetwork.api.service.IContainer;
import lombok.Builder;
import lombok.Getter;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 27/02/2024 at 16:59
*/

@Getter @Builder
public class ServerVersion {
    final Infos[] infos;
    private final String version;


    public ServerVersion(Infos[] infos, String version) {
        this.infos = infos;
        this.version = version;
    }


    @Getter
    public static class Infos {
        private final Provider provider;
        private final String url;
        private final String java;
        private final IContainer.JVMType type;

        public Infos(String provider, String url, String java, IContainer.JVMType type) {
            this.provider = Provider.getProvider(provider).orElse(new Provider(provider, null));
            this.url = url;
            this.java = java;
            this.type = type;
        }
    }


    @Getter
    static class Provider {
        static HashMap<String, Provider> providers = new HashMap<>();


        static {
            new Provider("PaperMC",URI.create("https://papermc.io/assets/logo/256x.png"));
            new Provider("Spigot",URI.create("https://static.spigotmc.org/img/spigot-og.png"));
            new Provider("Purpur",URI.create("https://purpurmc.org/docs/images/purpur-small.png"));
            new Provider("BungeeCord",URI.create("https://i.imgur.com/JuikbVt.png"));
            new Provider("Waterfall",URI.create("https://static-00.iconduck.com/assets.00/droplet-emoji-1289x2048-ejncffnq.png"));
        }
        private final String name;
        private final URI logo;

        public Provider(String name, URI logo) {
            this.name = name;
            this.logo = logo;
            providers.put(name, this);
        }


        static Optional<Provider> getProvider(String name){
            return Optional.ofNullable(providers.get(name));
        }
    }
}
