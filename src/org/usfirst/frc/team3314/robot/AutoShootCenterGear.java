package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


enum autoShootCenterGearState{
	START,
	TURNTURRET,
	SHOOT,
	DRIVE,
	DROPGEAR,
	WAIT,
	DRIVEBACK,
	DONE,
}
public class AutoShootCenterGear {

		autoShootCenterGearState currentState;
		autoShootCenterGearState nextState;
		Robot robot;
		int time;
		double desiredDistance = 0;
		
		public AutoShootCenterGear(Robot myRobot) {
			robot = myRobot;
			currentState = autoShootCenterGearState.START;
			time = 0;
		
		}
		
		public void update() {
			calcNext();
			doTransition();
			currentState = nextState;
			time--;
		}
		
		public void reset() {
			currentState = autoShootCenterGearState.START;
		}
		


		public void calcNext() {
			nextState = currentState;
			switch (currentState) {
				case START:
					nextState = autoShootCenterGearState.TURNTURRET;
					break;
					
				case TURNTURRET:
					if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 100 || time <= 0)  {
						nextState = autoShootCenterGearState.SHOOT;
					}
					break;
				case SHOOT:
					if (time <= 0 ) {
						nextState = autoShootCenterGearState.DRIVE;
					}
					break;
				case DRIVE:
					if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearState.DROPGEAR;
					}
					break;
				case DROPGEAR:
					if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
						nextState = autoShootCenterGearState.WAIT;
					}
					break;
				case WAIT:
					if (time <= 0) {
						nextState = autoShootCenterGearState.DRIVEBACK;
					}
					break;
				case DRIVEBACK:
					if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearState.DONE;
					}
					break;
				case DONE:
					break;
			}
			
			
		}
		public void doTransition() {
			if (currentState == autoShootCenterGearState.START && nextState == autoShootCenterGearState.TURNTURRET) {	
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
			if (currentState == autoShootCenterGearState.TURNTURRET && nextState == autoShootCenterGearState.SHOOT) {
				robot.shootRequest = true;
				time = 250; 
			}
			if (currentState == autoShootCenterGearState.SHOOT && nextState == autoShootCenterGearState.DRIVE) {
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
			if (currentState == autoShootCenterGearState.DRIVE && nextState == autoShootCenterGearState.DROPGEAR) {
				robot.tdt.setDriveTrainSpeed(0);
				robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
			}
			if (currentState == autoShootCenterGearState.DROPGEAR && nextState == autoShootCenterGearState.WAIT) {
				time = 15;
				robot.tdt.resetDriveEncoders();
				desiredDistance = -36;
				}
			if (currentState == autoShootCenterGearState.WAIT && nextState == autoShootCenterGearState.DRIVEBACK) {
				robot.tdt.setDriveTrainSpeed(-1);
			}
			if (currentState == autoShootCenterGearState.DRIVEBACK && nextState == autoShootCenterGearState.DONE) {
				robot.tdt.setDriveTrainSpeed(0);
				robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			}
		 }
	}