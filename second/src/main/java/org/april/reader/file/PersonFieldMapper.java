package org.april.reader.file;

import org.april.pojo.Person;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;


/**
 * 按名称映射字段
 */
public class PersonFieldMapper implements FieldSetMapper<Person> {
    @Override
    public Person mapFieldSet(FieldSet fieldSet) {
        if (fieldSet == null) {
            return null;
        }

        Person person = new Person();
        person.setId(fieldSet.readLong("id"));
        person.setLastName(fieldSet.readString("lastName"));
        person.setFirstName(fieldSet.readString("firstName"));

        return person;
    }
}
