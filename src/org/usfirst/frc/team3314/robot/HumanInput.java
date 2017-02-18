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
		
	//following methods return whether driver/operator press button to do something specific
	public boolean getGyroLock() {
		boolean result = false;
		result = rightStick.getRawButton(1);
		return result;
	}
	
	public boolean getSpeedControl() {
		boolean result = false;
		result = leftStick.getRawButton(1);
		return result;
	}
		
	public boolean getHighGear() {
		boolean result = false;
		result = leftStick.getRawButton(3);
		return result;
	}
	
	public boolean getLowGear() {
		boolean result = false;
		result = leftStick.getRawButton(2);
		return result;
	}
	
	public boolean getExtendGearIntake() {
		boolean result = false;
		result = operator.getRawButton(1);
		return result;
	}
	
	public boolean getRetractGearIntake() {
		boolean result = false;
		result = operator.getRawButton(2);
		return result;
	}
	
	public boolean getFuelIntake() {
		//ball intake
		boolean result = false;
		result = rightStick.getRawButton(5);
		return result;
	}
	
	public boolean getShoot() {
		boolean result = false;
		result = operator.getRawButton(6);
		return result;
	}
	
	public boolean getFlashlight() {
		boolean result = false;
		result = operator.getRawButton(3);
		return result;
}
	public boolean turnNinety() {
		return leftStick.getRawButton(7);
	}
	}