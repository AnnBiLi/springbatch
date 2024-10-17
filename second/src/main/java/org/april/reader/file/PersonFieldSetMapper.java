package org.april.reader.file;

import org.april.pojo.Person;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class PersonFieldSetMapper implements FieldSetMapper<Person> {
    @Override
    public Person mapFieldSet(FieldSet fieldSet) {
        Person person = new Person();
        person.setId(fieldSet.readLong(0));
        person.setFirstName(fieldSet.readString(1));
        person.setLastName(fieldSet.readString(2));
        return person;
    }
}
