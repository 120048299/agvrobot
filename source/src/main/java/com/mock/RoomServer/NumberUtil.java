package com.mock.RoomServer;

public class NumberUtil {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 把byte转为字符串的bit
     */
    public static int[] byteToBitArray(byte b) {
        int [] bits=new int[8];
        bits[0]=(byte) ((b >> 7) & 0x1);
        bits[1]=(byte) ((b >> 6) & 0x1);
        bits[2]=(byte) ((b >> 5) & 0x1);
        bits[3]=(byte) ((b >> 4) & 0x1);
        bits[4]=(byte) ((b >> 3) & 0x1);
        bits[5]=(byte) ((b >> 2) & 0x1);
        bits[6]=(byte) ((b >> 1) & 0x1);
        bits[7]=(byte) ((b >> 0) & 0x1);
        return bits;
    }

    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();//将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    //byte数组转成long
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 方法二：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }

        return new String(buf);
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexStringToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public static long hexStringToLong(String str) {
        byte[] bytes = hexStringToBytes(str);
        return byteToLong(bytes);

    }

    public static void putShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    /**
     * unsigned 16 bit  ===> int value
     * @param b
     * @param index
     * @return
     */
    public static int getShort(byte[] b, int index) {

        return (int) (   ((b[index + 1] << 8) & 0xFF00) + (b[index +0] & 0xFF));
       // return (short) (((b[index + 0] << 8) | b[index + 1] & 0xff));
    }

}
