package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGearDriveToHopperShootStates {
	START,
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
	DRIVE4,
	SHOOT,
	DONE
	
}

public class AutoGearDriveToHopperShoot {
	autoGearDriveToHopperShootStates currentState;
	autoGearDriveToHopperShootStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
		
	public AutoGearDriveToHopperShoot(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearDriveToHopperShootStates.START;
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time--;
	}
	
	public void reset() {
		currentState = autoGearDriveToHopperShootStates.START;
	}

	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			nextState = autoGearDriveToHopperShootStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearDriveToHopperShootStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGearDriveToHopperShootStates.TURN;
			}
			break;
		case TURN:
			if (robot.tdt.gyroControl.onTarget()){
				nextState = autoGearDriveToHopperShootStates.STOP2;
			}
			break;
		case STOP2:
			robot.tdt.resetDriveEncoders();
			if (time <= 0 ) {
				nextState = autoGearDriveToHopperShootStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearDriveToHopperShootStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoGearDriveToHopperShootStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearDriveToHopperShootStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoGearDriveToHopperShootStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearDriveToHopperShootStates.STOP4;
			}
			break;
		case STOP4:
			if (time<= 0) {
				nextState = autoGearDriveToHopperShootStates.TURN2;
			}
			break;
		case TURN2:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoGearDriveToHopperShootStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearDriveToHopperShootStates.DRIVE4;
			}
			break;
		case DRIVE4:
			if (time <=0) {
				nextState = autoGearDriveToHopperShootStates.SHOOT;
			}
		case SHOOT:
			if (time <= 0) {
				nextState = autoGearDriveToHopperShootStates.DONE;
			}
		case DONE:
			break;
		}
	}
		public void doTransition() {
			if (currentState == autoGearDriveToHopperShootStates.START && nextState == autoGearDriveToHopperShootStates.DRIVE1) {
				if (robot.blueRequest) {
					desiredDistance = 88;
				}
				else if (robot.redRequest) {
					desiredDistance = 88;
				}
				robot.tdt.setDriveMode(driveMode.GYROLOCK);
				robot.tdt.resetDriveEncoders();
				robot.tdt.setDriveAngle(0);
				robot.tdt.setDriveTrainSpeed(1);
			}
			if (currentState == autoGearDriveToHopperShootStates.DRIVE1 && nextState == autoGearDriveToHopperShootStates.STOP1) {
				//stops robot, 1/2 sec
				robot.navx.reset();
				robot.tdt.setDriveTrainSpeed(0);
				time = 20;
			}
			
			if (currentState == autoGearDriveToHopperShootStates.STOP1 && nextState == autoGearDriveToHopperShootStates.TURN) {
				//robot turns right to angle of peg, 1.5 sec
				if (robot.blueRequest) {
					robot.tdt.setDriveAngle(60);
					desiredDistance = 18;
				}
				if (robot.redRequest) {
					robot.tdt.setDriveAngle(-60);
					desiredDistance = 18;
				}
			}

			if (currentState == autoGearDriveToHopperShootStates.TURN && nextState == autoGearDriveToHopperShootStates.STOP2) {
				robot.tdt.resetDriveEncoders();
				robot.tdt.setDriveTrainSpeed(0);
				time = 12;
			}
			if (currentState == autoGearDriveToHopperShootStates.STOP2 && nextState == autoGearDriveToHopperShootStates.DRIVE2) {
				robot.tdt.setDriveTrainSpeed(.5);	
			}
			
			if (currentState == autoGearDriveToHopperShootStates.DRIVE2 && nextState == autoGearDriveToHopperShootStates.STOP3) {
				//robot stops
				time = 7;
				robot.tdt.setDriveTrainSpeed(0);
			}
			if (currentState == autoGearDriveToHopperShootStates.STOP3 && nextState == autoGearDriveToHopperShootStates.DROPGEAR) {
				robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
			}
			if (currentState == autoGearDriveToHopperShootStates.DROPGEAR && nextState == autoGearDriveToHopperShootStates.WAIT) {
				if (robot.blueRequest) {
					desiredDistance = -18;
				}
				if (robot.redRequest) {
					desiredDistance = -18;
				}
				time = 15;
			}
			if (currentState == autoGearDriveToHopperShootStates.WAIT && nextState == autoGearDriveToHopperShootStates.DRIVEBACK) {
				robot.tdt.resetDriveEncoders();
				robot.tdt.setDriveTrainSpeed(-1);
				robot.tdt.setDriveAngle(robot.navx.getYaw());
			}
			if (currentState == autoGearDriveToHopperShootStates.DRIVEBACK && nextState == autoGearDriveToHopperShootStates.STOP4) {
				robot.tdt.setDriveTrainSpeed(0);
			}
			if (currentState == autoGearDriveToHopperShootStates.STOP4 && nextState == autoGearDriveToHopperShootStates.TURN2) {
				if (robot.blueRequest) {
					robot.tdt.setDriveAngle(90);
					desiredDistance = 180;
				}
				else if (robot.redRequest) {
					robot.tdt.setDriveAngle(-90);
					desiredDistance = 180;
				}
				
			}
			if (currentState == autoGearDriveToHopperShootStates.TURN2 && nextState == autoGearDriveToHopperShootStates.DRIVE3) {
				robot.tdt.resetDriveEncoders();
				robot.tdt.setDriveTrainSpeed(-.5);	
				robot.hal.shooterTalon.set(robot.shooter.desiredSpeed);
			}
			if (currentState == autoGearDriveToHopperShootStates.DRIVE3 && nextState == autoGearDriveToHopperShootStates.DRIVE4) {
				time = 25;
				robot.tdt.setDriveMode(driveMode.TANK);
				robot.tdt.setStickInputs(.5, .5);
			}
			if (currentState == autoGearDriveToHopperShootStates.DRIVE4 && nextState == autoGearDriveToHopperShootStates.SHOOT) {
				time = 1000;
				robot.tdt.setStickInputs(0, 0);
				robot.shootRequest = true;
			}
			if (currentState == autoGearDriveToHopperShootStates.SHOOT && nextState == autoGearDriveToHopperShootStates.DONE) {
				
			}
		}		
}
