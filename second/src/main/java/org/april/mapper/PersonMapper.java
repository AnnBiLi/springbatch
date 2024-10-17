package org.april.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.april.pojo.Person;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {
}
