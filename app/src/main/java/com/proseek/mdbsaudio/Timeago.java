package com.proseek.mdbsaudio;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timeago {
    public static String getTimeago(long duration)
    {
        Date d=new Date();

        long second= TimeUnit.MILLISECONDS.toSeconds(d.getTime()-duration);
        long minutes= TimeUnit.MILLISECONDS.toMinutes(d.getTime()-duration);
        long hours= TimeUnit.MILLISECONDS.toHours(d.getTime()-duration);
        long days =TimeUnit.MILLISECONDS.toDays(d.getTime()-duration);

       if (second<60)
       {
           return "jus now";
       }
       else if(minutes==1)
       {
           return "a minute ago";
       }
       else if(minutes>1 && minutes<60)
       {
           return minutes+"minutes ago";
       }
       else if(hours==1)
       {
           return "an hour ago";
       }
       else if(hours>1 && hours<24)
       {
           return hours+"hours ago";
       }
       else if(days==1)
       {
           return "a day ago";
       }
       else {
           return days+"days ago";
       }
    }
}
