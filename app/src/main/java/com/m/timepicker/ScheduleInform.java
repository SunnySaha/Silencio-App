package com.m.timepicker;

public class ScheduleInform {

    private int Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, delay, vol;
    String Name, StartTime, EndTime;
    boolean swi;

    public ScheduleInform(int saturday, int sunday, int monday, int tuesday, int wednesday, int thursday, int friday, int delay, String name, String startTime, String endTime, int vol, boolean swi) {
        Saturday = saturday;
        Sunday = sunday;
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
        this.delay = delay;
        Name = name;
        StartTime = startTime;
        EndTime = endTime;
        this.vol = vol;
        this.swi = swi;
    }

    public boolean isSwi() {
        return swi;
    }

    public void setSwi(boolean swi) {
        this.swi = swi;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public void setSaturday(int saturday) {
        Saturday = saturday;
    }

    public void setSunday(int sunday) {
        Sunday = sunday;
    }

    public void setMonday(int monday) {
        Monday = monday;
    }

    public void setTuesday(int tuesday) {
        Tuesday = tuesday;
    }

    public void setWednesday(int wednesday) {
        Wednesday = wednesday;
    }

    public void setThursday(int thursday) {
        Thursday = thursday;
    }

    public void setFriday(int friday) {
        Friday = friday;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getSaturday() {
        return Saturday;
    }

    public int getSunday() {
        return Sunday;
    }

    public int getMonday() {
        return Monday;
    }

    public int getTuesday() {
        return Tuesday;
    }

    public int getWednesday() {
        return Wednesday;
    }

    public int getThursday() {
        return Thursday;
    }

    public int getFriday() {
        return Friday;
    }

    public int getDelay() {
        return delay;
    }

    public String getName() {
        return Name;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }
}
