package org.april.processor;

import org.april.pojo.Person;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BatchItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) {
        person.setFirstName(person.getFirstName().toUpperCase());
        person.setLastName(person.getLastName().toUpperCase());
        return person;
    }

}
