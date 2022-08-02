package server;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class CaptchaLoader {

    private static CaptchaLoader loader = null;
    private HashMap<Integer, File> captchaMap;
    File directory = new File("resources/captchas");
    File[] captchaFiles = directory.listFiles();

    private final Random generator;

    private CaptchaLoader(int width, int height){
        captchaMap = new HashMap<>();
        generator = new Random();
        if (captchaFiles == null)
            return;
        for (File child : captchaFiles) {
            buildCaptchaFile(child.getPath(), width, height);
        }
    }

    public static CaptchaLoader getInstance(){
        if (loader == null)
            loader = new CaptchaLoader(100, 50);
        return loader;
    }

    public void buildCaptchaFile(String path, int width, int height){
        Image pic = (new ImageIcon(path)).getImage();
        ImageIcon icon = new ImageIcon(pic.getScaledInstance(width, height, Image.SCALE_DEFAULT));
        String beginPad = directory.getPath() + "/captcha";
        String endPad = ".jpg";
        String num = path;
        num = num.replaceFirst(beginPad, "");
        num = num.replaceFirst(endPad, "");
        captchaMap.put(Integer.parseInt(num), new File(path));
        //captchaMap.put(Integer.parseInt(num), icon);
    }

    public int getRandomCaptcha(int CaptchaChosen){
        Object[] keyList = captchaMap.keySet().toArray();
        int rando = 0;
        do {
            rando = generator.nextInt(keyList.length);
        } while (Integer.parseInt(keyList[rando].toString()) == CaptchaChosen);
        return Integer.parseInt(keyList[rando].toString());
    }

    public File getCaptcha(int x){
        return captchaMap.get(x);
    }
}
