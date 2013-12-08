package cis573.carecoor.data;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import cis573.carecoor.R;
import cis573.carecoor.bean.Appointment;
import cis573.carecoor.bean.Contact;
import cis573.carecoor.bean.Game;
import cis573.carecoor.bean.Schedule;
import cis573.carecoor.bean.TakeRecord;
import cis573.carecoor.utils.FileKit;
import cis573.carecoor.utils.Logger;
import cis573.carecoor.utils.ResourceKit;

public class DataCenter {

	public static final String TAG = "DataCenter";
	
	private static final String FILENAME_USER_CONTACTS = "user_contacts";
	private static final String FILENAME_USEFUL_CONTACTS = "useful_contacts";
	private static final String FILENAME_DRUG_LISTS="drug_lists";
	private static final String FILENAME_SCHEDULES="schedules";
	private static final String FILENAME_TAKE_RECORD = "take_records";
	private static final String FILENAME_APPOINTMENT = "appointments";
	
	private static List<Contact> mUsefulContacts = null;
	private static List<Contact> mDrugs = null;
	private static List<Contact> mUserContacts = null;
	private static List<Schedule> mSchedules = null;
	private static List<TakeRecord> mTakeRecords = null;
	private static List<Appointment> mAppointments = null;
	private static List<Game> mGames = null;
	
	public static List<Contact> getUsefulContacts(Context context) {
		if(mUsefulContacts == null) {
			mUsefulContacts = (List<Contact>) FileKit.readObject(context, FILENAME_USEFUL_CONTACTS);
		}
		return mUsefulContacts;
	}
	
	public static List<Contact> getDrugLists(Context context) {
		if(mDrugs == null) {
			mDrugs = (List<Contact>) FileKit.readObject(context, FILENAME_DRUG_LISTS);
		}
		return mDrugs;
	}
	
	public static void setUsefulContacts(Context context, List<Contact> contacts) {
		if(contacts != null) {
			mUsefulContacts = contacts;
			FileKit.saveObject(context, FILENAME_USEFUL_CONTACTS, mUsefulContacts);
		}
	}
	
	public static void setDrugLists(Context context, List<Contact> contacts) {
		if(contacts != null) {
			mDrugs = contacts;
			FileKit.saveObject(context, FILENAME_DRUG_LISTS, mDrugs);
		}
	}
	
	public static List<Contact> getUserContacts(Context context) {
		if(mUserContacts == null) {
			mUserContacts = (List<Contact>) FileKit.readObject(context, FILENAME_USER_CONTACTS);
		}
		return mUserContacts;
	}
	
	public static void setUserContacts(Context context, List<Contact> contacts) {
		if(contacts != null) {
			mUserContacts = contacts;
			FileKit.saveObject(context, FILENAME_USER_CONTACTS, mUserContacts);
		}
	}
	
	/****************** Schedules *********************/
	
	public static List<Schedule> getSchedules(Context context) {
		if(mSchedules == null) {
			Calendar cal=Calendar.getInstance();
			cal.set(2013, 11, 3,1,1,0);
			
			Schedule s=new Schedule(cal.getTime());
			s.setMedicine(MedicineCenter.getMedicineList(context).get(0));
			Schedule.Time t=new Schedule.Time(10,45);
			List<Schedule.Time> tt=new ArrayList<Schedule.Time>();
			tt.add(t);
			s.setTimes(tt);
			//Integer[] d={0,1,2,3,4,5,6};
			//List<Integer> days=new ArrayList(Arrays.asList(d));
			s.setDays(null);
			s.setDuration(20);
			s.setTracking(true);
			mSchedules=new ArrayList<Schedule>();
			mSchedules.add(s);
			//Log.i("mSchedule size",new Integer(mSchedules.size()).toString());
			//mSchedules = (List<Schedule>) FileKit.readObject(context, FILENAME_SCHEDULES);
		}
		return mSchedules;
	}
	
	public static void setSchedules(Context context, List<Schedule> schedules) {
		if(schedules != null) {
			mSchedules = schedules;
			FileKit.saveObject(context, FILENAME_SCHEDULES, mSchedules);
		}
	}
	
