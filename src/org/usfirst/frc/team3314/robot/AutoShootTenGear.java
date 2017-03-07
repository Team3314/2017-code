package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoShootTenGearStates {
		START,
		TURNTURRET,
		SHOOT,
		DRIVE1,
		STOP1,
		TURN,
		DRIVE2,
		STOP2,
		DROPGEAR,
		WAIT,
		DRIVEBACK,
		DONE
	}

public class AutoShootTenGear {
		autoShootTenGearStates currentState;
		autoShootTenGearStates nextState;
		Robot robot;
		int time;
		double desiredDistance;
		
	public AutoShootTenGear(Robot myRobot) {
		robot = myRobot;
		currentState = autoShootTenGearStates.START;
		time = 0;
		desiredDistance = 77.5;
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time--;
	}
	
	public void reset() {
		currentState = autoShootTenGearStates.START;
	}
	
	public void calcNext() {
		nextState = currentState;
		switch (currentState) {
			case START:
				nextState = autoShootTenGearStates.TURNTURRET;
				break;
			case TURNTURRET:
				if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 50 && robot.cam.calibrated)  {
					nextState = autoShootTenGearStates.SHOOT;
				}
				break;
			case SHOOT:
				if (time <= 0) {
					nextState = autoShootTenGearStates.DRIVE1;
				}
				break;
			case DRIVE1:
				if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)) {
					nextState = autoShootTenGearStates.STOP1;
				}
				break;
			case STOP1:
				if (time <=0) {
					nextState = autoShootTenGearStates.TURN;
				}
				break;
			case TURN:
				if (robot.tdt.gyroControl.onTarget()) {
					nextState = autoShootTenGearStates.DRIVE2;
				}
				break;
			case DRIVE2:
				if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)) {
					nextState = autoShootTenGearStates.STOP2;
				}
				break;
			case STOP2:
				if (time<= 0 ){
						nextState = autoShootTenGearStates.DROPGEAR;
				}
			case DROPGEAR:
				if (robot.hal.gearIntake.get().toString() == Constants.kRaiseGearIntake) {
					nextState = autoShootTenGearStates.WAIT;
				}
				break;
			case WAIT:
				if (time <= 0) {
					nextState = autoShootTenGearStates.DRIVEBACK;
				}
				break;
			case DRIVEBACK:
				if (robot.tdt.avgEncPos * Constants.kRevToInConvFactor <= desiredDistance) {
					nextState = autoShootTenGearStates.DONE;
				}
				break;
			case DONE:
				break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoShootTenGearStates.START && nextState == autoShootTenGearStates.TURNTURRET) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kDropGearIntake));
			robot.shooter.desiredSpeed = 3500;
			robot.cam.desiredPosition = .3555;			
			
			if (robot.blueRequest) {
				robot.turret.desiredTarget = .1;
			}
			else if (robot.redRequest) {
				robot.turret.desiredTarget = 7;
			}
		}
		if (currentState == autoShootTenGearStates.TURNTURRET && nextState == autoShootTenGearStates.SHOOT) {
			robot.shootRequest = true;
			time = 250; 
		}
		if (currentState == autoShootTenGearStates.SHOOT && nextState == autoShootTenGearStates.DRIVE1) {
			robot.shootRequest = false;
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.rDriveTalon1.setPosition(0);
			robot.tdt.lDriveTalon1.setPosition(0);
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveTrainSpeed(0.5);
		}
		if (currentState == autoShootTenGearStates.DRIVE1 && nextState == autoShootTenGearStates.STOP1) {
			//stops robot, 1/2 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoShootTenGearStates.STOP1 && nextState == autoShootTenGearStates.TURN) {
			//robot turns right to angle of peg, 1.5 sec
			if (robot.blueRequest) {
				robot.tdt.setDriveAngle(60);
			}
			if (robot.redRequest) {
				robot.tdt.setDriveAngle(-60);
			}
			robot.tdt.rDriveTalon1.setPosition(0);
			robot.tdt.lDriveTalon1.setPosition(0);
			desiredDistance = 30;
			time = 75;
		}
	
		if (currentState == autoShootTenGearStates.TURN && nextState == autoShootTenGearStates.DRIVE2) {
			//robot drives at 1/2 speed, 1 sec
			robot.tdt.setDriveTrainSpeed(.5);
			time = 50;
		}
		if (currentState == autoShootTenGearStates.DRIVE2 && nextState == autoShootTenGearStates.STOP2) {
			//robot stops
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoShootTenGearStates.STOP2 && nextState == autoShootTenGearStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kRaiseGearIntake));
			time = 25;
		}
		if (currentState == autoShootTenGearStates.DROPGEAR && nextState == autoShootTenGearStates.WAIT) {
			desiredDistance = -25;
		}
		if (currentState == autoShootTenGearStates.WAIT && nextState == autoShootTenGearStates.DRIVEBACK) {
			robot.tdt.rDriveTalon1.setPosition(0);
			robot.tdt.lDriveTalon1.setPosition(0);
			robot.tdt.setDriveTrainSpeed(-.5);
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
		}
		if (currentState == autoShootTenGearStates.DRIVEBACK && nextState == autoShootTenGearStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		
	}
}