package cis573.carecoor.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
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
			mSchedules = (List<Schedule>) FileKit.readObject(context, FILENAME_SCHEDULES);
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
			mTakeRecords = (List<TakeRecord>) FileKit.readObject(context, FILENAME_TAKE_RECORD);
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
