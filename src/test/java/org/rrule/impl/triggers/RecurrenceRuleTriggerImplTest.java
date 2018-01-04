package org.rrule.impl.triggers;

import static org.junit.Assert.assertEquals;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.rrule.RecurrenceRuleScheduleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biweekly.util.DayOfWeek;

public class RecurrenceRuleTriggerImplTest {

	private Logger log = LoggerFactory.getLogger(RecurrenceRuleTriggerImplTest.class);
	private boolean done = false;

	@Test
	public void testComputeFirstFireTime() throws Exception {


		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		String jobName = "Jobname";
		JobDataMap newJobDataMap = new JobDataMap();
		newJobDataMap.put("me", this);
		JobDetail job = newJob(HelloJob.class).withIdentity(jobName, "group1").usingJobData(newJobDataMap).build();

		String name = "Trigger";
		Date start = new Date();
		Trigger trigger = newTrigger().withIdentity(name).startNow()
				.withSchedule(RecurrenceRuleScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(start.getHours(),
						(start.getMinutes() + 1) % 60, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
						DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
				.build();

		sched.scheduleJob(job, trigger);
		sched.start();

		log.info(job.getKey() + " will run at: " + trigger.getNextFireTime());
		assertEquals((start.getMinutes() + 1) % 60, trigger.getNextFireTime().getMinutes());
		TriggerKey triggerKey = new TriggerKey(name);
		assertEquals(jobName, sched.getTrigger(triggerKey).getJobKey().getName());

		try {
			while (!done) {
				Thread.sleep(1000L);
			}
		} catch (Exception e) {
			//
		}

		assertEquals(trigger.getFireTimeAfter(new Date()).getDay(),(start.getDay()+1)%7);
		log.info("Test complete");
		sched.shutdown(true);

	}
	@Test
	public void buildAndScheduleFromRRule() throws Exception {
		done = false;
		
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		String jobName = "Jobname";
		JobDataMap newJobDataMap = new JobDataMap();
		newJobDataMap.put("me", this);
		JobDetail job = newJob(HelloJob.class).withIdentity(jobName, "group1").usingJobData(newJobDataMap).build();
		
		String name = "Trigger";
		Date start = new Date();
		String recurrenceRuleExpression = "FREQ=MINUTELY;INTERVAL=2;BYHOUR=9,10,11,12,13,14,15,16,17,18,19";
		Trigger trigger = newTrigger().withIdentity(name).startNow()
				.withSchedule(RecurrenceRuleScheduleBuilder.recurrenceRuleSchedule(recurrenceRuleExpression))
				.build();
		
		sched.scheduleJob(job, trigger);
		sched.start();
		
		log.info(job.getKey() + " will run at: " + trigger.getNextFireTime());
		assertEquals((start.getMinutes() + 2) % 60, trigger.getFireTimeAfter(new Date()).getMinutes());
		TriggerKey triggerKey = new TriggerKey(name);
		assertEquals(jobName, sched.getTrigger(triggerKey).getJobKey().getName());
		
		try {
			while (!done) {
				Thread.sleep(1000L);
			}
		} catch (Exception e) {
			//
		}
		
		log.info(trigger.getFireTimeAfter(new Date()).toString());
		assertEquals((start.getMinutes() + 2) % 60, trigger.getFireTimeAfter(new Date()).getMinutes());
		log.info("Test complete");
		sched.shutdown(true);
		
	}

	public void done() {
		this.done = true;

	}
}
