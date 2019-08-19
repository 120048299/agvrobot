package com.wootion.service;


import com.github.pagehelper.PageInfo;
import com.wootion.model.*;

import java.util.List;
import java.util.Map;

public interface IHistoryVideoService {
    /**
     * 新增任务模板详细信息
     * @param videoName 传入数据
     * @return 返回true or false
     * @Author Luolin
     */
    boolean saveHistoryVideo(String videoName, String realName, int videoType,String siteId);



    int changeRobotConifg(String videoAddr,String thermalAddr,int thermalType);

}
