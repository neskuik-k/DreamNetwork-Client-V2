package be.alexandre01.dreamnetwork.client.commands;



import be.alexandre01.dreamnetwork.client.console.Console;
import com.github.tomaslanger.chalk.Chalk;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public class Commands {

    public ArrayList<CommandsExecutor> executorList;
   public Commands(){
       this.executorList = new ArrayList<>();

   }

   public void addCommands(CommandsExecutor executor){
    this.executorList.add(executor);
   }

   public void check(String[] args){
        boolean hasFound = false;
       for(CommandsExecutor executors : executorList){
               if(executors.onCommand(args)){
                   hasFound = true;
               }


       }
       if(!hasFound){
           Console.print(Chalk.on("Commande introuvable.  | Tapez 'help' pour avoir de l'aide.").underline().red(), Level.WARNING);
           PrintWriter writer = null;
       }

   }
    public static void main(String args[]) throws InterruptedException {

        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        Thread.sleep(500);
        System.out.println("WOW");
        System.out.println("You entered string "+s);
        int a = in.nextInt();
        System.out.println("You entered integer "+a);
        float b = in.nextFloat();
        System.out.println("You entered float "+b);
    }

}
