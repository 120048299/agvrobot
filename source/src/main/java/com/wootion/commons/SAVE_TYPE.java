package com.wootion.commons;

public enum SAVE_TYPE {
    VideoPhoto(1),FlirAndVideoPhoto(2),VideoAndAudio(3);
    private final int value;

    SAVE_TYPE(int i) {
        value = i;
    }
    public final int getValue() {
        return value;
    }

    public static SAVE_TYPE fromInt(int i) {
        for (SAVE_TYPE b : SAVE_TYPE.values()) {
            if (b.getValue() == i) {
                return b;
            }
        }
        return null;
    }

    public String toStrValue() {
        switch (value) {
            case 1:
                return "可见光图片";
            case 2:
                return "红外+可见光";
            case 3:
                return "音视频";
        }

        return "未知";
    }
}
