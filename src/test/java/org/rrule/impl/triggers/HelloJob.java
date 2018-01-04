package org.rrule.impl.triggers;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloJob implements Job {
	Logger log = LoggerFactory.getLogger(HelloJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("running job.....");
		((RecurrenceRuleTriggerImplTest)context.getMergedJobDataMap().get("me")).done();

	}
}
