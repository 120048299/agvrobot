package com.wootion.model;
import lombok.Data;

import java.util.List;
@Data
public class RegzObject {
    private String uid;

    private String name;

    private Integer opsType;

    private Integer meterType;

    private String memo;

    private List<RegzObjectField> fieldList;

}