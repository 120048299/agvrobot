package com.wootion.mapper;

import com.wootion.model.DictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DictTypeMapper {
    @Select("    select * from dict_type order by order_number")
    List<DictType> selectAll();
}


