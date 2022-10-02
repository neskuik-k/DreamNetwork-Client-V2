package be.alexandre01.dreamnetwork.api.commands.sub.types;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeType;
import be.alexandre01.dreamnetwork.api.commands.sub.SubCommandCompletor;

public class TextNode extends NodeType {

    public TextNode(String... text){
        type = SubCommandCompletor.Type.TEXT;
        super.objects = new Object[]{text};
    }
    public String[] getTexts() {
        return (String[]) super.objects;
    }
}
