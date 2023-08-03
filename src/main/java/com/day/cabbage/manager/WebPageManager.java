package com.day.cabbage.manager;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.day.cabbage.constant.CabbageConfig;
import com.day.cabbage.constant.pattern.RegularPattern;
import com.day.cabbage.mapper.ResMapper;
import com.day.cabbage.pojo.osu.Beatmap;
import com.day.cabbage.pojo.osu.OsuFile;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The type Web page manager.
 */
@Component

public class WebPageManager {

    private static final String AVA_URL = "https://a.ppy.sh/";
    private static final String USERPAGE_URL = "https://osu.ppy.sh/u/";
    private static final String OSU_FILE_URL = "https://osu.ppy.sh/osu/";
    private static final String PP_PLUS_URL = "http://syrin.me/pp+/u/";//site almost shutdown
    private static final OkHttpClient client = new OkHttpClient();
    private final Log logger = LogFactory.get(WebPageManager.class);
    @Autowired
    private ResMapper resDAO;
    private HashMap<Integer, Document> map = new HashMap<>();


    /**
     * Gets avatar.
     *
     * @param uid the uid
     * @return the avatar
     */
    public BufferedImage getAvatar(int uid) {
        URL avaurl;
        BufferedImage ava;
        BufferedImage resizedAva;
        logger.info("开始获取玩家" + uid + "的头像");
        try {
            avaurl = new URL(AVA_URL + uid + "?" + System.currentTimeMillis() / 1000 + ".png");
            ava = ImageIO.read(avaurl);

            ImageInputStream iis = ImageIO.createImageInputStream(avaurl.openConnection().getInputStream());
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            String format = readers.next().getFormatName();
            if ("gif".equals(format)) {
                BufferedImage img = new BufferedImage(ava.getWidth(), ava.getHeight(), BufferedImage.TYPE_INT_ARGB);
                img.createGraphics().drawImage(ava, 0, 0, null);
                ava = img;
            }


            if (ava != null) {
                //进行缩放
                if (ava.getHeight() > 128 || ava.getWidth() > 128) {
                    //获取原图比例，将较大的值除以128，然后把较小的值去除以这个f
                    int resizedHeight;
                    int resizedWidth;
                    if (ava.getHeight() > ava.getWidth()) {
                        float f = (float) ava.getHeight() / 128;
                        resizedHeight = 128;
                        resizedWidth = (int) (ava.getWidth() / f);
                    } else {
                        float f = (float) ava.getWidth() / 128;
                        resizedHeight = (int) (ava.getHeight() / f);
                        resizedWidth = 128;
                    }
                    resizedAva = new BufferedImage(resizedWidth, resizedHeight, ava.getType());
                    Graphics2D g = (Graphics2D) resizedAva.getGraphics();
                    g.drawImage(ava.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH), 0, 0, resizedWidth, resizedHeight, null);
                    g.dispose();
                    ava.flush();
                } else {
                    //如果不需要缩小，直接把引用转过来
                    resizedAva = ava;
                }
                return resizedAva;
            } else {
                return null;
            }

        } catch (IOException e) {
            //存在没设置头像的情况 不做提醒
            return null;
        }

    }


    /**
     * Gets bg backup.
     *
     * @param beatmap the beatmap
     * @return the bg backup
     */
    public BufferedImage getBGBackup(Beatmap beatmap) {

        try {


            OsuFile osuFile = parseOsuFile(beatmap);
            if (osuFile == null) {
                //TODO 解析失败
                //cqManager.warn("解析谱面" + beatmap.getBeatmapId() + "的.osu文件中BG名失败。");
                return null;
            }
            byte[] img = (byte[]) resDAO.getBGBySidAndName(beatmap.getBeatmapSetId(), osuFile.getBgName());
            if (img != null) {
                try (ByteArrayInputStream in = new ByteArrayInputStream(img)) {
                    //正常从数据库获取背景
                    return ImageIO.read(in);
                } catch (IOException e) {
                    //TODO 数据库中的背景损坏
                    // cqManager.warn("数据库中" + beatmap.getBeatmapId() + "的背景损坏。");
                }
            }
            //从API当中获取背景
            Request request = new Request.Builder()
                    .url("https://osu.ppy.sh/home")
                    .build();
            String token = null;
            String session = null;
            try (Response response = client.newCall(request).execute()) {
                if (!Objects.equals(response.code(), HttpStatus.SC_OK)) {
                    //TODO access official site faile
                    //cqManager.warn("下载谱面" + beatmap.getBeatmapId() + "时访问官网失败");
                    return null;
                }
                for (String s : response.headers().toMultimap().get("Set-Cookie")) {
                    if (s.startsWith("XSRF-TOKEN")) {
                        token = s.substring(s.indexOf("=") + 1, s.indexOf(";"));
                    }
                    if (s.startsWith("osu_session")) {
                        session = s.substring(s.indexOf("=") + 1, s.indexOf(";"));
                        break;
                    }
                }
            }

            FormBody.Builder builder = new FormBody.Builder();

            builder.add("_token", token);
            builder.add("username", CabbageConfig.Account);
            builder.add("password", CabbageConfig.AccountPwd);

            RequestBody body = builder.build();
            request = new Request.Builder()
                    .header("referer", "https://osu.ppy.sh/home")
                    .header("cookie", "XSRF-TOKEN=" + token + "; osu_session=" + session)
                    .url("https://osu.ppy.sh/session").post(body).build();

            try (Response response = client.newCall(request).execute()) {
                if (!Objects.equals(response.code(), HttpStatus.SC_OK)) {
                    //TODO download beatmap fail
                    //cqManager.warn("下载谱面" + beatmap.getBeatmapId() + "时登陆失败，返回code：" + response.code());
                    return null;
                }
                for (String s : response.headers().toMultimap().get("Set-Cookie")) {
                    if (s.startsWith("osu_session")) {
                        session = s.substring(s.indexOf("=") + 1, s.indexOf(";"));
                        break;
                    }
                }
            }

            //正式下载map
            request = new Request.Builder()
                    .header("referer", "https://osu.ppy.sh/beatmapsets/" + beatmap.getBeatmapSetId())
                    .header("cookie", "osu_session=" + session)
                    .url("https://osu.ppy.sh/beatmapsets/" + beatmap.getBeatmapSetId() + "/download").build();

            try (Response response = client.newCall(request).execute();
                 ZipInputStream zis = new ZipInputStream(new CheckedInputStream(response.body().byteStream(), new CRC32()))) {

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    logger.info("当前文件名为：" + entry.getName());
//                    byte[] data = new byte[(int) entry.getSize()];
//                    int start = 0, end = 0, flag = 0;
//                    while (entry.getSize() - start > 0) {
//                        end = zis.read(data, start, (int) entry.getSize() - start);
//                        if (end <= 0) {
//                            logger.info("正在读取" + 100 + "%");
//                            break;
//                        }
//                        start += end;
//                        //每20%输出一次，如果为100则为1%
//                        if ((start - flag) > (int) entry.getSize() / 5) {
//                            flag = start;
//                            logger.info("正在读取" + (float) start / entry.getSize() * 100 + "%");
//                        }
//
//                    }
                    // 使用缓冲区读取数据
                    int buffer_size = 4096;
                    if ((int) entry.getSize() > buffer_size)
                        buffer_size = (int) entry.getSize();
                    byte[] buffer = new byte[buffer_size];
                    int read;
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    byte[] data = outputStream.toByteArray();
                    outputStream.close();
                    String filename = entry.getName();
                    if (filename.contains("/")) {
                        filename = filename.substring(filename.indexOf("/") + 1);
                    }
                    if (osuFile.getBgName().equals(filename)) {
                        ByteArrayInputStream in = new ByteArrayInputStream(data);
                        BufferedImage bg = ImageIO.read(in);
                        //懒得重构成方法了_(:з」∠)_
                        //我错了 我不偷懒了_(:з」∠)_
                        BufferedImage resizedBG = resizeImg(bg, 1366, 768);
                        //获取bp原分辨率，将宽拉到1366，然后算出高，减去768除以二然后上下各减掉这部分
                        //在谱面rank状态是Ranked或者Approved时，写入硬盘
                        if (beatmap.getApproved() == 1 || beatmap.getApproved() == 2) {
                            //扩展名直接从文件里取
                            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                                ImageIO.write(resizedBG, osuFile.getBgName().substring(osuFile.getBgName().lastIndexOf(".") + 1), out);
                                resizedBG.flush();
                                byte[] imgBytes = out.toByteArray();
                                resDAO.addBG(beatmap.getBeatmapSetId(), osuFile.getBgName(), imgBytes);
                            } catch (IOException e) {
                                //TODO unzip beatmap file fail
                                //cqManager.warn("解析谱面" + beatmap.getBeatmapId() + "的ZIP流时出现异常，", e);
                                return null;
                            }
                        }
                        in.close();
                        return resizedBG;
                    }

                }

            } catch (Exception e) {
                //TODO get beatmap file fail
                //cqManager.warn("获取谱面" + beatmap.getBeatmapId() + "的ZIP流时出现异常，", e);
                return null;
            }
        } catch (Exception e) {
            //TODO get BG from official site fail
            //cqManager.warn("从官网获取谱面" + beatmap.getBeatmapId() + "的背景时出现异常");
            return null;
        }
        return null;

    }


    /**
     * Gets rank.
     *
     * @param rScore the r score
     * @param start  the start
     * @param end    the end
     * @return the rank
     */
    public int getRank(long rScore, int start, int end) {
        long endValue = getScore(end);
        if (rScore < endValue || endValue == 0) {
            map.clear();
            return 0;
        }
        if (rScore == endValue) {
            map.clear();
            return end;
        }
        //第一次写二分法……不过大部分时间都花在算准确页数，和拿页面元素上了
        while (start <= end) {
            int middle = (start + end) / 2;
            long middleValue = getScore(middle);

            if (middleValue == 0) {
                map.clear();
                return 0;
            }
            if (rScore == middleValue) {
                // 等于中值直接返回
                //清空掉缓存
                map.clear();
                return middle;
            } else if (rScore > middleValue) {
                //rank和分数成反比，所以大于反而rank要在前半部分找
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        map.clear();
        return 0;
    }

    /**
     * Gets last active.
     *
     * @param uid the uid
     * @return the last active
     */
    public Date getLastActive(int uid) {
        int retry = 0;
        Document doc = null;
        while (retry < 5) {
            try {
                logger.info("正在获取" + uid + "的上次活跃时间");
                doc = Jsoup.connect(USERPAGE_URL + uid).timeout((int) Math.pow(2, retry + 1) * 1000).get();
                break;
            } catch (IOException e) {
                logger.error("出现IO异常：" + e.getMessage() + "，正在重试第" + (retry + 1) + "次");
                retry++;
            }
        }
        if (retry == 5) {
            logger.error("玩家" + uid + "请求API获取数据，失败五次");
            return null;
        }
        Elements link = doc.select("time[class*=timeago]");
        if (link.size() == 0) {
            return null;
        }
        String a = link.get(1).text();
        a = a.substring(0, 19);
        try {
            //转换为北京时间
            return new Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a).getTime() + 8 * 3600 * 1000);
        } catch (ParseException e) {
            logger.error("将时间转换为Date对象出错");
        }
        return null;
    }

    /**
     * Gets osu file.
     *
     * @param beatmap the beatmap
     * @return the osu file
     */
    public String getOsuFile(Beatmap beatmap) {
        HttpURLConnection httpConnection;
        String osuFile = resDAO.getOsuFileBybid(beatmap.getBeatmapId());
        if (osuFile != null) {
            return osuFile;
        }
        int retry = 0;
        //获取.osu的逻辑和获取BG不一样，Qua的图BG不缓存，而.osu必须缓存
        //即使是qua的图，也必须有sid的文件夹


        while (retry < 5) {
            try {
                httpConnection =
                        (HttpURLConnection) new URL(OSU_FILE_URL + beatmap.getBeatmapId()).openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setConnectTimeout((int) Math.pow(2, retry + 1) * 1000);
                httpConnection.setReadTimeout((int) Math.pow(2, retry + 1) * 1000);
                if (httpConnection.getResponseCode() != 200) {
                    logger.error("HTTP GET请求失败: " + httpConnection.getResponseCode() + "，正在重试第" + (retry + 1) + "次");
                    retry++;
                    continue;
                }
                //将返回结果读取为Byte数组
                osuFile = new String(readInputStream(httpConnection.getInputStream()), StandardCharsets.UTF_8);
                if (beatmap.getApproved() == 1 || beatmap.getApproved() == 2) {
                    resDAO.addOsuFile(beatmap.getBeatmapId(), osuFile);
                }
                //手动关闭连接
                httpConnection.disconnect();
                return osuFile;
            } catch (IOException e) {
                logger.error("出现IO异常：" + e.getMessage() + "，正在重试第" + (retry + 1) + "次");
                retry++;
            }

        }
        if (retry == 5) {
            logger.error("获取" + beatmap.getBeatmapId() + "的.osu文件，失败五次");
        }
        return null;
    }

    /**
     * 让图片肯定不会变形，但是会切掉东西的拉伸
     *
     * @param bg     the bg
     * @param weight the weight
     * @param height the height
     * @return the buffered image
     */
    public BufferedImage resizeImg(BufferedImage bg, Integer weight, Integer height) {

        BufferedImage resizedBG;
        //获取bp原分辨率，将宽拉到1366，然后算出高，减去768除以二然后上下各减掉这部分
        int resizedWeight = weight;
        int resizedHeight = (int) Math.ceil((float) bg.getHeight() / bg.getWidth() * weight);
        int heightDiff = ((resizedHeight - height) / 2);
        int widthDiff = 0;
        //如果算出重画之后的高<768(遇到金盏花这种特别宽的)
        if (resizedHeight < height) {
            resizedWeight = (int) Math.ceil((float) bg.getWidth() / bg.getHeight() * height);
            resizedHeight = height;
            heightDiff = 0;
            widthDiff = ((resizedWeight - weight) / 2);
        }
        //把BG横向拉到1366;
        //忘记在这里处理了
        BufferedImage resizedBGTmp = new BufferedImage(resizedWeight, resizedHeight, bg.getType());
        Graphics2D g = resizedBGTmp.createGraphics();
        g.drawImage(bg.getScaledInstance(resizedWeight, resizedHeight, Image.SCALE_SMOOTH), 0, 0, resizedWeight, resizedHeight, null);
        g.dispose();

        //切割图片
        resizedBG = new BufferedImage(weight, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < weight; x++) {
            //这里之前用了原bg拉伸之前的分辨率，难怪报错
            for (int y = 0; y < height; y++) {
                resizedBG.setRGB(x, y, resizedBGTmp.getRGB(x + widthDiff, y + heightDiff));
            }
        }
        //刷新掉bg以及临时bg的缓冲，将其作废
        bg.flush();
        resizedBGTmp.flush();
        return resizedBG;
    }

    private long getScore(int rank) {
        Document doc = null;
        int retry = 0;
        logger.info("正在抓取#" + rank + "的玩家的分数");
        //一定要把除出来的值强转
        //math.round好像不太对，应该是ceil
        int p = (int) Math.ceil((float) rank / 50);
        //获取当前rank在当前页的第几个
        int num = (rank - 1) % 50;
        //避免在同一页内的连续查询，将上次查询的doc和p缓存起来
        if (map.get(p) == null) {
            while (retry < 5) {
                try {
                    doc = Jsoup.connect("https://osu.ppy.sh/rankings/osu/score?page=" + p).timeout((int) Math.pow(2, retry + 1) * 1000).get();
                    break;
                } catch (IOException e) {
                    logger.error("出现IO异常：" + e.getMessage() + "，正在重试第" + (retry + 1) + "次");
                    retry++;
                }

            }
            if (retry == 5) {
                logger.error("查询分数失败五次");
                return 0;
            }
            map.put(p, doc);
        } else {
            doc = map.get(p);
        }
        String score = doc.select("td[class*=focused]").get(num).child(0).attr("title");
        return Long.parseLong(score.replace(",", ""));

    }

    /**
     * Prase osu file osu file.
     * 这个方法只能处理ranked/approved/qualified的.osu文件,在目前的业务逻辑里默认.osu文件是存在的。
     * 方法名大包大揽，其实我只能处理出BG名字（
     *
     * @param beatmap the beatmap
     * @return the osu file
     */

    private OsuFile parseOsuFile(Beatmap beatmap) {
        //先获取
        //2017-12-30 18:53:37改为从网页获取（不是所有的osu文件都缓存了
        String osuFile = getOsuFile(beatmap);
        String bgName;
        Matcher m = RegularPattern.BGNAME_REGEX.matcher(osuFile);
        if (m.find()) {
            OsuFile result = new OsuFile();
            bgName = m.group(1);
            result.setBgName(bgName);
            return result;
        } else {
            return null;
        }
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
