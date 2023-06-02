package be.alexandre01.dreamnetwork.core.console.accessibility;

import be.alexandre01.dreamnetwork.api.commands.sub.NodeBuilder;
import be.alexandre01.dreamnetwork.api.commands.sub.NodeContainer;
import be.alexandre01.dreamnetwork.core.console.Console;
import be.alexandre01.dreamnetwork.core.console.ConsolePath;
import be.alexandre01.dreamnetwork.core.console.ConsoleReader;
import be.alexandre01.dreamnetwork.core.console.colors.Colors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static be.alexandre01.dreamnetwork.core.console.Console.getFromLang;
import static be.alexandre01.dreamnetwork.core.console.Console.print;

public class AccessibilityMenu {
    @Getter protected Console console;
    String consoleName;
    HashMap<String, PromptText> prompts = new HashMap<>();
    HashMap<Integer, String> pages = new HashMap<>();
    HashMap<String, Operation> values = new HashMap<>();
    HashMap<String, ShowInfos> infos = new HashMap<>();
    Multimap<String, NodeContainer> arguments =  ArrayListMultimap.create();

    Operation currentOperation;
    private int currentPage = 0;
    String currentInput;

    List<FinishCatch> finishCatches = new ArrayList<>();

    Operation[] operations = new Operation[0];
    ScheduledExecutorService executor;

    public Operation[] getArray(){
        return operations;
    }
    public AccessibilityMenu(){
        // Ignore
    }

    public AccessibilityMenu(String consoleName){
        setConsoleName(consoleName);
    }
    public void show(){
        currentPage = -1;
        Console.setActualConsole(console.name);
        skipToNext();
    }

    public void insertArgumentBuilder(String value, NodeContainer... n){
        this.arguments.putAll(value, Arrays.asList(n));
    }

    public void setArgumentsBuilder(NodeContainer... objects){
        for (NodeContainer object : objects) {
            new NodeBuilder(object,console);
        }
    }
    public AccessibilityMenu addValueInput(PromptText prompt, ValueInput valueInput){
        infos.put(prompt.getValue(), new ShowInfos());
        prompt.input = valueInput;
        if(currentInput == null){
            currentInput = prompt.getValue();
        }
        pages.put(pages.size(), prompt.getValue());
        prompts.put(prompt.getValue(),prompt);

        return this;
    }

    public void addFinishCatch(FinishCatch finishCatch){
        finishCatches.add(finishCatch);
    }
    public Operation retry(){
        Operation op = wroteCurrent(Operation.OperationType.RETRY);
        redrawScreen();
        return op;
    }
    public Operation errorAndRetry(ShowInfos infos){
        Operation op = wroteCurrent(Operation.OperationType.ERROR);
        redrawScreen();
        return op;
    }

    private Operation wroteCurrent(Operation.OperationType type){
        if(currentOperation != null){
            currentOperation.type = type;
            return currentOperation;
        }
        return currentOperation = Operation.set(type);
    }

    public Operation skip(){
        return wroteCurrent(Operation.OperationType.SKIP);
    }

    public void redrawScreen(){
        Console.clearConsole();
        drawInfos();
    }

    public void drawInfos(){
        ShowInfos info = infos.get(currentInput);
        if(currentOperation != null){
            if(currentOperation.type == Operation.OperationType.RETRY || currentOperation.type == Operation.OperationType.ERROR){
                Console.debugPrint(Colors.RED_UNDERLINED+info.error+Colors.RESET);
            }
        }
        Console.debugPrint(info.headMessage.replace("%data%",currentInput));
        if(info.writingMessage != null){
            console.setWriting(info.writingMessage);
        }


    }

    public Operation switchTo(AccessibilityMenu value){
        return Operation.set(Operation.OperationType.SWITCH,value);
    }
    public Operation finish() {
        return wroteCurrent(Operation.OperationType.FINISH);
    }

    public void exitConsole(){
        Console.setActualConsole(ConsolePath.Main.DEFAULT);
    }

    private void playCurrentTransition(){
        PromptText text = prompts.get(currentInput);
        if(text.macro != null){
            ConsoleReader.sReader.runMacro(text.macro);
        }

        console.completorNodes.clear();
        if(text.suggestions != null){
            insertArgumentBuilder(currentInput, text.suggestions);
        }
        text.input.onTransition(infos.get(currentInput));
        setArgumentsBuilder(arguments.get(currentInput).toArray(new NodeContainer[0]));
        console.reloadCompletors();
    }

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

        console = Console.load(this.consoleName);
        console.setNoHistory(true);
        try {
            if(currentInput == null){
                throw new NullPointerException("No input found");
            }


            playCurrentTransition();
            console.setConsoleAction(new Console.IConsole() {
                @Override
                public void listener(String[] args) {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < args.length; i++) {
                        builder.append(args[i]);
                        if(i != args.length - 1){
                            builder.append(" ");
                        }

                        PromptText promptText = prompts.get(currentInput);
                        promptText.value = builder.toString();

                        Operation operation = promptText.input.received(promptText, args, infos.get(currentInput));

                       injectOperation(operation);
                    }

                }

                @Override
                public void consoleChange() {

                }
            });

            console.setKillListener(reader -> {
                //Shutdown other things
                String data;
                while ((data = reader.readLine(Console.getFromLang("service.creation.cancelCreation"))) != null){
                    if(data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes")){
                        // quit
                        exitConsole();
                    }else {
                        Console.debugPrint(Console.getFromLang("service.creation.cancelCreationCancelled"));
                    }
                    Console.getConsole("m:create").run();
                    break;
                }
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
        finishCatches.clear();
        currentOperation = null;
        currentPage = 0;
        currentInput = null;
    }

    public void forceExit(){
        Console.debugPrint("ForceExit");
        Console.setBlockConsole(false);
        Console.debugPrint("ForceExit");
        exitConsole();
        clearData();
        injectOperation(Operation.set(Operation.OperationType.FINISH));
    }
    public void skipToNext(){
        currentPage++;
        currentInput = pages.get(currentPage);


        playCurrentTransition();
        redrawScreen();
        currentOperation = null;
    }


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
            menu.buildAndRun(console.name);

            menu.addFinishCatch(() -> {
                clearData();
                exitConsole();
            });
            menu.show();
            return;
        }

        if(operation.type == Operation.OperationType.FINISH){
            for (FinishCatch finishCatch : finishCatches) {
                finishCatch.onFinish();
            }
            return;
        }
    }


    public static interface ValueInput{
        public void onTransition(ShowInfos infos);
        public Operation received(PromptText value,String[] args,ShowInfos infos);

    }

    @Getter
    public static class PromptText{
        private String value;
        private String macro;
        private NodeContainer[] suggestions;
        private ValueInput input;

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
        OperationType type;
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
        String headMessage = getFromLang("service.creation.headMessage");
        String writingMessage = getFromLang("service.creation.writing");


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
