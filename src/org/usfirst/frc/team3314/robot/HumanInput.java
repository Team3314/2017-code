package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInput {
	Joystick leftStick;
	Joystick rightStick;
	Joystick operator;
	Joystick buttonBox;
	Joystick lmao;

	public HumanInput() {
		leftStick = new Joystick(0); //left attack3 stick
		rightStick = new Joystick(1); //right attack3 stick
		operator = new Joystick(2); //xbox controller
		buttonBox = new Joystick(3);
		lmao = new Joystick(4); //extreme 3d stick
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
		boolean result = false;
		result = rightStick.getRawButton(5);
		return result;
	}
	
	public boolean getSpinShooter() {
		boolean result = false;
		result = operator.getRawButton(6);
		return result;
	}
	
	public boolean getShoot() {
		boolean result = false;
		if (operator.getRawAxis(3) > .75) {
			result = true;
		}
		return result;
	}
	
	public boolean getFlashlight() {
		boolean result = false;
		result = operator.getRawButton(7);
		return result;
	}
	public boolean feedShooter() {
		boolean result = false;
		result = operator.getRawButton(5);
		return result;
	}
	public boolean gyroReset() {
		boolean result = false;
		result = leftStick.getRawButton(7);
		return result;
	}
	public boolean turnShooterLeft() {
		boolean result = false;
		if (operator.getPOV() == 270) {
			result = true;
		}
		return result;
	}
	public boolean turnShooterForward() {
		boolean result = false;
		if (operator.getPOV() == 0) {
			result = true;
		}
		return result;
	}
	public boolean turnShooterRight() {
		boolean result = false;
		if (operator.getPOV() == 90) {
			result = true;
		}
		return result;
	}
	public boolean enableTurretTracking() {
		boolean result = false;
		if (operator.getPOV() == 180) {
			result = true;
		}
		return result;
		
	}
	public boolean calibrateCam() {
		return leftStick.getRawButton(10);
	}
	
	public boolean turnNinety() {
		return leftStick.getRawButton(9);
	}
	public boolean turnNegativeNinety() {
		return leftStick.getRawButton(8);
	}
	public boolean turnTen() {
		return rightStick.getRawButton(9);
	}
	public boolean turnNegativeTen() {
		return rightStick.getRawButton(8);
	}
	public boolean runClimber() {
		return operator.getRawButton(3);
	}
	public boolean runClimberReverse() {
		return operator.getRawButton(1);
	}
	public boolean setShooterClose(){
		return operator.getRawButton(7);
	}
	public boolean setShooterFar() {
		return operator.getRawButton(8);
	} 
	public boolean setShooterManual() {
		return operator.getRawButton(4);
	}
	
	public boolean getBinaryOne() {
		return buttonBox.getRawButton(1);
	}
	
	public boolean getBinaryTwo() {
		return buttonBox.getRawButton(2);
	}
	
	public boolean getBinaryFour() {
		return buttonBox.getRawButton(3);
	}
	
	public boolean getBinaryEight() {
		return buttonBox.getRawButton(4);
	}
}