package com.afinal.youjinchen.rock_paper_scissors;


import java.util.Random;

public class AI extends Player {
    private static final String[] NAMES = {"陳佑津", "袁詩婷", "林軒宇", "不敗之神"};
    private static final Random random = new Random();

    @Override
    public String getName() {
        return NAMES[random.nextInt(NAMES.length)];
    }

    @Override
    public Fist decide() {
        Fist[] fists = Fist.values();
        return fists[random.nextInt(fists.length)];
    }
}
