package com.wootion.dao.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageRowBounds;
import com.wootion.dao.IDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Luolin
 * @Description:
 * @Date: Created in 2018/2/13
 * @Modified By:
 */
@Repository
@Primary
public class BaseDaoImpl implements IDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

   /* @Override
    public int insert(String statement, Object object){
        return sqlSessionTemplate.insert(statement, object);
    }

    @Override
    public int update(String statement, Object object){
        return sqlSessionTemplate.update(statement, object);
    }

    @Override
    public int delete(String statement, Object object){
        return sqlSessionTemplate.delete(statement, object);
    }
*/
    @Override
    public Object selectOne(String statement, Object object){
        return sqlSessionTemplate.selectOne(statement, object);
    }

    @Override
    public List<?> selectList(String statement, Object object,int pageNum,int pageSize){
        return sqlSessionTemplate.selectList(statement, object,new PageRowBounds(pageNum,pageSize));
    }

    @Override
    public PageInfo<?> selectPageInfo(String statement, Object object, int pageNum, int pageSize){
        List<?> list = sqlSessionTemplate.selectList(statement, object,new PageRowBounds(pageNum,pageSize));
        return new PageInfo<>(list);
    }
    @Override
    public List<?> selectAll(String statement) {
        return sqlSessionTemplate.selectList(statement);
    }

    @Override
    public List<?> selectList(String statement, Object object){
        return sqlSessionTemplate.selectList(statement, object);
    }

   /* public int insert(Object obj) {
        String className = obj.getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1, className.length());
        return insert(className+".insert", obj);
    }

    public int update(Object obj) {
        String className = obj.getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1, className.length());
        return update(className+".updateByPrimaryKey", obj);
    }

    public int updateSelectively(Object obj) {
        String className = obj.getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1, className.length());
        return update(className+".updateByPrimaryKeySelective", obj);
    }

    public int delete(Object obj) {
        String className = obj.getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1, className.length());
        return delete(className+".deleteByPrimaryKey", obj);
    }*/

    public Object selectOne(Class entityClass, String id){
        String className=entityClass.getName();
        className = className.substring(className.lastIndexOf('.') + 1, className.length());
        String statement = className + ".selectByPrimaryKey";
        return sqlSessionTemplate.selectOne(statement, id);
    }


}
