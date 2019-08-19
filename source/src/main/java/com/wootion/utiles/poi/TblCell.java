package com.wootion.utiles.poi;

public class TblCell {
    private int colspan;
    private int rowspan;
    private String itemId;
    private String alias;
    public TblCell(String itemId, String alias) {
        this.itemId = itemId;
        this.alias = alias;
        this.colspan = 1;
        this.rowspan = 1;
    }
    public TblCell(String itemId, String alias, int colspan, int rowspan) {
        this.itemId = itemId;
        this.alias = alias;
        this.colspan = colspan < 1 ? 1 : colspan;
        this.rowspan = rowspan < 1 ? 1 : rowspan;
    }
    public int getColspan() {
        return colspan;
    }
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }
    public int getRowspan() {
        return rowspan;
    }
    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
  /*  @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }*/

    @Override
    public String toString() {
        return "TblCell{" +
                "colspan=" + colspan +
                ", rowspan=" + rowspan +
                ", itemId='" + itemId + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
