package graphics;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class CaptchaLoader {

    private static CaptchaLoader loader = null;
    private HashMap<Integer, ImageIcon> captchaMap;
    File directory = new File("resources/captchas");
    File[] captchaFiles = directory.listFiles();

    private final Random generator;
    private int CaptchaChosen;

    private CaptchaLoader(int width, int height){
        captchaMap = new HashMap<>();
        generator = new Random();
        CaptchaChosen = 0;
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
        captchaMap.put(Integer.parseInt(num), icon);
    }

    public ImageIcon getRandomCaptcha(){
        Object[] keyList = captchaMap.keySet().toArray();
        int rando = 0;
        do {
            rando = generator.nextInt(keyList.length);
        } while (Integer.parseInt(keyList[rando].toString()) == CaptchaChosen);
        CaptchaChosen = Integer.parseInt(keyList[rando].toString());
        return captchaMap.get(CaptchaChosen);
    }

    public boolean checkCaptcha(String text){
        return (text.equals(Integer.toString(CaptchaChosen)));
    }

    public int getCaptchaChosen() {
        return CaptchaChosen;
    }
}
