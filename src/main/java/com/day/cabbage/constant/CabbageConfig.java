package com.day.cabbage.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cabbage")
public class CabbageConfig {
    /***
     * 用来下载beatmap的osu账号
     */
    public static String Account;
    /***
     * 用来下载beatmap的osu账号密码
     * ！！！注意防止泄露！！！
     */
    public static String AccountPwd;
    /***
     * osu API key
     */
    public static String OsuApiKey;
    /***
     * 默认用户组名称
     */
    public static String DEFAULT_ROLE;

    /***
     * PP字体文件路径
     */
    public static String PPFontPath;
    /***
     * 用于websocket的端口
     */
    public static int ServerPort;

    public String getPPFontPath() {
        return PPFontPath;
    }

    public void setPPFontPath(String PPFontPath) {
        CabbageConfig.PPFontPath = PPFontPath;
    }

    public int getServerPort() {
        return ServerPort;
    }

    public void setServerPort(int serverPort) {
        ServerPort = serverPort;
    }

    public String getPpFontPath() {
        return PPFontPath;
    }

    public void setPpFontPath(String PPFontPath) {
        CabbageConfig.PPFontPath = PPFontPath;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getAccountPwd() {
        return AccountPwd;
    }

    public void setAccountPwd(String accountPwd) {
        AccountPwd = accountPwd;
    }

    public String getOsuApiKey() {
        return OsuApiKey;
    }

    public void setOsuApiKey(String osuApiKey) {
        OsuApiKey = osuApiKey;
    }

    public String getDefaultRole() {
        return DEFAULT_ROLE;
    }

    public void setDefaultRole(String defaultRole) {
        DEFAULT_ROLE = defaultRole;
    }
}
