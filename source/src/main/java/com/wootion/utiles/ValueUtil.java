package com.wootion.utiles;

import com.wootion.agvrobot.utils.NumberUtil;
import com.wootion.model.RegzObjectField;

/**
 * @Author: majunhui
 * @Date: 2019/3/15 0015
 * @Version 1.0
 */
public class ValueUtil {
    /*
    -- 类型字段 1：整型; 2：浮点型(保留4位小数); 3：枚举(整型) 4-百分比(*100 保留2位小数), 5：循环整型
    -- 备注字段
    --   type=3时，配置枚举，多个枚举用逗号分隔，值与描述用横线分隔，例如：0-干,1-湿，不配默认为0-分,1-合
    --   type=5时，配置范围，最小值和最大值用横线分隔，例如0-9 表示取值在0-9之间循环，当取值大于9又从0开始计数，不配默认为0-9循环
    */
    public static String formatValue(int type, Double value, String memo) {
        if (value==null) {
            return null;
        }
        Double dValue = value;
        String sValue;
        switch (type) {
            case 1:
                dValue += 0.5;
                sValue = "" + dValue.intValue();
                break;
            case 2:
                sValue = String.format("%.4f",dValue);
                break;
            case 3:
                dValue += 0.5;
                sValue = ""+dValue.intValue();
                break;
            case 4:
                sValue = String.format("%.2f",(dValue*100));
                break;
            case 5:
                dValue += 0.5;
                int iValue = dValue.intValue();
                int min = 0;
                int max = 9;
                if (memo!=null && memo.length()>2) {
                    String[] mm = memo.split("-");
                    if (mm.length>1) {
                        min = Integer.valueOf(mm[0]);
                        max = Integer.valueOf(mm[1]);
                    }
                }
                if (iValue>max) {
                    iValue = min;
                }
                if (iValue<min) {
                    iValue = max;
                }
                sValue = "" + iValue;
                break;
            default:
                sValue = String.format("%.4f",dValue);
                break;
        }
        sValue= NumberUtil.rvZeroAndDot(sValue);
        return sValue;
    }

    public static String formatValue(int type, String value, String memo) {
        if (value==null) {
            return null;
        }
        Double dValue = Double.valueOf(value);
        return formatValue(type, dValue, memo);
    }
    public static String formatField(RegzObjectField field, String value) {
        int type = field.getFieldType();
        String memo = field.getMemo();
        String sName = field.getFieldName();
        String sValue = formatValue(type, value, memo);
        String sUnit = field.getFieldUnit();

        if (sValue==null || sValue.isEmpty()) {
            return sName + ":[无]";
        } else {
            if (type==3) {
                sUnit = "";
                if (memo==null || memo.isEmpty()) {
                    // 没有配置，默认(0-分,1-合)
                    sValue = sValue.equals("1")?"合":"分";
                } else {
                    // 根据枚举显示值
                    for (String kvStr: memo.split(",")) {
                        String[] kv = kvStr.split("-");
                        if (kv.length>1) {
                            if (kv[0].equals(sValue)) {
                                sValue = kv[1];
                                break;
                            }
                        }
                    }
                }
            }
            return sName + ":" + sValue + sUnit;
        }
    }
}
