package com.wootion.mapper;

import com.wootion.model.Dict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DictMapper {
    @Select("  select * from dict where dict_code=#{dictCode} order by order_number")
    List<Dict> selectDict(@Param("dictCode") String dictCode);
}


