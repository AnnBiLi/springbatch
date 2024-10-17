package org.april.config;

import jakarta.annotation.Resource;
import org.april.handler.BatchStepExceptionHandler;
import org.april.listen.BatchJobListener;
import org.april.pojo.Person;
import org.april.processor.BatchItemProcessor;
import org.april.reader.MyItemReader;
import org.april.reader.file.PersonFieldSetMapper;
import org.april.service.IPersonService;
import org.april.writer.BatchItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 *  spring3 不需要 @EnableBatchProcessing 了。
 * 如果添加它，Spring Batch的自动配置（元数据表创建，启动时启动作业等）将退出。Spring3的迁移指南中提到了这一点。
 *
 * 要么继续使用 @EnableBatchProcessing  从 modular = true 中受益，但在这种情况下，你必须手动做Spring 3以前做的事情（即初始化数据库，在启动时运行作业等）。
  * 或删除 @EnableBatchProcessing （像以前一样从靴子3功能中受益），但在这种情况下，您需要手动模块化上下文
 */

@Configuration
public class BatchJob implements StepExecutionListener{
    private static final Logger logger = LoggerFactory.getLogger(BatchJob.class);


    @Autowired
    public PlatformTransactionManager platformTransactionManager;

    @Autowired
    public BatchStepExceptionHandler exceptionHandler;

//    @Autowired
//    public BatchItemWriter batchitemwriter;

    @Autowired
    public BatchItemProcessor batchitemprocessor;

    @Resource
    public IPersonService personService;

    private Map<String, JobParameter<?>> jobParams;

    /**
     * 构建job
     * @param listener
     * @return
     */
    @Bean("messagebatchinsertjob")
    public Job MessageBatchInsertJob(BatchJobListener listener,JobRepository jobRepository ) {
        JobBuilder jobBuilder = new JobBuilder("MessageBatchInsertJob", jobRepository);
        return jobBuilder.listener(listener)
                .flow(MessageBatchInsertStep(jobRepository)).end().build();
    }

    /**
     * 1、Skip:如果处理过程中某条记录是错误的,如CSV文件中格式不正确的行,那么可以直接跳过该对象,继续处理下一个。
     * 2、在chunk元素上定义skip-limit属性,告诉Spring最多允许跳过多少个items,超过则job失败
     * 3、Restart:如果将job状态存储在数据库中,而一旦它执行失败,	那么就可以选择重启job实例,	并继续上次的执行位置。
     * 4、最后,对于执行失败的job作业,我们可以重新启动,并让他们从上次断开的地方继续执行。要达到这一点,只需要使用和上次 一模一样的参数来启动job,
     * 则Spring	Batch会自动从数据库中找到这个实例然后继续执行。你也可以拒绝重启,或者参数控 制某个
     * job中的一个tep可以重启的次数(一般来说多次重试都失败了,那我们可能需要放弃。)
     *
     * @return
     */
    @Bean
    public Step MessageBatchInsertStep(JobRepository jobRepository ) {

        logger.info("MessageBatchInsertStep");
        StepBuilder stepBuilder = new StepBuilder("MessageBatchInsertStep",jobRepository);
        return stepBuilder.listener(this).<Person, Person>chunk(5, platformTransactionManager)
//                .reader(reader())//从数据库读取
                .reader(fileRead())
                .processor(batchitemprocessor)
                .writer(writer())
                .faultTolerant()
                .skip(Exception.class).skipLimit(5)
                .taskExecutor(new SimpleAsyncTaskExecutor()).startLimit(5).allowStartIfComplete(true)
                .exceptionHandler(exceptionHandler).build(); // 设置并发方式执行exceptionHandler,异常时打印日志并抛出异常

    }



    /**
     * 从文件读取
     * @return
     */
    public FlatFileItemReader<Person> fileRead() {
        System.out.println("fileRead()方法开始");
        FlatFileItemReader<Person> fileRead = new FlatFileItemReader<>();
        fileRead.setEncoding("UTF-8");
        fileRead.setResource(new FileSystemResource(new File("D:\\2\\user.txt")));
        fileRead.setLinesToSkip(1);//跳过开头多少行，一般开头行是字段的名，可以跳过，便于数据封装

        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<Person>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(","));
        lineMapper.setFieldSetMapper(new FieldSetMapper<Person>() {

            @Override
            public Person mapFieldSet(FieldSet fieldSet) {
                Person user = new Person();
                try {
                    user.setId(fieldSet.readLong(0));
                    user.setFirstName(fieldSet.readString(1));
                    user.setLastName(fieldSet.readString(2));
                 } catch (Exception e) {
                    logger.error("解析异常："+e.getMessage());
                }
                return user;
            }
        });
        fileRead.setLineMapper(lineMapper);
        System.out.println("fileRead()方法结束");
        return fileRead;
    }

    @Bean
    public FlatFileItemReader<Person> fileReader() {
        FlatFileItemReader<Person> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setResource(new FileSystemResource(new File("D:\\2\\user.txt")));
        fileItemReader.setLinesToSkip(1); //跳过开头多少行，一般开头行是字段的名，可以跳过，便于数据封装
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        //DelimitedLineTokenizer defaults to comma as its delimiter
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer()); //默认将逗号作为其分隔符
        lineMapper.setFieldSetMapper(new PersonFieldSetMapper());
        fileItemReader.setLineMapper(lineMapper);
        fileItemReader.open(new ExecutionContext());
        return fileItemReader;
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        jobParams = stepExecution.getJobParameters().getParameters();
        System.out.println("读取到的参数是:" + jobParams);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("afterStep执行了");
        return null;
    }


    /**
     * 从数据库读取
     * @return
     */
    @Bean
    public ItemReader<Person> reader() {
        logger.info("reader开始执行......");
        List<Person> list = personService.list();
        return new MyItemReader(list);
    }


    @Bean
    public ItemWriter<Person> writer(){
        return new BatchItemWriter();
    }






}
