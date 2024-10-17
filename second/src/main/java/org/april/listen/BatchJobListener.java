package org.april.listen;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class BatchJobListener  implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BatchJobListener.class);

    public void afterJob(JobExecution jobExecution) {
        log.info("任务处理结束");
    }

    public void beforeJob(JobExecution jobExecution) {
        log.info("任务处理开始");
    }


}
