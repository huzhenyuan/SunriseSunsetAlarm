package com.zhenyuan.sunrisesunsetalarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSeekBarChangeListener {

	public static final String SUNRISE_ALARM_INTENT = "com.zhenyuan.sunrise.alarm";
	public static final String SUNSET_ALARM_INTENT = "com.zhenyuan.sunset.alarm";
	private static final String SUN_RISE_SET_FORMAT = "HH:mm";
	private SeekBar mSunriseSeekBar, mSunsetSeekBar;

	private TextView mSunriseTime, mSunsetTime, mSunriseAlarmTime,
			mSunsetAlarmTime;

	private Calendar mSunriseDate, mSunsetDate;

	private double mLatitude = 0, mLongitude = 0;

	private String SP = "sp";

	private int mSunriseProgress = 0, mSunsetProgress = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSunriseSeekBar = (SeekBar) findViewById(R.id.sunrise_seek);
		mSunsetSeekBar = (SeekBar) findViewById(R.id.sunset_seek);
		mSunriseTime = (TextView) findViewById(R.id.sunrise_time);
		mSunsetTime = (TextView) findViewById(R.id.sunset_time);
		mSunriseAlarmTime = (TextView) findViewById(R.id.sunrise_alarm_time);
		mSunsetAlarmTime = (TextView) findViewById(R.id.sunset_alarm_time);

		mSunriseSeekBar.setOnSeekBarChangeListener(this);
		mSunsetSeekBar.setOnSeekBarChangeListener(this);
		double[] location = LocationUtil
				.getLocalTitudes(getApplicationContext());
		if (location != null) {
			mLatitude = location[0];
			mLongitude = location[1];
		}

		Calendar[] suniseSunset = null;
		Calendar calendar = Calendar.getInstance();
		suniseSunset = SunriseSunset.getSunriseSunset(calendar, mLatitude,
				mLongitude);
		SimpleDateFormat formatCopy = (SimpleDateFormat) new SimpleDateFormat(
				SUN_RISE_SET_FORMAT).clone();
		formatCopy.setTimeZone(calendar.getTimeZone());
		mSunriseTime.setText(formatCopy.format(suniseSunset[0].getTime()));
		mSunsetTime.setText(formatCopy.format(suniseSunset[1].getTime()));

		mSunriseDate = suniseSunset[0];
		mSunsetDate = suniseSunset[1];

		SharedPreferences sp = getSharedPreferences(SP, 0);
		mSunriseProgress = sp.getInt(SUNRISE_ALARM_INTENT, 0);
		mSunsetProgress = sp.getInt(SUNSET_ALARM_INTENT, 0);

		if (mSunriseProgress != 0) {
			mSunriseSeekBar.setProgress(mSunriseProgress);
		}
		if (mSunsetProgress != 0) {
			mSunsetSeekBar.setProgress(mSunsetProgress);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		TextView target = null;
		String targetName = null;
		Calendar targetDate = null;
		if (seekBar == mSunriseSeekBar) {
			target = mSunriseAlarmTime;
			targetName = SUNRISE_ALARM_INTENT;
			targetDate = (Calendar) mSunriseDate.clone();
		} else if (seekBar == mSunsetSeekBar) {
			target = mSunsetAlarmTime;
			targetName = SUNSET_ALARM_INTENT;
			targetDate = (Calendar) mSunsetDate.clone();
		}
		int minute = 0;
		if (target != null) {
			switch (progress) {
			case 0:
				minute = 0;
				break;
			case 1:
				minute = 5;
				break;
			case 2:
				minute = 10;
				break;
			case 3:
				minute = 15;
				break;
			case 4:
				minute = 30;
				break;
			default:
				break;
			}
		}

		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent rtcIntent = PendingIntent.getBroadcast(this, 0,
				new Intent(targetName), 1);
		alarms.cancel(rtcIntent);
		if (minute != 0) {
			targetDate.add(Calendar.DAY_OF_MONTH, 1);
			targetDate.add(Calendar.MINUTE, -minute);
			alarms.set(AlarmManager.RTC_WAKEUP, targetDate.getTimeInMillis(),
					rtcIntent);
			SimpleDateFormat formatCopy = (SimpleDateFormat) new SimpleDateFormat(
					SUN_RISE_SET_FORMAT).clone();
			Calendar calendar = Calendar.getInstance();
			formatCopy.setTimeZone(calendar.getTimeZone());
			target.setText(formatCopy.format(targetDate.getTime()));
		} else {
			target.setText(getResources().getString(R.string.off));
		}

		Editor backup = getSharedPreferences(SP, 0).edit();
		backup.putInt(targetName, seekBar.getProgress());
		backup.commit();
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {

	}

}
