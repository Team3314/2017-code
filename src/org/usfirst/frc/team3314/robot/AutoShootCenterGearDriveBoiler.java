package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


enum autoShootCenterGearDriveBoilerState{
	START,
	TURNTURRET,
	SHOOT,
	DRIVE,
	DROPGEAR,
	WAIT,
	DRIVEBACK,
	//you probably want
	STOP,
	TURN,
	DRIVE2,
	DONE,
}
public class AutoShootCenterGearDriveBoiler {

		autoShootCenterGearDriveBoilerState currentState;
		autoShootCenterGearDriveBoilerState nextState;
		Robot robot;
		int time;
		double desiredDistance = 0;
		
		public AutoShootCenterGearDriveBoiler(Robot myRobot) {
			robot = myRobot;
			currentState = autoShootCenterGearDriveBoilerState.START;
			time = 0;
		
		}
		
		public void update() {
			calcNext();
			doTransition();
			currentState = nextState;
			time--;
		}
		
		public void reset() {
			currentState = autoShootCenterGearDriveBoilerState.START;
		}
		


		public void calcNext() {
			nextState = currentState;
			switch (currentState) {
				case START:
					nextState = autoShootCenterGearDriveBoilerState.TURNTURRET;
					break;
					
				case TURNTURRET:
					if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 100 || time <= 0)  {
						nextState = autoShootCenterGearDriveBoilerState.SHOOT;
					}
					break;
				case SHOOT:
					if (time <= 0 ) {
						nextState = autoShootCenterGearDriveBoilerState.DRIVE;
					}
					break;
				case DRIVE:
					if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveBoilerState.DROPGEAR;
					}
					break;
				case DROPGEAR:
					if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
						nextState = autoShootCenterGearDriveBoilerState.WAIT;
					}
					break;
				case WAIT:
					if (time <= 0) {
						nextState = autoShootCenterGearDriveBoilerState.DRIVEBACK;
					}
					break;
				case DRIVEBACK:
					if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveBoilerState.DONE;
					}
					break;
				case STOP:
					if (time<= 0) {
						nextState = autoShootCenterGearDriveBoilerState.TURN;
					}
					break;
				case TURN:
					if (robot.tdt.gyroControl.onTarget()) {
						nextState = autoShootCenterGearDriveBoilerState.DRIVE2;
					}
					break;
				case DRIVE2:
					if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveBoilerState.DONE;
					}
					break;
				case DONE:
					break;
			}
			
			
		}
		public void doTransition() {
			if (currentState == autoShootCenterGearDriveBoilerState.START && nextState == autoShootCenterGearDriveBoilerState.TURNTURRET) {	
				if (robot.blueRequest) {
					robot.shooter.desiredSpeed = 4350;
					robot.cam.desiredPosition = 1500;		
					robot.turret.desiredTarget =.065;
				}
				else if (robot.redRequest) {
					robot.shooter.desiredSpeed = 4478;
					robot.cam.desiredPosition = 1533;		
					robot.turret.desiredTarget = 7.18;
				}
				time = 50;
			}
			if (currentState == autoShootCenterGearDriveBoilerState.TURNTURRET && nextState == autoShootCenterGearDriveBoilerState.SHOOT) {
				robot.shootRequest = true;
				time = 250; 
			}
			if (currentState == autoShootCenterGearDriveBoilerState.SHOOT && nextState == autoShootCenterGearDriveBoilerState.DRIVE) {
				robot.shootRequest = false;
				if (robot.blueRequest) {
					desiredDistance = 80;
				}
				else if (robot.redRequest) {
					desiredDistance = 80;
				}
				robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
				robot.tdt.setDriveMode(driveMode.GYROLOCK);
				robot.tdt.setDriveAngle(robot.navx.getYaw());
				robot.tdt.setDriveTrainSpeed(.5);
			 }
			if (currentState == autoShootCenterGearDriveBoilerState.DRIVE && nextState == autoShootCenterGearDriveBoilerState.DROPGEAR) {
				robot.tdt.setDriveTrainSpeed(0);
				robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
			}
			if (currentState == autoShootCenterGearDriveBoilerState.DROPGEAR && nextState == autoShootCenterGearDriveBoilerState.WAIT) {
				time = 15;
				robot.tdt.resetDriveEncoders();
				desiredDistance = -36;
				}
			if (currentState == autoShootCenterGearDriveBoilerState.WAIT && nextState == autoShootCenterGearDriveBoilerState.DRIVEBACK) {
				robot.tdt.setDriveTrainSpeed(-1);
			}
			
			if (currentState == autoShootCenterGearDriveBoilerState.DRIVEBACK && nextState == autoShootCenterGearDriveBoilerState.STOP) {
				robot.tdt.setDriveTrainSpeed(0);
			}
			if (currentState == autoShootCenterGearDriveBoilerState.STOP && nextState == autoShootCenterGearDriveBoilerState.TURN) {
				if (robot.blueRequest) {
					//desiredDistance = 216;
					//robot.tdt.setDriveAngle(25);
				}
				else if (robot.redRequest) {
					//desiredDistance = 216;
					//robot.tdt.setDriveAngle(-25);
				}
				
			}
			if (currentState == autoShootCenterGearDriveBoilerState.TURN && nextState == autoShootCenterGearDriveBoilerState.DRIVE2) {
				robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
				robot.tdt.resetDriveEncoders();
				robot.tdt.setDriveTrainSpeed(1);
			}
			
			if (currentState == autoShootCenterGearDriveBoilerState.DRIVEBACK && nextState == autoShootCenterGearDriveBoilerState.DONE) {
				robot.tdt.setDriveTrainSpeed(0);
			}
		 }
	}