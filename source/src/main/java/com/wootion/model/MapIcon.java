package com.wootion.model;

public class MapIcon {
    private String uid;

    private String iconName;

    private Integer iconType;

    private Float lon;

    private Float lat;

    private Float iconHeight;

    private Float iconWidth;

    private String iconUrl;

    private String siteId;

    private Integer zoomLevel;

    private String devId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? null : uid.trim();
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName == null ? null : iconName.trim();
    }

    public Integer getIconType() {
        return iconType;
    }

    public void setIconType(Integer iconType) {
        this.iconType = iconType;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getIconHeight() {
        return iconHeight;
    }

    public void setIconHeight(Float iconHeight) {
        this.iconHeight = iconHeight;
    }

    public Float getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(Float iconWidth) {
        this.iconWidth = iconWidth;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl == null ? null : iconUrl.trim();
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId == null ? null : siteId.trim();
    }

    public Integer getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(Integer zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId == null ? null : devId.trim();
    }
}