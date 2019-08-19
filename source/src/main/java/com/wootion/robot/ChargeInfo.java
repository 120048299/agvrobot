package com.wootion.robot;

import lombok.Data;

@Data
public class ChargeInfo {

    public ChargeInfo(int status) {
        this.status = status;
    }

    int status;//0 无, 1 正在充电流程中;

}
