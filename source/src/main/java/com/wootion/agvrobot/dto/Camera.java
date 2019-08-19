package com.wootion.agvrobot.dto;

import com.wootion.commons.DEV_STATUS;
import com.wootion.model.Dev;

public class Camera extends Dev {
    private String CameraIp;
    private String port;
    private String userName;
    private String password;
    private DEV_STATUS cameraStatus;
    public Camera (Dev d){
        super(d);
        cameraStatus = DEV_STATUS.fromInt(d.getStatus());
        String []params = d.getParams().split(",");
        this.CameraIp = params[0];
        this.port = params[1];
        this.userName = params[2];
        this.password = params[3];
    }
    public Dev getDev(){
        Dev dev = new Dev(this);
        return dev;
    }
    @Override
    public String  getParams(){
        return String.format("%s,%s,%s,%s",this.CameraIp, this.port,this.userName,this.password);
    }
    public String getCameraIp() {
        return CameraIp;
    }

    public void setCameraIp(String cameraIp) {
        CameraIp = cameraIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DEV_STATUS getCameraStatus() {
        cameraStatus = DEV_STATUS.fromInt(this.getStatus());
        return cameraStatus;
    }

    public void setCameraStatus(DEV_STATUS cameraStatus) {
        this.cameraStatus = cameraStatus;
        this.setStatus(cameraStatus.getValue());
    }
}
