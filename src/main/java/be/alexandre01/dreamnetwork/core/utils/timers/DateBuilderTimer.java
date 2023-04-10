package be.alexandre01.dreamnetwork.core.utils.timers;

import be.alexandre01.dreamnetwork.core.console.Console;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateBuilderTimer {
    private long time;
    private Date date;
    private String build;
    private String longBuild;
    private int multiplier = 1;
    Format h;
    Format m;
    Format s;

    public DateBuilderTimer(){
        this(0,true);
    }
    public DateBuilderTimer(long time){
        this(time,false);
    }
    public DateBuilderTimer(long time,boolean reversed){

        this.time = time+new Date().getTime();
         h = new SimpleDateFormat("hh");
         m = new SimpleDateFormat("mm");
         s = new SimpleDateFormat("ss");

         if(reversed){
             setReversed(true);
         }
         loadDate();
    }

    public void setReversed(boolean b){
        if(b){
            multiplier = multiplier*-1;
        }
    }

    private long now(){
        long now = new Date().getTime();
        return (time-now)*multiplier;
    }
    public DateBuilderTimer loadDate(){
            Date date = new Date(now());
            int hour =  (int) ((date.getTime() / (1000*60*60)) % 24);
            String minute = m.format(date);
            String second = s.format(date);
            StringBuilder sb = new StringBuilder();
            if(hour > 0){
                sb.append("0"+hour+":");

            }
            sb.append(minute+":");
            sb.append(second);
            this.date = date;
            this.build = sb.toString();

            return this;
        }
    public DateBuilderTimer loadComplexDate(){
        long now = new Date().getTime();

        Date date = new Date(now());
        int hour =  (int) ((date.getTime() / (1000*60*60)) % 24);
        String minute = m.format(date);
        String second = s.format(date);
        StringBuilder lsb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        if(hour > 0){
            sb.append("0"+hour+":");
            lsb.append(Console.getFromLang("core.utils.timers.hour", hour));
            lsb.append(" et ");
        }
        if(Integer.parseInt(minute) != 0){
            lsb.append(Console.getFromLang("core.utils.timers.minute", minute));
            lsb.append(" et ");
        }

            lsb.append(Console.getFromLang("core.utils.timers.second", second));
            sb.append(second);
        this.date = date;
        this.build = sb.toString();
        this.longBuild = lsb.toString();

        return this;
    }
    public Date getDate() {
        return date;
    }

    public String getBuild() {
        return build;
    }

    public String getLongBuild() {
        return longBuild;
    }
}

