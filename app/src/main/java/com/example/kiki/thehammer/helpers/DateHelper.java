package com.example.kiki.thehammer.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lazar on 19/1/2018.
 */

public class DateHelper {

    public static final SimpleDateFormat format = new SimpleDateFormat("DD/MM/yyyy hh:mm");

    public static Date stringToDate(String date_str){
        Date date = null;
        try {
            date = format.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
