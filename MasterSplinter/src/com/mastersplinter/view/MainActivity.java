package com.mastersplinter.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

import com.mastersplinter.api.API_NXT;
import com.mastersplinter.utilities.Compass;
import com.mastersplinter.variables.Variables;
import com.mastersplinter.variables.Variables.CONN_TYPE;
import com.mastersplinter.models.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	NXTConnector conn = null;

	private TextView status_msg = null;
	private FrameLayout progressContainer = null;

	private LightSensor light = null;
	private SoundSensor sound = null;
	private TouchSensor touch = null;
	private UltrasonicSensor sonic = null;

	private boolean ENGINES_ON = false;

	private Compass compass = null;
	private int direction = 0; // 0-North ; 1-East ; 2-South ; 3-West 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		compass = new Compass(this);

		this.status_msg = (TextView) findViewById(R.id.status_msg);
		this.progressContainer = (FrameLayout) findViewById(R.id.progressContainer);

		setupNXJCache();
	}


	public void onClickStartEngines(View v){
		/*	findViewById(R.id.start_engines).setClickable(false);
		findViewById(R.id.stop_engines).setClickable(true);
		 */	

		ENGINES_ON = true;
		progressContainer.setVisibility(View.VISIBLE);

		if(conn == null){
			startConnection();
			Motor.B.setPower(Variables.MOTOR_POWER);
			Motor.C.setPower(Variables.MOTOR_POWER);
		}
		startSensors();

		new StartBot().execute();
	}

	public void onClickStopEngines(View v){
		/*	findViewById(R.id.start_engines).setClickable(true);
		findViewById(R.id.stop_engines).setClickable(false);
		 */	
		ENGINES_ON = false;
	}

	private class StartBot extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {

			/*	Log.d("Light Sensor", light.readValue() + "");
			Log.d("Sound Sensor", sound.readValue() + "");
			Log.d("Touch Sensor", touch.isPressed() + "");
			Log.d("Ultrasonic Sensor", sonic.getDistance() + "");
			 */
			switch(direction){ // Always rotating right // TODO: rotate to the best side
			case 0:
				if(!compass.getDirection().isFacingNorth()){
					return Variables.ROTATE_RIGHT;
				}
				break;
			case 1:
				if(!compass.getDirection().isFacingEast()){
					return Variables.ROTATE_RIGHT;
				}
				break;
			case 2:
				if(!compass.getDirection().isFacingSouth()){
					return Variables.ROTATE_RIGHT;
				}
				break;
			case 3:
				if(!compass.getDirection().isFacingWest()){
					return Variables.ROTATE_RIGHT;
				}
				break;
			}

			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(progressContainer.isShown())
				progressContainer.setVisibility(View.GONE);

			if(ENGINES_ON){
				switch(result){
					case 0: // Move Front
						Motor.B.forward();
						Motor.C.forward();
						break;
					case 1: // Move Backwards
						Motor.B.backward();
						Motor.C.backward();
						break;
					case 2: // Rotate Right
						Motor.B.forward();
						Motor.C.backward();
						break;
					case 3: // Rotate Left
						Motor.B.backward();
						Motor.C.forward();
						break;
					default:
						Motor.B.stop();
						Motor.C.stop();
						break;
				}

				try {
					Thread.sleep(100);
					new StartBot().execute();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				stopActuators();
				//	closeConnection();
			}
		}
	}

	private void setupNXJCache() {

		File root = Environment.getExternalStorageDirectory();

		try {
			String androidCacheFile = "nxj.cache";
			File mLeJOS_dir = new File(root + "/MasterSplinter");
			if (!mLeJOS_dir.exists()) {
				mLeJOS_dir.mkdir();

			}
			File mCacheFile = new File(root + "/MasterSplinter/", androidCacheFile);

			if (root.canWrite() && !mCacheFile.exists()) {
				FileWriter gpxwriter = new FileWriter(mCacheFile);
				BufferedWriter out = new BufferedWriter(gpxwriter);
				out.write("");
				out.flush();
				out.close();
				this.status_msg.setText("nxj.cache (record of connection addresses) written to: " + mCacheFile.getName() + Variables.GO_AHEAD);
			} else {
				this.status_msg.setText("nxj.cache file not written as"
						+ (!root.canWrite() ? mCacheFile.getName() + " can't be written to sdcard." : " cache already exists.") + Variables.GO_AHEAD);

			}
		} catch (IOException e) {
			Log.e(Variables.TAG, "Could not write nxj.cache " + e.getMessage(), e);
		}
		//	this.status_msg.setVisibility(View.VISIBLE);
		//	this.status_msg.requestLayout();
	}

	public void stopActuators() {
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
	}

	private void startSensors() {
		if(light == null)
			this.light = new LightSensor(SensorPort.S3);
		if(sound == null)
			this.sound = new SoundSensor(SensorPort.S2);
		if(touch == null)
			this.touch = new TouchSensor(SensorPort.S1);
		if(sonic == null)
			this.sonic = new UltrasonicSensor(SensorPort.S4);
	}

	private void startConnection() {
		conn = API_NXT.connect(CONN_TYPE.LEGO_LCP);
		NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void closeConnection() {
		if (conn != null) {
			try {
				NXTCommandConnector.close();
				conn.close();
				conn.getNXTComm().close();
			} catch (Exception e) {
			} finally {
				Log.d("CONNECTION", "NULL");
				conn = null;
			}
		}
	}

	@Override
	protected void onStop(){
		compass.stopListener();
		super.onStop();
	}

	@Override
	protected void onResume(){
		super.onResume();
		compass.resumeListener();
	}

	public void faceNorth(View v){
		direction = Variables.NORTH_DIR;
	}

	public void faceSouth(View v){
		direction = Variables.SOUTH_DIR;
	}

	public void faceEast(View v){
		direction = Variables.EAST_DIR;
	}

	public void faceWest(View v){
		direction = Variables.WEST_DIR;
	}
}
