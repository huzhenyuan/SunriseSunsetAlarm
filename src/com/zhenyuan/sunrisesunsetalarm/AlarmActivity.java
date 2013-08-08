package com.zhenyuan.sunrisesunsetalarm;

import java.io.IOException;
import java.util.HashMap;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlarmActivity extends Activity {
	private SoundPool mSoundPool;
	private float leftVolume = 1.0f, rightVolume = 1.0f;
	private float rate = 1.0f;
	private int priority = 1, loop = -1;
	private HashMap<Integer, Integer> mSoundPoolMap;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

		mSoundPoolMap = new HashMap<Integer, Integer>();
		AssetFileDescriptor descriptor;
		try {
			descriptor = this.getResources().getAssets().openFd("alarm.wav");
			mSoundPoolMap.put(1, mSoundPool.load(descriptor, 1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int streamID = mSoundPool.play(1, leftVolume, rightVolume,
						priority, loop, rate);
				if (streamID != 0) {
					Toast.makeText(AlarmActivity.this,
							getResources().getString(R.string.alarm),
							Toast.LENGTH_SHORT).show();
				}
			}
		}, 1000);

		Button off = (Button) findViewById(R.id.off);
		off.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				mSoundPool.stop(1);
				finish();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSoundPool.stop(1);
		mSoundPool.release();
		mSoundPoolMap.clear();
	}
}
