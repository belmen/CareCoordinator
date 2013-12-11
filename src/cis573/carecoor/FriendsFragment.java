package cis573.carecoor;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cis573.carecoor.bean.Friend;

public class FriendsFragment extends Fragment {
	
	private ListView mListView;
	private FriendsAdapter mAdapter;
	private List<Friend> mFriendList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.friends_fragment, container, false);
		mListView = (ListView) view;
		mListView.setOnItemClickListener(onItemClick);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new FriendsAdapter(getActivity());
		mFriendList = new ArrayList<Friend>();
		addFriends(mFriendList);
		mAdapter.setFriendList(mFriendList);
		mListView.setAdapter(mAdapter);
	}

	private static void addFriends(List<Friend> list) {
		if(list == null) {
			return;
		}
		list.add(new Friend("Penn Nursing", "https://www.facebook.com/PennNursing", R.drawable.penn_nursing));
		list.add(new Friend("Penn Engineering", "https://www.facebook.com/PennEngineering", R.drawable.penn_engineering));
	}
	
	OnItemClickListener onItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Friend item = (Friend) arg0.getItemAtPosition(arg2);
			if(item != null) {
				final String url = item.getUrl();
				new AlertDialog.Builder(getActivity())
				.setTitle(R.string.dialog_title_friends)
				.setMessage(getString(R.string.dialog_friends_facebook_page, item.getName()))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();
			}
		}
	};
	
	public static class FriendsAdapter extends BaseAdapter {

		private Context mContext;
		private List<Friend> mFriendList;
		
		
		public FriendsAdapter(Context context) {
			this.mContext = context;
		}

		public void setFriendList(List<Friend> friendList) {
			this.mFriendList = friendList;
		}

		@Override
		public int getCount() {
			return mFriendList != null ? mFriendList.size() : 0;
		}

		@Override
		public Object getItem(int arg0) {
			return mFriendList != null ? mFriendList.get(arg0) : null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if(convertView == null) {
				convertView = View.inflate(mContext, R.layout.friends_list_item, null);
				vh = new ViewHolder();
				vh.icon = (ImageView) convertView.findViewById(R.id.friends_item_icon);
				vh.name = (TextView) convertView.findViewById(R.id.friends_item_name);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			Friend item = (Friend) getItem(position);
			if(item != null) {
				vh.icon.setImageResource(item.getIconId());
				vh.name.setText(item.getName());
			}
			return convertView;
		}
		
		private static class ViewHolder {
			ImageView icon;
			TextView name;
		}
	}
}
