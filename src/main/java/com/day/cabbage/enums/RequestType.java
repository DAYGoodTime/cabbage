package com.day.cabbage.enums;

public enum RequestType {
    BP,
    BPME,
    STAT,
    STATME,
    RECENT,
    BIND,
    GETBINDLIST,
    ERROR;

    public static RequestType getIndex(int index){
        if(index>values().length) return ERROR;
        else return values()[index];
    }
}