	public static void addSchedule(Context context, Schedule schedule) {
		if(getSchedules(context) == null) {
			mSchedules = new ArrayList<Schedule>();
		}
		mSchedules.add(schedule);
		FileKit.saveObject(context, FILENAME_SCHEDULES, mSchedules);
	}
	
	public static void removeSchedule(Context context, Schedule schedule) {
		if(getSchedules(context) == null) {
			return;
		}
		mSchedules.remove(schedule);
		FileKit.saveObject(context, FILENAME_SCHEDULES, mSchedules);
	}
	
	public static void saveSchedule(Context context, Schedule schedule) {
		if(getSchedules(context) == null) {
			return;
		}
		int id = mSchedules.indexOf(schedule);
		mSchedules.remove(id);
		mSchedules.add(id, schedule);
		FileKit.saveObject(context, FILENAME_SCHEDULES, mSchedules);
	}
	
	public static int getScheduleId(Context context, Schedule schedule) {
		if(getSchedules(context) == null) {
			return -1;
		}
		return mSchedules.indexOf(schedule);
	}
	
	/****************** Take Records *********************/
	
	public static List<TakeRecord> getTakeRecords(Context context) {
		if(mTakeRecords == null) {
			Schedule s=DataCenter.getSchedules(context).get(0);
			Schedule.Time time=s.getTimes().get(0);
	
			Calendar cal=Calendar.getInstance();
			cal.set(2013,11,4,10,30,0);
			Date takedate=cal.getTime();
			cal.set(2013,11,4,time.hour,time.minute,0);
			Date plan=cal.getTime();
			TakeRecord take=new TakeRecord(s,takedate,plan);
			mTakeRecords=new ArrayList<TakeRecord>();
			mTakeRecords.add(take);
			
			cal.set(2013,11,6,10,35,0);
			takedate=cal.getTime();
			cal.set(2013,11,6,time.hour,time.minute,0);
			plan=cal.getTime();
			take=new TakeRecord(s,takedate,plan);		
			mTakeRecords.add(take);
			
			
			//Log.i("mTakeRecord size",new Integer(mTakeRecords.size()).toString());
			//mTakeRecords = (List<TakeRecord>) FileKit.readObject(context, FILENAME_TAKE_RECORD);
		}
		
		return mTakeRecords;
	}
	
	public static void addTakeRecord(Context context, TakeRecord record) {
		getTakeRecords(context);
		if(mTakeRecords == null) {
			mTakeRecords = new ArrayList<TakeRecord>();
		}
		mTakeRecords.add(0, record);
		FileKit.saveObject(context, FILENAME_TAKE_RECORD, mTakeRecords);
	}
	
	/****************** Appointment *********************/
	
	public static List<Appointment> getAppointments(Context context) {
		if(mAppointments == null) {
			mAppointments = (List<Appointment>) FileKit.readObject(context, FILENAME_APPOINTMENT);
		}
		return mAppointments;
	}
	
	public static void addAppointments(Context context, Appointment appointment) {
		getAppointments(context);
		if(mAppointments == null) {
			mAppointments = new ArrayList<Appointment>();
		}
		mAppointments.add(appointment);
		FileKit.saveObject(context, FILENAME_APPOINTMENT, mAppointments);
	}
	
	public static void removeAppointments(Context context, Appointment appointment) {
		if(getAppointments(context) == null) {
			return;
		}
		mAppointments.remove(appointment);
		FileKit.saveObject(context, FILENAME_APPOINTMENT, mAppointments);
	}
	
	public static int getAppointmentId(Context context, Appointment appointment) {
		if(getAppointments(context) == null) {
			return -1;
		}
		return mAppointments.indexOf(appointment);
	}
	
	/****************** Games *********************/
	
	public static List<Game> getGames(Context context) {
		if(mGames == null) {
			String json = ResourceKit.readAsString(context, R.raw.game_list);
			try {
				mGames = JsonFactory.parseGameList(new JSONArray(json));
			} catch (JSONException e) {
				Logger.e(TAG, e.getMessage(), e);
			}
		}
		return mGames;
	}
}
