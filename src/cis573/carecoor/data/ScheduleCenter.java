package cis573.carecoor.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.Duration;

import cis573.carecoor.bean.Schedule;
import cis573.carecoor.bean.TakeRecord;
import cis573.carecoor.utils.Utils;
import android.content.Context;

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
				if(record.getSchedule().equals(schedule)
						&& Utils.inSameDay(record.getTakeTime(), date)) {
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
				// Has schedule today: check next schedule today;
				List<Integer> hours = schedule.getHours();
				Calendar next = Calendar.getInstance(Locale.US);
				next.setTime(time.getTime());
				for(int hour : hours) {
					next.set(Calendar.HOUR_OF_DAY, hour);
					next.set(Calendar.MINUTE, 0);
					next.set(Calendar.SECOND, 0);
					next.set(Calendar.MILLISECOND, 0);
					if(time.before(next)) {	// Check take records
						List<TakeRecord> records = getDayTakeRecordsForSchedule(context,
								schedule, time.getTime());
						if(records != null && records.size() > 0) {
							TakeRecord record = records.get(0);
							if(record.getPlanned() == hour) {	// Has taken, check next schedule
								continue;
							}
						}
						return next.getTime();
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
}
