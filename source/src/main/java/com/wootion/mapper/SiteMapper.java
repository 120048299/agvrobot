package com.wootion.mapper;

import com.wootion.model.Robot;
import com.wootion.model.Site;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: majunhui
 * @Date: 2018/12/20
 * @Version 1.0
 */

@Component
@Mapper
public interface SiteMapper {
    @Select("SELECT * FROM site")
    List<Site> findAll();

    @Select("SELECT * FROM site WHERE uid=#{uid}")
    Site findByUid(String uid);

    @Select("SELECT name FROM site where uid=#{uid}")
    String getSiteNameById(String uid);


}


