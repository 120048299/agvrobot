package com.wootion.bo;

import com.wootion.model.Resource;
import com.wootion.model.Role;

import java.util.List;

public class RoleResource extends Role {
    private List<Resource> resources;

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
