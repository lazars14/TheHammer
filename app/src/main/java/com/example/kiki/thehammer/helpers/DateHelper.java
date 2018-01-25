package com.example.kiki.thehammer.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lazar on 19/1/2018.
 */

public class DateHelper {

    public static final SimpleDateFormat format = new SimpleDateFormat("DD/MM/yyyy hh:mm");
    private static final String[] timeNames = new String[]{"days", "hours", "minutes"};
    private static final String whitespace = " ";

    public static Date stringToDate(String date_str){
        Date date = null;
        try {
            date = format.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String dateToString(Date date){
        return format.format(date);
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
