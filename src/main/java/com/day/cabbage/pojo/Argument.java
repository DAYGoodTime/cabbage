package com.day.cabbage.pojo;

import java.util.List;

public class Argument {
    private String kookId;
    private String osuId;
    private String osuName;
    private Integer mode;
    private List<String> subArgument;

    public String getKookId() {
        return kookId;
    }

    public String getOsuId() {
        return osuId;
    }

    public String getOsuName() {
        return osuName;
    }

    public Integer getMode() {
        return mode;
    }

    public List<String> getSubArgument() {
        return subArgument;
    }

    public Argument setKookId(String kookId) {
        this.kookId = kookId;
        return this;
    }

    public Argument setOsuId(String osuId) {
        this.osuId = osuId;
        return this;
    }

    public Argument setOsuName(String osuName) {
        this.osuName = osuName;
        return this;
    }

    public Argument setMode(Integer mode) {
        this.mode = mode;
        return this;
    }

    public Argument setSubArgument(List<String> subArgument) {
        this.subArgument = subArgument;
        return this;
    }
}
