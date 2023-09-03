package be.alexandre01.dreamnetwork.api.console.language;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 20:21
*/
public interface ILanguage {
    String translateTo(String map, Object... variables);

    String getLocalizedName();

    java.util.HashMap<String, String> getMessages();
}
