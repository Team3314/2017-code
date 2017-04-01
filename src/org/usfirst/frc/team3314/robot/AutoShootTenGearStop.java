package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoShootTenGearStopStates {
	START,
	TURNTURRET,
	SHOOT,
	DRIVE1,
	STOP1,
	TURN,
	STOP2,
	DRIVE2,
	STOP3,
	DONE
}

public class AutoShootTenGearStop {
	autoShootTenGearStopStates currentState;
	autoShootTenGearStopStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
	
public AutoShootTenGearStop(Robot myRobot) {
	robot = myRobot;
	currentState = autoShootTenGearStopStates.START;
	time = 0;
}

public void update() {
	calcNext();
	doTransition();
	currentState = nextState;
	time--;
}

public void reset() {
	currentState = autoShootTenGearStopStates.START;
}

public void calcNext() {
	nextState = currentState;
	switch (currentState) {
		case START:
			nextState = autoShootTenGearStopStates.TURNTURRET;
			break;
		case TURNTURRET:
			SmartDashboard.putNumber("Turret Error", robot.hal.turretTalon.getClosedLoopError());
			SmartDashboard.putNumber("Turret Position", robot.hal.turretTalon.getPosition());
			if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 250
			&& robot.hal.camTalon.getClosedLoopError() <= 50)  {
				nextState = autoShootTenGearStopStates.SHOOT;
			}
			break;
		case SHOOT:
			if (time <= 0) {
				nextState = autoShootTenGearStopStates.DRIVE1;
			}
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearStopStates.STOP1;
			}
			break;
		case STOP1:
			if (time <=0) {
				nextState = autoShootTenGearStopStates.TURN;
			}
			break;
		case TURN:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoShootTenGearStopStates.STOP2;
			}
			break;
		case STOP2:
			if (time <= 0) {
				nextState = autoShootTenGearStopStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearStopStates.STOP3;
			}
			break;
		case STOP3:
			if (time<= 0 ){
					nextState = autoShootTenGearStopStates.DONE;
			}
			break;
		case DONE:
			break;
	}
}

public void doTransition() {
	if (currentState == autoShootTenGearStopStates.START && nextState == autoShootTenGearStopStates.TURNTURRET) {
		robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			
		if (robot.blueRequest) {
			robot.shooter.desiredSpeed = 3370;
			robot.cam.desiredPosition = .265625;		
			robot.turret.desiredTarget = .1;
			desiredDistance = 90;
		}
		else if (robot.redRequest) {
			robot.shooter.desiredSpeed = 4200;
			robot.cam.desiredPosition = .2578125;		
			robot.turret.desiredTarget = 7.2;

			desiredDistance = 90;
		}
		robot.hal.shooterTalon.set(robot.shooter.desiredSpeed);
	}
	if (currentState == autoShootTenGearStopStates.TURNTURRET && nextState == autoShootTenGearStopStates.SHOOT) {
		robot.shootRequest = true;
		time = 225; 
	}
	if (currentState == autoShootTenGearStopStates.SHOOT && nextState == autoShootTenGearStopStates.DRIVE1) {
		robot.shootRequest = false;
		robot.tdt.setDriveMode(driveMode.GYROLOCK);
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveAngle(0);
		robot.tdt.setDriveTrainSpeed(1);
	}
	if (currentState == autoShootTenGearStopStates.DRIVE1 && nextState == autoShootTenGearStopStates.STOP1) {
		//stops robot, 1/2 sec
		robot.navx.reset();
		robot.tdt.setDriveTrainSpeed(0);
		time = 20;
	}
	
	if (currentState == autoShootTenGearStopStates.STOP1 && nextState == autoShootTenGearStopStates.TURN) {
		//robot turns right to angle of peg, 1.5 sec
		if (robot.blueRequest) {
			desiredDistance = 25;
			robot.tdt.setDriveAngle(60);
		}
		if (robot.redRequest) {
			desiredDistance = 25;
			robot.tdt.setDriveAngle(-60);
		}
	}

	if (currentState == autoShootTenGearStopStates.TURN && nextState == autoShootTenGearStopStates.STOP2) {
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(0);
		time = 12;
	}
	if (currentState == autoShootTenGearStopStates.STOP2 && nextState == autoShootTenGearStopStates.DRIVE2) {
		robot.tdt.setDriveTrainSpeed(.5);	
	}
	
	if (currentState == autoShootTenGearStopStates.DRIVE2 && nextState == autoShootTenGearStopStates.STOP3) {
		//robot stops
		time = 13;
		robot.tdt.setDriveTrainSpeed(0);
	}
	if (currentState == autoShootTenGearStopStates.STOP3 && nextState == autoShootTenGearStopStates.DONE) {
		robot.tdt.setDriveTrainSpeed(0);
	}
	
}
}