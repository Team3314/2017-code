package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInput {
	Joystick leftStick;
	Joystick rightStick;

	public HumanInput() {
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
	}
	
	public boolean getLightsOff() {
		//returns whether operator wants to turn solenoid lights off
		boolean result = false;
		result = leftStick.getRawButton(2);
		return result;
	}
	
	public boolean getFirstLight() {
		//returns whether operator wants to turn reverse light on
		boolean result = false;
		result = leftStick.getRawButton(1);
		return result;
	}
	
	public boolean getSecondLight() {
		//returns whether operator wants to turn forward light on
		boolean result = false;
		result = rightStick.getRawButton(1);
		return result;
	}	
}