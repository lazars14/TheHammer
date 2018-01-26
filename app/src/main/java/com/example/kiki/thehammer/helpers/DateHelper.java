package com.example.kiki.thehammer.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lazar on 19/1/2018.
 */

public class DateHelper {

    private static final String[] timeNames = new String[]{"days", "hours", "minutes"};
    private static final String whitespace = " ";

    public static String dateToString(Date date){
        return DateFormat.getDateTimeInstance().format(date);
    }

    public static boolean auctionEnded(Date endDate){
        Date now = new Date();
        if(now.after(endDate)){
            return true;
        }

        return false;
    }

    public static String calculateRemainingTime(Date endDate){
        Date now = new Date();
        StringBuilder sb = new StringBuilder();

        long diffInMillisec = endDate.getTime() - now.getTime();

        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillisec);

        if(diffInDays < 0){
            return "Auction Over";
        }

        long hoursMilis = diffInMillisec - TimeUnit.DAYS.toMillis(diffInDays);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(hoursMilis);
        long minsMilis = hoursMilis - TimeUnit.HOURS.toMillis(diffInHours);
        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(minsMilis);

        long[] time = new long[]{diffInDays, diffInHours, diffInMin};

        for(int i = 0; i < time.length; i++){
            if(time[i] != 0) sb.append(time[i] + whitespace + timeNames[i] + whitespace);
        }

        return sb.toString();
    }

}
