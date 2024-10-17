package org.april.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.DefaultExceptionHandler;
import org.springframework.stereotype.Component;

/**
 * 异常处理类
 */
@Component
public class BatchStepExceptionHandler extends DefaultExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BatchStepExceptionHandler.class);

    @Override
    public void handleException(RepeatContext context, Throwable throwable) throws Throwable {
        logger.error("Step运行时异常："+throwable.getMessage());
        throw new JobInterruptedException("Step运行时异常："+throwable.getMessage());
    }
}
