package org.april.reader;

import org.april.pojo.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;


public class MyItemReader implements ItemReader<Person> {


    private Iterator<Person> iterator;

    public MyItemReader(List<Person> data) {
        this.iterator = data.iterator();
    }

    @Override
    public Person read() {
        return iterator.hasNext() ? iterator.next() : null;
    }





}