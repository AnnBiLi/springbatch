package org.april.writer;

import org.april.pojo.Person;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;



@Component
@StepScope
public class BatchItemWriter implements ItemWriter<Person> {


    @Override
    public void write(Chunk<? extends Person> chunk) throws Exception {
        //打印到本地
        for (Person user : chunk) {
            System.out.println(user.getFirstName()+" "+user.getLastName());
        }
    }
}
