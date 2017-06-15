package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HumanInput {
	Joystick leftStick;
	Joystick rightStick;
	Joystick operator;
	Joystick buttonBox;

	public HumanInput() {
		leftStick = new Joystick(0); //left attack3 stick
		rightStick = new Joystick(1); //right attack3 stick
		operator = new Joystick(2); //xbox controller
		buttonBox = new Joystick(3);
	}
		
	//Following methods return true/false values depending on whether or not the driver or operator 
	//press certain buttons on their controllers. These values are used in the robot class to determine when to perform certain actions
	public boolean getGyroLock() {
		return rightStick.getRawButton(1);
	}
		
	public boolean getHighGear() {
		return leftStick.getRawButton(3);
	}
	
	public boolean getLowGear() {
		return leftStick.getRawButton(2);
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
		if (leftStick.getRawButton(1)) {
			return leftStick.getRawButton(1);
		}
		else {
			return operator.getRawButton(2);
		}
		
	}
	public boolean feedShooter() {
		boolean result = false;
		result = operator.getRawButton(5);
		return result;
	}
	public boolean gyroReset() {
		boolean result = false;
		result = buttonBox.getRawButton(7);
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
		return operator.getRawButton(9);
		
	}
	public boolean zeroCam() {
		return buttonBox.getRawButton(9);
	}
	public boolean runClimber() {
		return operator.getRawButton(1);
	}
	public boolean runClimberReverse() {
		return buttonBox.getRawButton(6);
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
		return buttonBox.getRawButton(13);
	}
	
	public boolean getBinaryTwo() {
		return buttonBox.getRawButton(14);
	}
	
	public boolean getBinaryFour() {
		return buttonBox.getRawButton(15);
	}
	
	public boolean getBinaryEight() {
		return buttonBox.getRawButton(16);
	}
	public boolean getRed() {
		return buttonBox.getRawButton(12);
	}
	public boolean getBlue() {
		boolean result = false;
		if (!buttonBox.getRawButton(12)) {
			result = true;
		}
		return result;
	}
	public boolean getOpenGearIntake() {
		boolean result = false;
		if (!buttonBox.getRawButton(11)) {
			result = true;
		}
		return result;
	}
	
	public boolean getCloseGearIntake() {
		return buttonBox.getRawButton(11);
	}
	public boolean getRingLight() {
		if (leftStick.getRawButton(4)) {
			return leftStick.getRawButton(4);
		}
		return operator.getRawButton(3);
	}
	public boolean enableDistanceChecking() {
		return operator.getRawButton(9);
	}
	public boolean zeroTurret() {
		return buttonBox.getRawButton(10); 
	}
	public boolean getReverseIndex() {
		return buttonBox.getRawButton(6);
	}
	public boolean getReverseAgitator() {
		return buttonBox.getRawButton(3);
	}
	public boolean getHopperShot() {
		if (operator.getPOV() == 180) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean incrementTurretLeft() {
		return buttonBox.getRawButton(1);
	}
	public boolean incrementTurretRight() {
		return buttonBox.getRawButton(2);
	}
	public boolean incrementSpeedUp() {
		return buttonBox.getRawButton(5);
	}
	public boolean incrementSpeedDown() {
		return buttonBox.getRawButton(8);
	}
	public boolean incrementCamPositionUp() {
		return buttonBox.getRawButton(4);
	}
	public boolean incrementCamPositonDown() {
		return buttonBox.getRawButton(7);
	}
	
}
