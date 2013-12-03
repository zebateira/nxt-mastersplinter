package com.mastersplinter.models;

public class Actuators{
	private int motor_A = 0;
	private int motor_B = 0;
	private int motor_C = 0;
	
	public Actuators(int a, int b, int c){
		this.motor_A = a;
		this.setMotor_B(b);
		this.setMotor_C(c);
	}

	public int getMotor_B() {
		return motor_B;
	}

	public void setMotor_B(int motor_B) {
		this.motor_B = motor_B;
	}

	public int getMotor_C() {
		return motor_C;
	}

	public void setMotor_C(int motor_C) {
		this.motor_C = motor_C;
	}
}
