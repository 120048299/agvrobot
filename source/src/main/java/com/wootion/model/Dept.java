package com.wootion.model;
import lombok.Data;

@Data
public class Dept {
    private String uid;

    private String name;

    private String code;

    private String parentId;

    private Integer level;
    public int compareTo(Dept other){
        if(level < other.level){
            return -1;
        }else if(level > other.level) {
            return 1;
        }else{
            return 0;
        }
    }
}