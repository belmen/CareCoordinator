package cis573.carecoor;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cis573.carecoor.bean.Medicine;
import cis573.carecoor.bean.Schedule;
import cis573.carecoor.bean.Schedule.Time;
import cis573.carecoor.data.DataCenter;
import cis573.carecoor.data.MedicineCenter;
import cis573.carecoor.data.ScheduleCenter;
import cis573.carecoor.reminder.ReminderCenter;
import cis573.carecoor.utils.Const;
import cis573.carecoor.utils.Utils;

public class MedScheduleFragment extends Fragment {

	public static final String TAG = "MedScheduleFragment";
	
	private static final int REQUEST_NEW_SCHEDULE = 0;
	private static final int REQUEST_TAKE = 1;

	private Button mBtnNew;
	private Button mBtnTrack;
	private ListView mLvSchedules;
	private ScheduleAdapter mAdapter = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.med_schedule_view, container, false);
		mBtnNew = (Button) view.findViewById(R.id.med_schedule_add_button);
		mBtnNew.setOnClickListener(onNewClick);
		mBtnTrack = (Button) view.findViewById(R.id.med_schedule_track_button);
		mBtnTrack.setOnClickListener(onTrackClick);
		mLvSchedules = (ListView) view.findViewById(R.id.med_schedule_list);
		mLvSchedules.setOnItemClickListener(onScheduleItemClick);
		mLvSchedules.setOnItemLongClickListener(onScheduleItemLongClick);
		TextView tvEmpty = (TextView) view.findViewById(R.id.med_schedule_empty);
		mLvSchedules.setEmptyView(tvEmpty);
		mAdapter = new ScheduleAdapter(getActivity());
		mLvSchedules.setAdapter(mAdapter);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter.setScheduleList(DataCenter.getSchedules(getActivity()));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_NEW_SCHEDULE || requestCode == REQUEST_TAKE) {
			if(resultCode == Activity.RESULT_OK) {
				mAdapter.setScheduleList(DataCenter.getSchedules(getActivity()));
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private OnItemClickListener onScheduleItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Schedule item = (Schedule) parent.getItemAtPosition(position);
			Intent intent = new Intent(getActivity(), TakeMedicineActivity.class);
			intent.putExtra(Const.EXTRA_SCHEDULE, item);
			startActivityForResult(intent, REQUEST_TAKE);
		}
	};
	
	private OnItemLongClickListener onScheduleItemLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final Schedule item = (Schedule) parent.getItemAtPosition(position);
			new AlertDialog.Builder(getActivity())
			.setTitle(R.string.dialog_title_remove_schedule)
			.setMessage(R.string.msg_remove_schedule)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ReminderCenter.cancelAlarm(getActivity(), item);
					DataCenter.removeSchedule(getActivity(), item);
					mAdapter.setScheduleList(DataCenter.getSchedules(getActivity()));
					mAdapter.notifyDataSetChanged();
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).show();
			return false;
		}
	};

	private OnClickListener onNewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), ChooseMedActivity.class);
			startActivityForResult(intent, REQUEST_NEW_SCHEDULE);
		}
	};
	
	private OnClickListener onTrackClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(!ScheduleCenter.hasTrackingSchedule(getActivity())) {
				new AlertDialog.Builder(getActivity())
				.setMessage(R.string.msg_no_tracking)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();
				return;
			}
			Intent intent = new Intent(getActivity(), TrackActivity.class);
			startActivity(intent);
		}
	};
	
	public static class ScheduleAdapter extends BaseAdapter {

		private Context mContext;
		private List<Schedule> mScheduleList;
		
		public ScheduleAdapter(Context context) {
			this.mContext = context;
		}

		public void setScheduleList(List<Schedule> scheduleList) {
			this.mScheduleList = scheduleList;
		}

		@Override
		public int getCount() {
			return mScheduleList != null ? mScheduleList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mScheduleList != null ? mScheduleList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if(convertView == null) {
				convertView = View.inflate(mContext, R.layout.schedule_item, null);
				vh = new ViewHolder();
				vh.name = (TextView) convertView.findViewById(R.id.schedule_item_name);
				vh.state = (TextView) convertView.findViewById(R.id.schedule_item_status);
				vh.tracking = (TextView) convertView.findViewById(R.id.schedule_item_tracking);
				vh.image = (ImageView) convertView.findViewById(R.id.schedule_item_image);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			Medicine med = null;
			Schedule item = (Schedule) getItem(position);
			if(item != null) {
				int state = ScheduleCenter.getScheduleStatus(mContext, item);
				if(state == ScheduleCenter.SCHEDULE_NO_TODAY) {
					vh.state.setText(R.string.schedule_state_no_today);
				} else if(state == ScheduleCenter.SCHEDULE_ENDED) {
					vh.state.setText(R.string.schedule_state_ended);
				} else if(state >= 0) {
					List<Time> times = item.getTimes();
					if(state < times.size()) {	// Not finished
						String next = mContext.getString(R.string.schedule_state_next,
								Utils.get12ClockTimeString(times.get(state)));
						vh.state.setText(Html.fromHtml(next));
					} else {	// Finished
						String finished = mContext.getString(R.string.schedule_state_finished);
						vh.state.setText(Html.fromHtml(finished));
					}
				}
				if(item.isTracking()) {
					vh.tracking.setVisibility(View.VISIBLE);
				} else {
					vh.tracking.setVisibility(View.GONE);
				}
				med = item.getMedicine();
				if(med != null) {
					vh.name.setText(med.getName());
					vh.image.setImageResource(MedicineCenter.getMedicineImageRes(mContext, med));
				}
			}
			return convertView;
		}
		
		private static class ViewHolder {
			TextView name;
			TextView state;
			TextView tracking;
			ImageView image;
		}
	}
}
