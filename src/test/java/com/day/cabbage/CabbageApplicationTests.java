package com.day.cabbage;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.day.cabbage.mapper.ResMapper;
import com.day.cabbage.pojo.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

@SpringBootTest
class CabbageApplicationTests {


    private static final Log logger = LogFactory.get(CabbageApplicationTests.class);
    static int base;

    static {
        Random random = new Random();
        base = random.nextInt();
    }

    @Autowired
    ResMapper resMapper;

    @Test
    void contextLoads() {
        File img = new File("D:\\DEV\\resource\\layout.png");
        uploadImg();

    }

    @Test
    void searchFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("D:\\DEV\\java\\cabbageWeb\\src\\main\\res\\font\\Gayatri.ttf"));
            ge.registerFont(customFont); // 注册字体Gayatri.ttf
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        String[] fontNames = ge.getAvailableFontFamilyNames();
        System.out.println(Arrays.toString(fontNames));
    }

    @Test
    void test_font() throws IOException {
        Font gayatri = new Font("Gayatri", Font.PLAIN, 48);
        BufferedImage bg = get("ppBanner.png");
        Graphics2D g2 = (Graphics2D) bg.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.decode("#ff66a9"));
        g2.setFont(gayatri);
        g2.drawString("1232332", 10, 30);
        g2.dispose();
        File output = new File("D:\\DEV\\resource\\output\\output.png");
        ImageIO.write(bg, "png", output);
    }

    private void uploadImg() {
        File uploadingDir = new File("D:\\DEV\\resource\\uploading");
        if (!uploadingDir.isDirectory()) {
            return;
        }
        for (File file : uploadingDir.listFiles()) {
            String fileName = file.getName();
            byte[] res = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
                byte[] b = new byte[1000];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                fis.close();
                res = bos.toByteArray();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (res != null) {
                Resource resource = new Resource(++base, fileName, res);
                resMapper.insert(resource);
            }
        }
    }

    public BufferedImage get(String name) {
        byte[] data = (byte[]) resMapper.getImage(name);
        try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            return ImageIO.read(in);
        } catch (IOException e) {
            logger.error("[获取图片失败]:" + name);
            return null;
        }
    }

}
