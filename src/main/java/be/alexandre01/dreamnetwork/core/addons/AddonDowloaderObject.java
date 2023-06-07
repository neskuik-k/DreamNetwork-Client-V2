package be.alexandre01.dreamnetwork.core.addons;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddonDowloaderObject {
    private String name;
    private String author;
    private String description;
    private String version;
    private String github;
    private String downloadLink;
    private String hash;
}
