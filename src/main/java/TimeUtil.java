package com.driver;

public class TimeUtil {


    public static int convertDeliverTime(String deliveryTime) {
        String[]time=deliveryTime.split(":");
        return Integer.parseInt(time[0])*60+Integer.parseInt(time[1]);
    }
    public static String convertDeliverTime(int deliveryTime) {
        int hh=deliveryTime/60;
        int mm=deliveryTime%60;
        String HH=String.valueOf(hh);
        String MM=String.valueOf(mm);
        return HH+":"+MM;
    }
}