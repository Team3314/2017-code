package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInput {
	Joystick leftStick;
	Joystick rightStick;
	Joystick operator;

	public HumanInput() {
		leftStick = new Joystick(1);
		rightStick = new Joystick(2);
		operator = new Joystick(0);
	}
	
	public boolean getLightsOff() {
		//returns whether operator wants to turn solenoid lights off
		boolean result = false;
		result = operator.getRawButton(2);
		return result;
	}
	
	public boolean getFirstLight() {
		//returns whether operator wants to turn reverse light on
		boolean result = false;
		result = operator.getRawButton(5);
		return result;
	}
	
	public boolean getSecondLight() {
		//returns whether operator wants to turn forward light on
		boolean result = false;
		result = operator.getRawButton(6);
		return result;
	}	
}