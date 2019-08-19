package com.wootion.model;
import lombok.Data;

@Data
public class Role {
    private String uid;
    private String role;
    private String description;
    private Boolean available;
}