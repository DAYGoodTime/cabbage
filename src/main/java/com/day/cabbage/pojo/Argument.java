package com.day.cabbage.pojo;

import java.util.List;

public class Argument {
    private String platformId;
    private Integer osuId;
    private String osuName;
    private Integer mode;
    private List<String> subArgument;

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Integer getOsuId() {
        return osuId;
    }

    public void setOsuId(Integer osuId) {
        this.osuId = osuId;
    }

    public String getOsuName() {
        return osuName;
    }

    public void setOsuName(String osuName) {
        this.osuName = osuName;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public List<String> getSubArgument() {
        return subArgument;
    }

    public void setSubArgument(List<String> subArgument) {
        this.subArgument = subArgument;
    }
}
