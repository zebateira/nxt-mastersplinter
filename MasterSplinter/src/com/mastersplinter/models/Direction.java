package com.mastersplinter.models;

import com.mastersplinter.variables.Variables;

public class Direction {
	private float value = -1;
	
	public Direction(){}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	public boolean isFacingNorth(){
		if(value < 360.0f && value > 360.0f-Variables.THRESHOLD)
			return true;
		else if(value >= Variables.NORTH && value < Variables.THRESHOLD)
			return true;
		return false;
	}

	public boolean isFacingSouth(){
		return (value > Variables.SOUTH - Variables.THRESHOLD) && (value < Variables.SOUTH + Variables.THRESHOLD);
	}
	
	public boolean isFacingWest(){
		return (value > Variables.WEST - Variables.THRESHOLD) && (value < Variables.WEST + Variables.THRESHOLD);
	}
	
	public boolean isFacingEast(){
		return (value > Variables.EAST - Variables.THRESHOLD) && (value < Variables.EAST + Variables.THRESHOLD);
	}
}
