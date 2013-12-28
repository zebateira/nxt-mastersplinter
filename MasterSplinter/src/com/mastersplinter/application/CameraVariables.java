package com.mastersplinter.application;

import android.app.Application;

public class CameraVariables extends Application {
	private int valX = -1, valY = -1;

	public static CameraVariables cameraVariables = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public CameraVariables() {
	    super();
	}

	public int getValX() {
		return valX;
	}

	public void setValX(int valX) {
		this.valX = valX;
	}
}
