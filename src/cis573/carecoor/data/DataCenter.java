package cis573.carecoor.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import cis573.carecoor.bean.Contact;
import cis573.carecoor.bean.Schedule;
import cis573.carecoor.utils.FileKit;

public class DataCenter {

	public static final String TAG = "DataCenter";
	
	private static final String FILENAME_USER_CONTACTS = "user_contacts";
	private static final String FILENAME_USEFUL_CONTACTS = "useful_contacts";
	private static final String FILENAME_DRUG_LISTS="drug_lists";
	private static final String FILENAME_SCHEDULES="schedules";
	
	private static List<Contact> mUsefulContacts = null;
	private static List<Contact> mDrugs = null;
	private static List<Contact> mUserContacts = null;
	private static List<Schedule> mSchedules = null;
	
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
		if(mSchedules == null) {
			mSchedules = (List<Schedule>) FileKit.readObject(context, FILENAME_SCHEDULES);
		}
		if(mSchedules == null) {
			mSchedules = new ArrayList<Schedule>();
		}
		mSchedules.add(schedule);
		FileKit.saveObject(context, FILENAME_SCHEDULES, mSchedules);
	}
	
	public static void removeSchedule(Context context, Schedule schedule) {
		if(mSchedules == null) {
			mSchedules = (List<Schedule>) FileKit.readObject(context, FILENAME_SCHEDULES);
		}
		if(mSchedules == null) {
			return;
		}
		mSchedules.remove(schedule);
		FileKit.saveObject(context, FILENAME_SCHEDULES, mSchedules);
	}
}
