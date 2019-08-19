package com.wootion.agvrobot.utils;

public class NumberUtil {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * byte 转为short
     * @return
     */
    public static short getUint8(byte b){
        return (short)(b & 0x00ff);
    }

    /**
     * 无符号16位 转为32位
     * @return
     */
    public static int getUint16(short s){
        return (int)(s & 0x0000ffff);
    }
    /**
     * 无符号32位 转为64位
     * @return
     */
    public static long getUint32(int i){
        return (long)(i & 0x00000000ffffffff);
    }

    /**
     * 把byte转为字符串的bit
     */
    public static int[] byteToBitArray(byte b) {
        int[] bits = new int[8];
        bits[0] = (byte) ((b >> 0) & 0x1);
        bits[1] = (byte) ((b >> 1) & 0x1);
        bits[2] = (byte) ((b >> 2) & 0x1);
        bits[3] = (byte) ((b >> 3) & 0x1);
        bits[4] = (byte) ((b >> 4) & 0x1);
        bits[5] = (byte) ((b >> 5) & 0x1);
        bits[6] = (byte) ((b >> 6) & 0x1);
        bits[7] = (byte) ((b >> 7) & 0x1);
        return bits;
    }

    /**
     * 把int转为字符串的bit
     */
    public static int[] intToBitArray(int b) {
        int[] bits = new int[32];
        for (int i=0;i<bits.length;i++){
            bits[i] = (byte) ((b >> i) & 0x1);
        }
        return bits;
    }

    /**
     * short 转为 按位的int数组
     */
    public static int[] shortToBitArray(int b) {
        int[] bits = new int[16];
        for (int i=0;i<bits.length;i++){
            bits[i] = (byte) ((b >> i) & 0x1);
        }
        return bits;
    }


    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    public static String byteToStr(byte b){
        int[] bits= byteToBitArray(b);
        String s="";
        for (int i=7;i>=0;i--){
            s+=String.valueOf(bits[i]);
        }
        return s;
    }


    /**
     * 二进制字符串转换为int数组,每个字节以","隔开
     **/
    public static int[] binStrToIntArr(String binStr) {
        String[] temp = binStr.split(",");
        int[] b = new int[temp.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp[i], 2).intValue();
        }
        return b;
    }

    /**
     * 二进制字符串转换为byte数组,每个字节以","隔开
     **/
    public static byte[] binStrToByteArr(String binStr) {
        String[] temp = binStr.split(",");
        byte[] b = new byte[temp.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp[i], 2).byteValue();
        }
        return b;
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
        for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
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


    public static void putShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    /**
     * unsigned 16 bit  ===> int value
     *
     * @param b
     * @param index
     * @return
     */
    public static int getShort(byte[] b, int index) {

        return (int) (((b[index + 1] << 8) & 0xFF00) + (b[index + 0] & 0xFF));
        // return (short) (((b[index + 0] << 8) | b[index + 1] & 0xff));
    }


    public static String rvZeroAndDot(String s){
        if (s.isEmpty()) {
            return null;
        }
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static boolean doubleEquals(double num1,double num2){
        double diff=Math.abs(num1-num2);
        if(diff<0.0000001){
            return true;
        }
        return false;
    }

    public static void main(String args[]){
        byte b[]=binStrToByteArr("0000101");
        System.out.println(b[0]);
        String s=byteToStr(b[0]);
        System.out.println(s);
    }

}
