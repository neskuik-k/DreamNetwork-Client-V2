package be.alexandre01.dreamnetwork.core.console.accessibility;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.console.Console;

public class AcceptOrRefuse implements CoreAccessibilityMenu.ValueInput{
    AcceptOrRefuseListener listener;
    String noArg;
    String yesArg;
    CoreAccessibilityMenu menu;
    @Deprecated

    public AcceptOrRefuse(String yes, String no, AcceptOrRefuseListener listener){
        this.listener = listener;
        this.noArg = no;
        this.yesArg = yes;
    }
    public AcceptOrRefuse(CoreAccessibilityMenu menu, String yes, String no, AcceptOrRefuseListener listener){
        this.menu = menu;
        this.listener = listener;
        this.noArg = no;
        this.yesArg = yes;
    }

    public AcceptOrRefuse(CoreAccessibilityMenu menu, AcceptOrRefuseListener listener){
        this.menu = menu;
        this.listener = listener;
        this.noArg = Console.getFromLang("menu.no");
        this.yesArg = Console.getFromLang("menu.yes");
    }
    @Override
    public void onTransition(CoreAccessibilityMenu.ShowInfos infos) {
        if(menu != null){
            menu.setArgumentsBuilder(NodeBuilder.create(yesArg,noArg));
        }
        infos.onEnter(null);
        infos.writing(Console.getFromLang("menu.ask.acceptOrRefuse",yesArg,noArg));
        listener.transition(infos);
    }



    @Override
    public CoreAccessibilityMenu.Operation received(CoreAccessibilityMenu.PromptText prompt, String[] args, CoreAccessibilityMenu.ShowInfos infos) {
        String value = prompt.getValue();
        if(value.equalsIgnoreCase(yesArg)){
            return listener.accept(value, args, infos);
        }else if(value.equalsIgnoreCase(noArg)){
            return listener.refuse(value, args, infos);
        }
        return CoreAccessibilityMenu.Operation.set(CoreAccessibilityMenu.Operation.OperationType.RETRY);
    }

    public interface AcceptOrRefuseListener{

        public void transition(CoreAccessibilityMenu.ShowInfos infos);
        public CoreAccessibilityMenu.Operation accept(String value, String[] args, CoreAccessibilityMenu.ShowInfos infos);

        public CoreAccessibilityMenu.Operation refuse(String value, String[] args, CoreAccessibilityMenu.ShowInfos infos);


    }



}
