package com.day.cabbage.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.day.cabbage.constant.CabbageConfig;
import com.day.cabbage.services.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/")
public class ApiController {

    private static final Log logger = LogFactory.get(ApiController.class);

    private final BotService botService;

    private static void sendImgToResponse(HttpServletResponse response, String base64, int width, int height) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        response.setContentType("image/png");
        try (InputStream in = new ByteArrayInputStream(bytes);
             OutputStream out = response.getOutputStream()) {
            BufferedImage img = ImageIO.read(in);
            BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            img2.getGraphics().drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(img2, "png", new MemoryCacheImageOutputStream(out));
            out.write(bytes);
        } catch (IOException ignore) {
            logger.warn("发送图片失败");
        }
    }

    @GetMapping("stat/{uid}")
    public void getStat(
            HttpServletResponse response,
            @PathVariable Integer uid,
            @RequestParam(value = "mode", defaultValue = "0", required = false) Integer mode
    ) {
        String result = botService.getStat(uid, mode);
        sendImgToResponse(response, result, 600, 288);
    }

    @GetMapping("recent/{uid}")
    public void getRecent(
            HttpServletResponse response,
            @PathVariable("uid") Integer uid,
            @RequestParam(value = "mode", defaultValue = "0", required = false) Integer mode
    ) {
        String result = botService.getRecent(uid, mode);
        sendImgToResponse(response, result, 1280, 720);
    }

    @GetMapping("bp/{uid}/{num}")
    public void getBP(
            HttpServletResponse response,
            @PathVariable("uid") Integer uid,
            @PathVariable(value = "num", required = false) Integer num,
            @RequestParam(value = "mode", defaultValue = "0", required = false) Integer mode
    ) {
        String result = botService.getBP(uid, num, mode);
        sendImgToResponse(response, result, 1280, 720);
    }

    @GetMapping("debug/bumpConstant")
    public void bumpConstant(){
        Class<CabbageConfig> c = CabbageConfig.class;
        try {
            CabbageConfig config = c.getDeclaredConstructor().newInstance();
            for (Field declaredField : c.getDeclaredFields()) {
                logger.info("[常量]:[{}]:[{}]",declaredField.getName(),declaredField.get(config));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }
}
