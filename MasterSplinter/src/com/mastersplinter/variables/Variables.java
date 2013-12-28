package com.mastersplinter.variables;

public class Variables {
	public final static String TAG = "Master Splinter";
	public final static String TAG_COMPASS = "Compass";
	public static final String GO_AHEAD = "Choose one!";
	public static enum CONN_TYPE {
		LEJOS_PACKET, LEGO_LCP
	}
	
	// Compass things
	public static float NORTH = 0.0f;
	public static float SOUTH = 180.0f;
	public static float EAST = 90.0f;
	public static float WEST = 270.0f;
	public static float THRESHOLD = 15.0f;
	
	public static int NORTH_DIR = 0;
	public static int EAST_DIR = 1;
	public static int SOUTH_DIR = 2;
	public static int WEST_DIR = 3;
	
	// Actuators
	public static int MOVE_FRONT = 0;
	public static int MOVE_BACKWARDS = 1;
	public static int ROTATE_RIGHT = 2;
	public static int ROTATE_LEFT = 3;
	public static int MOTOR_POWER = 35;
	public static int MORE_SPEED_RIGHT = 1;
	public static int MORE_SPEED_LEFT = 3;
	public static int BOTH_SAME_SPEED = 2;
	
	// OpenCV stuff
	public static int COLOR_SEG = 0;
	public static int NORMAL_MODE = 1;
	public static int MEDIUM_VALUE = 185;
	public static int THRESHOLD_COLOR = 45;

}
