package com.wootion.dao;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author: Luolin
 * @Description:
 * @Date: Created in 2018/2/13
 * @Modified By:
 */
public interface IDao {

/*
    int insert(String statement, Object object);

    int update(String statement, Object object);

    int delete(String statement, Object object);
*/

    Object selectOne(String statement, Object object);

    List<?> selectList(String statement, Object object, int pageNum, int pageSize);

    List<?> selectAll(String statement);

    PageInfo<?> selectPageInfo(String statement, Object object, int pageNum, int pageSize);

    List<?> selectList(String statement, Object object);

  /*  int delete(Object obj);

    int insert(Object obj);

    int update(Object obj);

    int updateSelectively(Object obj);
*/
    Object selectOne(Class classEntity, String id);

}
