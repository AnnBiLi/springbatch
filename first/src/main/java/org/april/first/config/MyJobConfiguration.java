package org.april.first.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class MyJobConfiguration  extends DefaultBatchConfiguration {

    @Bean
    public Job HelloJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("helloJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
//                .tasklet((contribution, chunkContext) -> null, transactionManager)
                .build();
    }

    @Bean
    public Tasklet tasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                //要执行逻辑--step步骤执行逻辑
                System.out.println("hello spring  batch！");
                return RepeatStatus.FINISHED;  //执行完了
            }
        };
    }


    @Override
    protected Charset getCharset() {
        return StandardCharsets.ISO_8859_1;
    }

}
