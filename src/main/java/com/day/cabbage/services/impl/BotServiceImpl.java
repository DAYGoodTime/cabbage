package com.day.cabbage.services.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.day.cabbage.manager.ApiManager;
import com.day.cabbage.manager.WebPageManager;
import com.day.cabbage.mapper.CurrentMapper;
import com.day.cabbage.mapper.UserInfoMapper;
import com.day.cabbage.mapper.UserMapper;
import com.day.cabbage.pojo.osu.Beatmap;
import com.day.cabbage.pojo.osu.OsuUser;
import com.day.cabbage.pojo.osu.Score;
import com.day.cabbage.pojo.osu.Userinfo;
import com.day.cabbage.services.BotService;
import com.day.cabbage.services.UserService;
import com.day.cabbage.util.ImgUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    private static final Log logger = LogFactory.get(BotService.class);
    private final UserService userService;
    private final WebPageManager webPageManager;
    private final ApiManager apiManager;
    private final UserInfoMapper userInfoMapper;
    private final ImgUtil imgUtil;
    private final UserMapper userMapper;
    private final CurrentMapper currentMapper;


    @Override
    public boolean bindUser(Integer mode, String platformId, Integer osuId) {
        Userinfo apiUser = apiManager.getUser(mode, osuId);
        OsuUser user = new OsuUser(
                platformId, osuId, "normal", apiUser.getUserName(), apiUser.getUserName(), false, mode, LocalDate.now()
        );
        Integer currentOsuId = currentMapper.getCurrentOsuId(platformId);
        if (currentOsuId == null) {
            currentMapper.addCurrent(platformId, osuId);
        }
        userInfoMapper.insert(apiUser);
        return userService.registerUser(user);
    }

    @Override
    public String getStat(Integer uid, Integer mode) {
        int day = 1; //compare day
        int scoreRank;
        Userinfo userFromAPI = apiManager.getUser(mode, uid);
        //获取score rank
        scoreRank = webPageManager.getRank(userFromAPI.getRankedScore(), 1, 2000);
        return imgUtil.drawUserInfo(userFromAPI, userFromAPI, "Normal", day, false, scoreRank, mode);
    }

    @Override
    public String getRecent(Integer uid, Integer mode) {
        Userinfo userFromAPI;
        //TODO autodetect mode
        if (mode == null) {
            OsuUser user = userService.getUser(uid);
            if (user == null) mode = 0;
            else {
                mode = user.getMainMode();
            }
        }
        userFromAPI = apiManager.getUser(mode, uid);
        if (userFromAPI == null) {
            //TODO get api null
            return null;
        }
        logger.info("开始查询用户:" + userFromAPI.getUserName() + "最近的游戏记录");
        Score score = apiManager.getRecent(mode, userFromAPI.getUserId());
        if (score == null) {
            //TODO score is null
            return null;
        }
        List<Score> scores = apiManager.getRecents(mode, userFromAPI.getUserId());
        int count = 0;
        for (Score score1 : scores) {
            if (score.getBeatmapId().equals(score1.getBeatmapId())) {
                count++;
            }
        }

        Beatmap beatmap = apiManager.getBeatmap(score.getBeatmapId());
        if (beatmap == null) {
            //TODO beatmap is null
            return null;
        }
        //TODO for only Text argument
        /*
            String resp = scoreUtil.genScoreString(score, beatmap, userFromAPI.getUserName(), count);
         */
        return imgUtil.drawResult(userFromAPI, score, beatmap, mode);
    }

    @Override
    public List<OsuUser> getBindUser(String platformId) {
        QueryWrapper<OsuUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("platform_id", platformId);
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public String getBP(Integer uid, Integer num, Integer mode) {
        //TODO mode selection
        mode = 0;
        //TODO only text
        boolean isOnlyText = false;
        List<Score> todayBP;
        List<Score> BpList;
        Userinfo userFromAPI;
        userFromAPI = apiManager.getUser(mode, uid);
        if (userFromAPI == null) {
            //TODO api user is null
            return null;
        }
        BpList = apiManager.getBP(mode, uid);
        if (BpList.size() == 0) {
            //TODO no bp message
            return null;
        }
        if (num != null) {
            if (num > BpList.size()) {
                //TODO no bp at this number
                return null;
            }
            Score score = BpList.get(num - 1);
            logger.info("获得了玩家" + userFromAPI.getUserName() + "在模式：" + mode + "的第" + num + "个BP：" + score.getBeatmapId() + "，正在获取歌曲名称");
            Beatmap map = apiManager.getBeatmap(score.getBeatmapId());
            return imgUtil.drawResult(userFromAPI, score, map, mode);
        } else {
            //TODO WIP can't use
//            todayBP = new ArrayList<>(BpList.size());
//            for (int i = 0; i < BpList.size(); i++) {
//                //对BP进行遍历，如果产生时间在24小时内，就加入今日bp豪华午餐，并且加上bp所在的编号
//                if (BpList.get(i).getDate().after(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)))) {
//                    BpList.get(i).setBpId(i);
//                    Beatmap map = apiManager.getBeatmap(BpList.get(i).getBeatmapId());
//                    BpList.get(i).setBeatmapName(map.getArtist() + " - " + map.getTitle() + " [" + map.getVersion() + "]");
//                    todayBP.add(BpList.get(i));
//                }
//            }
//            String result = imgUtil.drawUserBP(userFromAPI, todayBP, mode, false);
//            sendImgToResponse(response,result,1280,720);
            return null;
        }
    }
}
