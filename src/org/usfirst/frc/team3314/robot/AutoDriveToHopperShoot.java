package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoDriveToHopperShootStates {
	START,
	DRIVE1,
	STOP1,
	TURN,
	STOP2,
	DRIVE2,
	DRIVE3,
	SHOOT,
	DONE
	
}

public class AutoDriveToHopperShoot {
	autoDriveToHopperShootStates currentState;
	autoDriveToHopperShootStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
	
	
	public AutoDriveToHopperShoot(Robot myRobot) {
		robot = myRobot;
		currentState = autoDriveToHopperShootStates.START;
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time--;
		
	}
	
	public void reset() {
		currentState = autoDriveToHopperShootStates.START;
		
	}
	
	public void calcNext() {
		switch (currentState) {
			case START:
				nextState = autoDriveToHopperShootStates.DRIVE1;
				break;
			case DRIVE1:
				if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
					nextState = autoDriveToHopperShootStates.STOP1;
				}
				break;
			case STOP1:
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.TURN;
				}
				break;
			case TURN:
				if (robot.tdt.gyroControl.onTarget()) {
					nextState = autoDriveToHopperShootStates.STOP2;
				}
				break;
			case STOP2:
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.DRIVE2;
				}
				break;
			case DRIVE2:
			
				SmartDashboard.putNumber("Encoder Position", robot.tdt.avgEncPos);
				SmartDashboard.putNumber("Desired Encoder Ticks", desiredDistance*Constants.kInToRevConvFactor);
				if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
					nextState = autoDriveToHopperShootStates.DRIVE3;
				}
				break;
			case DRIVE3:
				SmartDashboard.putNumber("Time", time);
				if (time <= 0 /*robot.tdt.calcJerk() <= -.1*/) {
					nextState = autoDriveToHopperShootStates.SHOOT;
				}
				break;
			case SHOOT:
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.DONE;
				}
				break;
			case DONE:
				break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoDriveToHopperShootStates.START && nextState == autoDriveToHopperShootStates.DRIVE1 ) {
			if (robot.blueRequest) {
				robot.turret.desiredTarget = 7.2;
				desiredDistance = 70;
				robot.cam.desiredPosition = 1376;
				robot.shooter.desiredSpeed = 5450;
			}
			else if (robot.redRequest) {
				robot.turret.desiredTarget = 0.1;
				desiredDistance = 70;
				robot.cam.desiredPosition = 1400;
				robot.shooter.desiredSpeed = 4600;
			}
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(1);
			
		}
		if (currentState == autoDriveToHopperShootStates.DRIVE1 && nextState == autoDriveToHopperShootStates.STOP1 ) {
			robot.tdt.setDriveTrainSpeed(0);
			time = 10;
		}
		if (currentState == autoDriveToHopperShootStates.STOP1 && nextState == autoDriveToHopperShootStates.TURN ) {
		
			if (robot.blueRequest) {
				robot.tdt.setDriveAngle(90);
				desiredDistance = -30;
			}
			if (robot.redRequest) {
				robot.tdt.setDriveAngle(-90);
				desiredDistance = -30;
			}
		}
		if (currentState == autoDriveToHopperShootStates.TURN && nextState == autoDriveToHopperShootStates.STOP2 ) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		if (currentState == autoDriveToHopperShootStates.STOP2 && nextState == autoDriveToHopperShootStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(-.5);	
			robot.hal.shooterTalon.set(robot.shooter.desiredSpeed);
		}
		if (currentState == autoDriveToHopperShootStates.DRIVE2 && nextState == autoDriveToHopperShootStates.DRIVE3) {
			time = 50;
			robot.tdt.setDriveMode(driveMode.TANK);
			robot.tdt.setStickInputs(.5, .5);
		}
		if (currentState == autoDriveToHopperShootStates.DRIVE3 && nextState == autoDriveToHopperShootStates.SHOOT ) {
			time = 1000;
			robot.tdt.setStickInputs(0, 0);
			robot.shootRequest = true;
		}
		if (currentState == autoDriveToHopperShootStates.SHOOT && nextState == autoDriveToHopperShootStates.DONE ) {
			
		}
	}

}
