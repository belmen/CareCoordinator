package cis573.carecoor;

import cis573.carecoor.utils.Logger;
import cis573.carecoor.utils.MyToast;
import cis573.carecoor.utils.PreferenceUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BannerActivity extends FragmentActivity {

	public static final String TAG = "BaseActivity";
	
	private static final int REQUEST_ALERT_CALL = 999;
	
	private Button mBtnBack;
	private TextView mTvTitle;
	private Button mBtnAlert;
	private AlertDialog mAlertCfmDialog;
	private AlertDialog mAlertSecDialog;
	private Animation mAnimTitleIn;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initDialogs();
		mAnimTitleIn = AnimationUtils.loadAnimation(this, R.anim.title_in);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Logger.i(TAG, "mTvTitle is " + (mTvTitle == null ? "null" : "not null"));
//		if(mTvTitle != null) {
//			mTvTitle.startAnimation(mAnimTitleIn);
//		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Logger.i(TAG, String.format("Back from call, request=%d, result=%d", arg0, arg1));
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0 == REQUEST_ALERT_CALL) {
			mAlertSecDialog.show();
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		setContentView(View.inflate(this, layoutResID, null));
	}

	@Override
	public void setContentView(View view) {
		setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.banner_activity, null);
		mBtnBack = (Button) layout.findViewById(R.id.banner_back_btn);
		mBtnBack.setOnClickListener(onBackClick);
		mTvTitle = (TextView) layout.findViewById(R.id.banner_title_text);
		mBtnAlert = (Button) layout.findViewById(R.id.banner_alert_btn);
		mBtnAlert.setOnClickListener(onAlertClick);
		if(view != null) {
			layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		super.setContentView(layout, params);
	}
	
	public void setBannerTitle(int resId) {
		if(mTvTitle != null) {
			mTvTitle.setText(resId);
		}
	}
	
	public void setBannerTitle(String title) {
		if(mTvTitle != null) {
			mTvTitle.setText(title);
		}
	}
	
	public void showBackButton(boolean show) {
		if(mBtnBack != null) {
			if(show) {
				mBtnBack.setVisibility(View.VISIBLE);
			} else {
				mBtnBack.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void alertPrimary() {
		String number = PreferenceUtil.getPrimaryAlertNumber(this);
		if("".equals(number)) {
			MyToast.show(BannerActivity.this, R.string.msg_primary_number_empty);
			return;
		}
		doAlert(number);
	}
	
	private void alertSecondary() {
		String number = PreferenceUtil.getSecondaryAlertNumber(this);
		if("".equals(number)) {
			MyToast.show(BannerActivity.this, R.string.msg_secondary_number_empty);
			return;
		}
		doAlert(number);
	}
	
	private void doAlert(String number) {
		boolean makeCall = PreferenceUtil.getEnableAlertCall(this);
		boolean makeText = PreferenceUtil.getEnableAlertText(this);
		
		if(makeCall) {
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + number));
			startActivityForResult(intent, REQUEST_ALERT_CALL);
		}
		
		if(makeText) {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(number, null, "Test message from care coordinator", null, null);
		}
	}
	
	private void initDialogs() {
		
		mAlertCfmDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.dialog_alert_cfm_title)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertPrimary();
			}
		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		}).create();
		
		mAlertSecDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.dialog_alert_cfm_title)
		.setMessage(R.string.dialog_alert_secondary)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		}).setNeutralButton(R.string.dialog_alert_primary_again, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertPrimary();
			}
		}).setNegativeButton(R.string.dialog_alert_secondary_again, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertSecondary();
			}
		}).create();
	}
	
	private OnClickListener onBackClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
			finish();
		}
	};

	private OnClickListener onAlertClick = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			
			boolean makeCall = PreferenceUtil.getEnableAlertCall(BannerActivity.this);
			boolean makeText = PreferenceUtil.getEnableAlertText(BannerActivity.this);
			
			if(makeCall&&makeText){
				mAlertCfmDialog.setMessage(getString(R.string.dialog_alert_cfm_call_text));
			}else if(makeCall){
				mAlertCfmDialog.setMessage(getString(R.string.dialog_alert_cfm_call));
			}else if(makeText){
				mAlertCfmDialog.setMessage(getString(R.string.dialog_alert_cfm_text));
			}else{
				mAlertCfmDialog.setMessage(getString(R.string.dialog_alert_cfm_none));
			}
			
			mAlertCfmDialog.show();
		}
	};
}
