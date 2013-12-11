package cis573.carecoor.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import cis573.carecoor.bean.Schedule;
import cis573.carecoor.bean.Schedule.Time;
import cis573.carecoor.bean.TakeRecord;
import cis573.carecoor.utils.Utils;

public class ScheduleCenter {

	public static final String TAG = "ScheduleCenter";
	
	public static final int SCHEDULE_HAS_TODAY = 0;
	public static final int SCHEDULE_NO_TODAY = -1;
	public static final int SCHEDULE_ENDED = -2;
	
	public static List<TakeRecord> getDayTakeRecordsForScheduleToday(Context context, Schedule schedule) {
		return getDayTakeRecordsForSchedule(context,schedule, new Date());
	}
	
	public static List<TakeRecord> getDayTakeRecordsForSchedule(Context context, Schedule schedule, Date date) {
		if(schedule == null || date == null) {
			return null;
		}
		List<TakeRecord> list = new ArrayList<TakeRecord>();
		List<TakeRecord> records = DataCenter.getTakeRecords(context);
		if(records != null) {
			for(TakeRecord record : records) {
				int compare = Utils.compareDays(record.getTakeTime(), date);
				/*
				if(compare < 0) {
					break;					
				}*/
				if(record.belongsTo(schedule) && compare == 0) {
					list.add(record);
				}
			}
		}
		return list;
	}
	
	public static int getScheduleStatusSimple(Context context, Schedule schedule) {
		int duration = schedule.getDuration();
		List<Integer> days = schedule.getDays();
		Calendar now = Calendar.getInstance(Locale.US);
		if(duration > 0) {	// Has duration
			Date end = getEndOfDaysAfter(schedule.getCreateDate(), duration);
			if(end.before(now.getTime())) {
				return SCHEDULE_ENDED;
			}
		}
		if(days != null) {	// Not every day
			if(!days.contains(now.get(Calendar.DAY_OF_WEEK))) {
				return SCHEDULE_NO_TODAY;
			}
		}
		return SCHEDULE_HAS_TODAY;
	}
	
	public static int getScheduleStatus(Context context, Schedule schedule) {
		int result = getScheduleStatusSimple(context, schedule);
		if(result >= 0) {	// Check how many times taken today
			List<TakeRecord> records = getDayTakeRecordsForSchedule(context, schedule, new Date());
			result = records != null ? records.size() : 0;
		}
		return result;
	}
	
	public static Date getNextUntakeScheduledTime(Context context, Schedule schedule) {
		int duration = schedule.getDuration();
		List<Integer> days = schedule.getDays();
		Calendar time = Calendar.getInstance(Locale.US);
		Date end = null;
		if(duration > 0) {	// Has duration
			end = getEndOfDaysAfter(schedule.getCreateDate(), duration);
		}
		while(true) {
			if(end != null && end.before(time.getTime())) {	// Has ended
				return null;
			}
			if(days == null || days.contains(time.get(Calendar.DAY_OF_WEEK))) {
				List<Time> times = schedule.getTimes();
				for(Time plan : times) {
					Date next = Utils.getAdjustedDate(time.getTime(), plan);
					if(!next.before(time.getTime())) {
						// Check take records
						List<TakeRecord> records = getDayTakeRecordsForSchedule(context,
								schedule, time.getTime());
						if(records != null) {
							boolean find = false;
							for(TakeRecord record : records) {
								if(plan.equals(record.getPlannedTime())) {	// Has taken, check next schedule
									find = true;
									break;
								}
							}
							if(find) {
								continue;
							}
						}
						return next;
					}
				}
			}
			// No schedule today: move the time to the beginning of the next day
			time.add(Calendar.DATE, 1);
			time.set(Calendar.HOUR_OF_DAY, 0);
			time.set(Calendar.MINUTE, 0);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);
		}
	}
	
	public static Date getEndOfDaysAfter(Date time, int after) {
		Calendar end = Calendar.getInstance(Locale.US);
		end.setTime(time);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		end.add(Calendar.DATE, after);
		end.add(Calendar.SECOND, -1);	// Ended at 23:59:59 the previous day
		return end.getTime();
	}
	
	public static boolean hasTrackingSchedule(Context context) {
		List<Schedule> schedules = DataCenter.getSchedules(context);
		if(schedules == null) {
			return false;
		}
		for(Schedule schedule : schedules) {
			if(schedule.isTracking()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get overall conformity of all tracking schedules
	 * @param context
	 * @return
	 */
	public static Map<Date, Conformity> getOverallConformity(Context context) {
		List<Schedule> schedules = DataCenter.getSchedules(context);
		if(schedules == null) {
			return null;
		}
		Map<Date, Conformity> map = new TreeMap<Date, Conformity>();
		Date startDate = null;	// Get the earliest create date among all schedules
		for(Schedule schedule : schedules) {
			if(!schedule.isTracking()) {
				continue;
			}
			if(startDate == null || schedule.getCreateDate().before(startDate)) {
				startDate = schedule.getCreateDate();
			}
		}
		if(startDate == null) {
			return null;
		}
		Calendar now = Calendar.getInstance(Locale.US);
		setBeginningOfDay(now);
		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.setTime(startDate);
		setBeginningOfDay(calendar);
		while(!calendar.after(now)) {
			int taken = 0;
			int planned = 0;
			for(Schedule schedule : schedules) {
				if(!schedule.isTracking()) {	// Not tracking
					continue;
				}
				int plannedThis = getPlannedCount(schedule, calendar);
				if(plannedThis != 0) {
					planned += plannedThis;
					List<TakeRecord> records = getDayTakeRecordsForSchedule(context, schedule, calendar.getTime());
					if(records != null) {
						taken += records.size();
					}
				}
			}
			if(planned != 0) {
				map.put(calendar.getTime(), new Conformity(taken, planned));
			}
			calendar.add(Calendar.DATE, 1);
		}
		return map;
	}
	
	/**
	 * Get how many times that is planned to take medicine for the schedule at a date
	 * @param schedule
	 * @param calendar
	 * @return
	 */
	private static int getPlannedCount(Schedule schedule, Calendar calendar) {
		if(getEndOfDaysAfter(calendar.getTime(),1).before(schedule.getCreateDate())) {
		//if(schedule.getCreateDate().before(calendar.getTime())) {	// Not started
			return 0;
		}
		if(schedule.getDuration() > 0) {	// Has duration
			Date end = getEndOfDaysAfter(schedule.getCreateDate(), schedule.getDuration());
			if(end != null && end.before(calendar.getTime())) {	// Has ended
				return 0;
			}
		}
		List<Integer> days = schedule.getDays();
		if(days != null) {	// Has days
			if(!days.contains(calendar.get(Calendar.DAY_OF_WEEK))) {	// No schedule that day
				return 0;
			}
		}
		return schedule.getTimes().size();
	}
	
	private static void setBeginningOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	public static class Conformity {
		public final int taken;
		public final int planned;
		
		public Conformity(int taken, int planned) {
			this.taken = taken;
			this.planned = planned;
		}
		
		public double getConformity() {
			if(planned == 0) {
				return 0;
			}
			return (double) taken / (double) planned;
		}
	}
}
