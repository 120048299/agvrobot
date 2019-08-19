package com.wootion.commons;

public enum  DEV_STATUS {
        IDLE(0), WARNING(1), TASKING(2), OFFLINE(3);

        private final int value;
    DEV_STATUS(int i) {
            value = i;
        }

        public final int getValue() {
            return value;
        }

        public static DEV_STATUS fromInt(int i) {
            for (DEV_STATUS b : DEV_STATUS.values()) {
                if (b.getValue() == i) { return b; }
            }
            return null;
        }

        public String toStrValue() {
            switch (value) {
                case 0:
                    return "空闲";
                case 1:
                    return "故障";
                case 2:
                    return "执行";
                case 3:
                    return "离线";
            }
            return "";
        }


}
