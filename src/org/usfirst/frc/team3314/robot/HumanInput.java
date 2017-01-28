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
		
	public boolean getGyroLock() {
		//returns whether driver wants to turn gyrolock on
		boolean result = false;
		result = rightStick.getRawButton(1);
		return result;
	}
		
	public boolean getHighGear() {
		//returns whether driver wants to be on high gear
		boolean result = false;
		result = leftStick.getRawButton(3);
		return result;
	}
	
	public boolean getExtendGearIntake() {
		//returns whether driver wants to extend gear intake
		boolean result = false;
		result = operator.getRawButton(1);
		return result;
	}
	
	public boolean getRetractGearIntake() {
		//returns whether driver wants to retract gear intake
		boolean result = false;
		result = operator.getRawButton(2);
		return result;
	}
		
	public boolean getLowGear() {
		//returns whether driver wants to be on low gear
		boolean result = false;
		result = leftStick.getRawButton(2);
		return result;
	}
	
	public boolean getShoot() {
		//returns whether operator wants to shoot
		boolean result = false;
		result = operator.getRawButton(6);
		return result;
	}
}