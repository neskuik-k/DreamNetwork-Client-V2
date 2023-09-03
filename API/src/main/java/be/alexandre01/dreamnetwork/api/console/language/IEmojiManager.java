package be.alexandre01.dreamnetwork.api.console.language;

import lombok.SneakyThrows;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 02/09/2023 at 20:16
*/
public interface IEmojiManager {
    @SneakyThrows
    void load();

    String getEmoji(String key, String ifNot, String... ifYes);

    String getEmoji(String key, String ifNot);

    String getEmoji(String key);
}
