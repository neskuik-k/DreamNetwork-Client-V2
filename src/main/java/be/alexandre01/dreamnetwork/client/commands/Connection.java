package be.alexandre01.dreamnetwork.client.commands;

public class Connection implements CommandsExecutor{

    @Override
    public boolean onCommand(String[] args) {
        if(args[0].equalsIgnoreCase("connection")){
            if(args.length == 1){
                //new Connect("localhost",9999,"Console","password");
            }else {
                if (args.length >= 2){
                  //  new Connect(args[1],9999,"Console","password");
                }
            }



            return true;
        }
        return false;
    }
}
