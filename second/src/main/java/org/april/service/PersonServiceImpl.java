package org.april.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.april.mapper.PersonMapper;
import org.april.pojo.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements IPersonService{
}
