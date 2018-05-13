package io.a_ware.a_ware;

import android.text.TextUtils;


public class TimeModel {
    private String milliSecond;
    private String second;
    private String minute;
    private String hour;
    private String day;

    public long getMilliSecond() throws NumberFormatException {
        return Long.parseLong(milliSecond);
    }

    public long getSecond() throws NumberFormatException{
        return Long.parseLong(second)*1000;
    }

    public long getMinute() throws NumberFormatException{
        return Long.parseLong(minute)*60*1000;
    }


    public long getHour() throws NumberFormatException{
        return Long.parseLong(hour)*60*60*1000;
    }

    public long getDay() throws NumberFormatException{
        return Long.parseLong(day)*24*60*60*1000;
    }

    private TimeModel(TimeModelBuilder builder){
        this.milliSecond = builder.milliSecond;
        this.second = builder.second;
        this.minute = builder.minute;
        this.hour = builder.hour;
        this.day = builder.day;
    }

    public long getTotalTimeInMillisecond(){
        long time = 0;
        if(!TextUtils.isEmpty(milliSecond))
            time = time + getMilliSecond();
        if(!TextUtils.isEmpty(second))
            time = time+getSecond();
        if(!TextUtils.isEmpty(minute))
            time = time + getMinute();
        if(!TextUtils.isEmpty(hour))
            time = time + getHour();
        if(!TextUtils.isEmpty(day))
            time = time + getDay();
        return time;
    }

    public static class TimeModelBuilder{
        private String milliSecond;
        private String second;
        private String minute;
        private String hour;
        private String day;

        public TimeModelBuilder setMilliSecond(String milliSecond) {
            this.milliSecond = milliSecond;
            return this;
        }

        public TimeModelBuilder setSecond(String second) {
            this.second = second;
            return this;
        }

        public TimeModelBuilder setMinute(String minute) {
            this.minute = minute;
            return this;
        }

        public TimeModelBuilder setHour(String hour) {
            this.hour = hour;
            return this;
        }

        public TimeModelBuilder setDay(String day) {
            this.day = day;
            return this;
        }

        public TimeModel build(){
            return new TimeModel(this);
        }


    }
}
