package com.wootion.vo;

import org.apache.poi.ss.formula.functions.T;

import java.util.List;


/**
 * conditionName={'eqlx'} conditionTitle={'设备类型'} isShow={1} hasAll={1} hasMore={1}
 items={types}
 disableItems={[]} initSelectedItems={this.state.eqTypeList} oneRowShowNum={4}/>
 */
public class TaskEditCondition {
    private String conditionName;
    private String conditionTitle;
    private int isShow=1;
    private int hasAll=1;
    private int hasMore=0;

    private List items;
    private String[] initSelectedItems;
    private String[] disableItems;

    public TaskEditCondition(String conditionName, String conditionTitle) {
        this.conditionName = conditionName;
        this.conditionTitle = conditionTitle;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getConditionTitle() {
        return conditionTitle;
    }

    public void setConditionTitle(String conditionTitle) {
        this.conditionTitle = conditionTitle;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public int getHasAll() {
        return hasAll;
    }

    public void setHasAll(int hasAll) {
        this.hasAll = hasAll;
    }

    public int getHasMore() {
        return hasMore;
    }

    public void setHasMore(int hasMore) {
        this.hasMore = hasMore;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }


    public String[] getInitSelectedItems() {
        return initSelectedItems;
    }

    public void setInitSelectedItems(String[] initSelectedItems) {
        this.initSelectedItems = initSelectedItems;
    }

    public String[] getDisableItems() {
        return disableItems;
    }

    public void setDisableItems(String[] disableItems) {
        this.disableItems = disableItems;
    }
}
