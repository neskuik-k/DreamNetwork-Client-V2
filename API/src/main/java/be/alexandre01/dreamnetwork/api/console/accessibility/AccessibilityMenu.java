package be.alexandre01.dreamnetwork.api.console.accessibility;

import be.alexandre01.dreamnetwork.api.DNUtils;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.api.console.Console;
import be.alexandre01.dreamnetwork.api.console.colors.Colors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AccessibilityMenu {
    @Getter protected Console console;
    protected String consoleName;
    protected AccessibilityMenu switchedFrom;

    @Setter protected boolean safeRemove = false;
    protected HashMap<String, PromptText> prompts = new HashMap<>();
    protected TreeMap<Integer, String> pages = new TreeMap<>();
    protected HashMap<String, Operation> values = new HashMap<>();
    protected  HashMap<String, ShowInfos> infos = new HashMap<>();
    protected Multimap<String, NodeContainer> arguments =  ArrayListMultimap.create();

    protected Operation currentOperation;
    protected int currentPage = 0;
    protected String currentInput;

    protected List<FinishCatch> finishCatches = new ArrayList<>();



    protected Operation[] operations = new Operation[0];
    protected ScheduledExecutorService executor;

    public static AccessibilityMenu createObject(){
        return DNUtils.get().createAccessibilityMenu();
    }

    public Operation[] getArray(){
        return operations;
    }
    public AccessibilityMenu(){
        // Ignore
    }

    public abstract void executeFinishCatches();

    public AccessibilityMenu(String consoleName){
        setConsoleName(consoleName);
    }
    public abstract void show(boolean clear);

    public abstract void show();

    public abstract void insertArgumentBuilder(String value, NodeContainer... n);

    public abstract void setArgumentsBuilder(NodeContainer... objects);
    public abstract AccessibilityMenu addValueInput(PromptText prompt, int pos, ValueInput valueInput);



    public abstract void addFinishCatch(FinishCatch finishCatch);


    public abstract Operation retry();
    public abstract Operation errorAndRetry(ShowInfos infos);


    public abstract Operation skip();

    public abstract void redrawScreen();

    public abstract void drawInfos();

    public abstract Operation switchTo(AccessibilityMenu value);
    public abstract Operation finish();

    public abstract void exitConsole();

    public Operation getOperation(){
        return currentOperation;
    }

    public Operation getOperation(String value){
        return values.get(value);
    }

    public void changeTo(Console console){
        Console.setActualConsole(console.name);
    }
    public void changeTo(AccessibilityMenu menu){
        changeTo(menu.console);
    }

    protected void setConsoleName(String consoleName) {
        this.consoleName = consoleName;
    }

    public void buildAndRun(){
        buildAndRun(null);
    }
    public void buildAndRun(String consoleName){
        if(consoleName != null){
            this.consoleName = consoleName;
        }
        //Console.debugPrint("Building console "+this.consoleName);
        console = Console.load(this.consoleName);
        console.setNoHistory(true);
        try {
            if(currentInput == null){
                throw new NullPointerException("No input found");
            }

          //  playCurrentTransition(false);
            console.setConsoleAction(new Console.IConsole() {
                @Override
                public void listener(String[] args) {
                    StringBuilder builder = new StringBuilder();
                    //Console.print("console name "+console.name);
                    for (int i = 0; i < args.length; i++) {
                        builder.append(args[i]);
                        if(i != args.length - 1){
                            builder.append(" ");
                        }

                        PromptText promptText = prompts.get(currentInput);
                      //  Console.print(currentInput);
                        if(promptText == null){
                            Console.fine("Prompt text is null");
                            return;
                        }
                        promptText.value = builder.toString();

                        if(args[0].equalsIgnoreCase(":exit")){
                            forceExit();
                            return;
                        }

                        Operation operation = promptText.input.received(promptText, args, infos.get(currentInput));
                        if(operation != null){
                            injectOperation(operation);
                        }
                    }

                }

                @Override
                public void consoleChange() {

                }
            });

            console.setKillListener(reader -> {
                //Shutdown other things
                console.addOverlay(new Console.Overlay() {
                    @Override
                    public void on(String data) {
                        disable();
                        if(data.equalsIgnoreCase("y") ||data.equalsIgnoreCase("yes")){
                            // quit
                            forceExit();
                        }else {
                            Console.debugPrint(Console.getFromLang("menu.cancel"));
                        }
                    }
                }, Console.getFromLang("menu.cancelWriting"));
                return true;
            });
        }catch (Exception e){
            Console.bug(e);
        }

    }

    public void clearData(){
        pages.clear();
        values.clear();
        infos.clear();
        arguments.clear();
        switchedFrom = null;
        finishCatches.clear();
        currentOperation = null;
        currentPage = 0;
        currentInput = null;
    }

    public abstract void forceExit();
    public abstract void skipToNext();


    public void injectOperation(Operation operation){

        if(Console.isBlockConsole()){
            Console.setBlockConsole(false);
        }

        if(operation.type == Operation.OperationType.ACCEPTED){
            values.put(currentInput,operation);
            skipToNext();
            return;
        }

        if(operation.type == Operation.OperationType.RETRY){
            redrawScreen();
            return;
        }

        if(operation.type == Operation.OperationType.SKIP){
            skipToNext();
            return;
        }

        if(operation.type == Operation.OperationType.ERROR){
            return;
        }

        if(operation.type == Operation.OperationType.WAIT){
            Console.setBlockConsole(true);
            return;
        }

        if(operation.type == Operation.OperationType.SWITCH){
            AccessibilityMenu menu = (AccessibilityMenu) operation.returnValue;
            menu.buildAndRun();
            menu.switchedFrom = this;

            if(safeRemove){
                menu.addFinishCatch(() -> {
                    clearData();
                    exitConsole();
                });
            }
            menu.show();
            return;
        }

        if(operation.type == Operation.OperationType.FINISH){
            executeFinishCatches();
            if(switchedFrom != null)
                switchedFrom.executeFinishCatches();
            return;
        }
    }


    public static interface ValueInput{
        public void onTransition(ShowInfos infos);
        public Operation received(PromptText value,String[] args,ShowInfos infos);

    }

    @Getter
    public static class PromptText{
        @Setter private String value;
        private String macro;
        private NodeContainer[] suggestions;
        @Setter private ValueInput input;

        private PromptText(String value){
            this.value = value;
        }

        public PromptText setMacro(String macro){
            this.macro = macro;
            return this;
        }


        public PromptText setSuggestions(NodeContainer... suggestions){
            this.suggestions = suggestions;
            return this;
        }


        public static PromptText create(String value){
            return new PromptText(value);
        }
    }

    @Getter @AllArgsConstructor
    public static class Operation{
        @Setter OperationType type;
        Object returnValue;
        public static Operation set(OperationType type, Object returnValue){
            return new Operation(type,returnValue);
        }

        public static Operation accepted(Object returnValue){
            return new Operation(OperationType.ACCEPTED,returnValue);
        }
        public static Operation set(OperationType type){
            return new Operation(type,null);
        }
        public <T> T getFrom(Class<T> clazz){
            return (T) returnValue;
        }

        public static enum OperationType{
            ACCEPTED,RETRY,SKIP,ERROR,WAIT,SWITCH,FINISH
        }
    }
    @Getter
    public static class ShowInfos{
        String error = Colors.RED+"There is an error !"+Colors.RESET;
        String headMessage = Console.getFromLang("menu.headMessage");
        String writingMessage = Console.getFromLang("menu.writing");


        public void onEnter(String s){
            headMessage = s;
        }

        public void writing(String s){
            writingMessage = s;
        }

        public void error(String s){
             this.error = s;
        }
    }

    public static interface FinishCatch{
        public void onFinish();
    }

}
