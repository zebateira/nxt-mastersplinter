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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import com.mastersplinter.api.API_NXT;
import com.mastersplinter.application.CameraVariables;
import com.mastersplinter.utilities.Compass;
import com.mastersplinter.variables.Variables;
import com.mastersplinter.variables.Variables.CONN_TYPE;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class DetectActivity extends Activity implements CvCameraViewListener2 {
	private FrameLayout progressContainer = null;

	/** OpenCV stuff **/
	private CameraBridgeViewBase   mOpenCvCameraView;

	private Mat                    mRgba;
	private Mat                    mGray;

	private MenuItem               mItemColorSeg;
	private MenuItem               mItemNormalView;
	private int mode = 0;
	/** OpenCV stuff **/

	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i("OPENCV", "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//	compass = new Compass(this);
		setContentView(R.layout.vcom_detect_surface_view);
		this.progressContainer = (FrameLayout) findViewById(R.id.progressContainer);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setMaxFrameSize(640, 360);
	}
	
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		Mat finalFrame = null;

		if(mode == Variables.COLOR_SEG){
			finalFrame = colorDetection();
			Moments mm = Imgproc.moments(finalFrame);
			
			CameraVariables.cameraVariables.setValX((int) Math.round(mm.get_m10() / mm.get_m00())); 
	        //valY = (int) Math.round(mm.get_m01() / mm.get_m00()); 
			Log.d("X: ", CameraVariables.cameraVariables.getValX()+"");
		//	Log.d("Y: ", valY+"");
		}
		else if(mode == Variables.NORMAL_MODE){
			return mRgba;
		}

		return finalFrame;
	}
	
	
	private Mat colorDetection() {
		Mat hsv = new Mat();
		Mat hsv2 = new Mat();
		hsv.convertTo(hsv2, CvType.CV_8U);
		Imgproc.cvtColor(mRgba, hsv2, Imgproc.COLOR_RGB2HSV, 0); // default: COLOR_BGR2HSV

		Mat bw = new Mat(); //mHSVThreshed
		Mat bw2 = new Mat();
		bw.convertTo(bw2, CvType.CV_8U);
		
		// *** DETECTA COR
		/*// Luz
		Scalar lightLower = new Scalar(0, 0, 220);
		Scalar lightUpper = new Scalar(255, 10, 255);  
		Core.inRange(hsv2, lightLower, lightUpper, bw2);
		*/

		// Creme (fita de papel)
		// Core.inRange(hsv2, new Scalar(25, 30, 120), new Scalar(43, 42, 255), bw2);
		
		// Vermelho
		Core.inRange(hsv2, new Scalar(0, 100, 30), new Scalar(10, 255,255), bw2);
		return bw2;
	}
	

	@Override
	protected void onStop(){
	//	compass.stopListener();
		super.onStop();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume()
	{
		super.onResume();
	//	compass.resumeListener();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}
	
	@Override
	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//getMenuInflater().inflate(R.menu.main, menu);
		Log.i("MENU", "called onCreateOptionsMenu");
		mItemColorSeg = menu.add("Color Segmentation");
		mItemNormalView = menu.add("Normal view");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MENU", "called onOptionsItemSelected; selected item: " + item);
		if (item == mItemColorSeg)
			this.mode = Variables.COLOR_SEG;
		else if (item == mItemNormalView)
			this.mode = Variables.NORMAL_MODE;

		return true;
	}

}
