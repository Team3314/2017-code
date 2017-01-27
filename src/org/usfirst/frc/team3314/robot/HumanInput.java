package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInput {
	Joystick leftStick;
	Joystick rightStick;
	Joystick operator;

	public HumanInput() {
		leftStick = new Joystick(0); //left attack3 stick
		rightStick = new Joystick(1); //right attack3 stick
		operator = new Joystick(2); //xbox controller
	}
	
	public boolean getLightsOff() {
		//returns whether operator wants to turn solenoid lights off
		boolean result = false;
		result = operator.getRawButton(2);
		return result;
	}
	
	public boolean getReverseLight() {
		//returns whether operator wants to turn reverse light on
		boolean result = false;
		result = operator.getRawButton(5);
		return result;
	}
	
	public boolean getForwardLight() {
		//returns whether operator wants to turn forward light on
		boolean result = false;
		result = operator.getRawButton(6);
		return result;
	}
		
	public boolean getGyroLock() {
		//returns whether driver wants to turn gyrolock on
		boolean result = false;
		result = rightStick.getRawButton(1);
		return result;
	}
		
	public boolean getHighGear() {
		//returns whether driver wants to be on high gear
		boolean result = false;
		result = leftStick.getRawButton();
		return result;
	}
	
	public boolean getLowGear() {
		//returns whether driver wants to be on low gear
		boolean result = false;
		result = leftStick.getRawButton();
		return result;
	}
	
	public boolean getShoot() {
		//returns whether operator wants to shoot
		boolean result = false;
		result = operator.getRawButton();
		return result;
	}
}