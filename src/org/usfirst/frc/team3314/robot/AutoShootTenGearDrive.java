package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoShootTenGearDriveStates {
	START,
	TURNTURRET,
	SHOOT,
	DRIVE1,
	STOP1,
	TURN,
	STOP2,
	DRIVE2,
	STOP3,
	DROPGEAR,
	WAIT,
	DRIVEBACK,
	STOP4,
	TURN2,
	DRIVE3,
	DONE
}

public class AutoShootTenGearDrive {
	autoShootTenGearDriveStates currentState;
	autoShootTenGearDriveStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
	
public AutoShootTenGearDrive(Robot myRobot) {
	robot = myRobot;
	currentState = autoShootTenGearDriveStates.START;
	time = 0;
}

public void update() {
	calcNext();
	doTransition();
	currentState = nextState;
	time--;
}

public void reset() {
	currentState = autoShootTenGearDriveStates.START;
}

public void calcNext() {
	nextState = currentState;
	switch (currentState) {
		case START:
			nextState = autoShootTenGearDriveStates.TURNTURRET;
			break;
		case TURNTURRET:
			if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 75 && robot.cam.calibrated 
			&& robot.hal.adjustTalon.getClosedLoopError() <= 100 && robot.hal.adjustTalon.getSetpoint() == .265625)  {
				nextState = autoShootTenGearDriveStates.SHOOT;
			}
			break;
		case SHOOT:
			if (time <= 0) {
				nextState = autoShootTenGearDriveStates.DRIVE1;
			}
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.STOP1;
			}
			break;
		case STOP1:
			if (time <=0) {
				nextState = autoShootTenGearDriveStates.TURN;
			}
			break;
		case TURN:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoShootTenGearDriveStates.STOP2;
			}
			break;
		case STOP2:
			if (time <= 0) {
				nextState = autoShootTenGearDriveStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.STOP3;
			}
			break;
		case STOP3:
			if (time<= 0 ){
					nextState = autoShootTenGearDriveStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kCloseGearIntake) {
				nextState = autoShootTenGearDriveStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoShootTenGearDriveStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.STOP4;
			}
			break;
		case STOP4:
			if (time<= 0) {
				nextState = autoShootTenGearDriveStates.TURN2;
			}
			break;
		case TURN2:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoShootTenGearDriveStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.DONE;
			}
			break;
		case DONE:
			break;
	}
}

public void doTransition() {
	if (currentState == autoShootTenGearDriveStates.START && nextState == autoShootTenGearDriveStates.TURNTURRET) {
		robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
			
		if (robot.blueRequest) {
			robot.shooter.desiredSpeed = 3370;
			robot.cam.desiredPosition = .265625;		
			robot.turret.desiredTarget = 0;
			desiredDistance = 92;
		}
		else if (robot.redRequest) {
			robot.shooter.desiredSpeed = 3500;
			robot.cam.desiredPosition = .3555;		
			robot.turret.desiredTarget = 7.5;
			desiredDistance = 92;
		}
	}
	if (currentState == autoShootTenGearDriveStates.TURNTURRET && nextState == autoShootTenGearDriveStates.SHOOT) {
		robot.shootRequest = true;
		time = 250; 
	}
	if (currentState == autoShootTenGearDriveStates.SHOOT && nextState == autoShootTenGearDriveStates.DRIVE1) {
		robot.shootRequest = false;
		robot.tdt.setDriveMode(driveMode.GYROLOCK);
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveAngle(0);
		robot.tdt.setDriveTrainSpeed(1);
	}
	if (currentState == autoShootTenGearDriveStates.DRIVE1 && nextState == autoShootTenGearDriveStates.STOP1) {
		//stops robot, 1/2 sec
		robot.ahrs.reset();
		robot.tdt.setDriveTrainSpeed(0);
		time = 20;
	}
	
	if (currentState == autoShootTenGearDriveStates.STOP1 && nextState == autoShootTenGearDriveStates.TURN) {
		//robot turns right to angle of peg, 1.5 sec
		if (robot.blueRequest) {
			desiredDistance = 18;
			robot.tdt.setDriveAngle(60);
		}
		if (robot.redRequest) {
			desiredDistance = 18;
			robot.tdt.setDriveAngle(-60);
		}
	}

	if (currentState == autoShootTenGearDriveStates.TURN && nextState == autoShootTenGearDriveStates.STOP2) {
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(0);
		time = 12;
	}
	if (currentState == autoShootTenGearDriveStates.STOP2 && nextState == autoShootTenGearDriveStates.DRIVE2) {
		robot.tdt.setDriveTrainSpeed(.75);	
	}
	
	if (currentState == autoShootTenGearDriveStates.DRIVE2 && nextState == autoShootTenGearDriveStates.STOP3) {
		//robot stops
		time = 7;
		robot.tdt.setDriveTrainSpeed(0);
	}
	if (currentState == autoShootTenGearDriveStates.STOP3 && nextState == autoShootTenGearDriveStates.DROPGEAR) {
		robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
	}
	if (currentState == autoShootTenGearDriveStates.DROPGEAR && nextState == autoShootTenGearDriveStates.WAIT) {
		if (robot.blueRequest) {
			desiredDistance = -20;
		}
		if (robot.redRequest) {
			desiredDistance = -20;
		}
		time = 50;
	}
	if (currentState == autoShootTenGearDriveStates.WAIT && nextState == autoShootTenGearDriveStates.DRIVEBACK) {
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(-1);
		robot.tdt.setDriveAngle(robot.ahrs.getYaw());
	}
	if (currentState == autoShootTenGearDriveStates.DRIVEBACK && nextState == autoShootTenGearDriveStates.STOP4) {
		robot.tdt.setDriveTrainSpeed(0);
	}
	if (currentState == autoShootTenGearDriveStates.STOP4 && nextState == autoShootTenGearDriveStates.TURN2) {
		if (robot.blueRequest) {
			desiredDistance = 10; //180;
		}
		else if (robot.redRequest) {
			desiredDistance = 10; //180;
		}
		robot.tdt.setDriveAngle(0);
	}
	if (currentState == autoShootTenGearDriveStates.TURN2 && nextState == autoShootTenGearDriveStates.DRIVE3) {
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(1);
	}
	if (currentState == autoShootTenGearDriveStates.DRIVE3 && nextState == autoShootTenGearDriveStates.DONE) {
		robot.tdt.setDriveTrainSpeed(0);
	}
	
}
}