package com.mastersplinter.utilities;


import com.mastersplinter.models.*;
import com.mastersplinter.variables.Variables;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Compass {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;
	private Direction direction = null;

	private final SensorEventListener mListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
		//	Log.d(Variables.TAG_COMPASS, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
			mValues = event.values;
			getDirection().setValue(event.values[0]);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public Compass(Context context){
		mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		this.setDirection(new Direction());
	}

	public void resumeListener(){
		mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public void stopListener(){
		mSensorManager.unregisterListener(mListener);
	}
	
	public float getOrientation(){
		return mValues[0];
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

}
