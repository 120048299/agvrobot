package com.wootion.model;

import java.util.Date;
import lombok.Data;
@Data
public class SnappedPicture {
    private String uid;

    private String fileName;

    private String memo;

    private Integer isInfra;

    private String siteId;

    private Date createTime;

    private String picTypeName;

}