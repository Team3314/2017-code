package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInput {
	Joystick leftStick;
	Joystick rightStick;
	Joystick lmao;
	Joystick operator;

	public HumanInput() {
		leftStick = new Joystick(0); //left attack3 stick
		rightStick = new Joystick(1); //right attack3 stick
		lmao = new Joystick(3);
		operator = new Joystick(2); //xbox controller
	}
		
	//following methods return whether driver/operator press button to do something specific
	public boolean getGyroLock() {
		return rightStick.getRawButton(1);
	}
	
	public boolean getSpeedControl() {
		return leftStick.getRawButton(9);
	}
		
	public boolean getHighGear() {
		return leftStick.getRawButton(3);
	}
	
	public boolean getLowGear() {
		return leftStick.getRawButton(2);
	}
	
	public boolean getRaiseGearIntake() {
		return rightStick.getRawButton(3);
	}
	
	public boolean getDropGearIntake() {
		return rightStick.getRawButton(2);
	}
	
	public boolean getFuelIntake() {
		//ball intake
		return rightStick.getRawButton(5);
	}
	
	public boolean getSpinShooter() {
		//spins up shooter motor but doesn't actually shoot
		return operator.getRawButton(7);
	}
	
	public boolean getShoot() {
		return operator.getRawButton(6);
	}
	
	public boolean getFlashlight() {
		return operator.getRawButton(3);
	}
	
	public boolean turnNinety() {
		return leftStick.getRawButton(7);
	}
}