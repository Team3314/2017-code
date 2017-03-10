package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoSideGearDriveStates {
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
	DONE
}

public class AutoSideGearDrive{
	autoSideGearDriveStates currentState;
	autoSideGearDriveStates nextState;
	double desiredDistance;
	double turnAngle;
	double turnBackAngle;
	Robot robot;
	double time = 0;
	
	public AutoSideGearDrive(Robot myRobot) {
		robot = myRobot;
		currentState = autoSideGearDriveStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoSideGearDriveStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Auto state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			nextState = autoSideGearDriveStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoSideGearDriveStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoSideGearDriveStates.TURN;
			}
			break;
		case TURN:
			if (robot.tdt.gyroControl.onTarget()){
				nextState = autoSideGearDriveStates.STOP2;
			}
			break;
		case STOP2:
			if (time <= 0 ) {
				nextState = autoSideGearDriveStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoSideGearDriveStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoSideGearDriveStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kCloseGearIntake) {
				nextState = autoSideGearDriveStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoSideGearDriveStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoSideGearDriveStates.STOP4;
			}
			break;
		case STOP4:
			if (time<= 0) {
				nextState = autoSideGearDriveStates.TURN2;
			}
			break;
		case TURN2:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoSideGearDriveStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoSideGearDriveStates.DONE;
			}
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoSideGearDriveStates.START && nextState == autoSideGearDriveStates.DRIVE1) {
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveTrainSpeed(1);
		}
		if (currentState == autoSideGearDriveStates.DRIVE1 && nextState == autoSideGearDriveStates.STOP1) {
			//stops robot, 1/2 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		
		if (currentState == autoSideGearDriveStates.STOP1 && nextState == autoSideGearDriveStates.TURN) {
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

		if (currentState == autoSideGearDriveStates.TURN && nextState == autoSideGearDriveStates.STOP2) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 12;
		}
		if (currentState == autoSideGearDriveStates.STOP2 && nextState == autoSideGearDriveStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(.75);	
		}
		
		if (currentState == autoSideGearDriveStates.DRIVE2 && nextState == autoSideGearDriveStates.STOP3) {
			//robot stops
			time = 7;
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoSideGearDriveStates.STOP3 && nextState == autoSideGearDriveStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
		}
		if (currentState == autoSideGearDriveStates.DROPGEAR && nextState == autoSideGearDriveStates.WAIT) {
			if (robot.blueRequest) {
				desiredDistance = -30;
			}
			if (robot.redRequest) {
				desiredDistance = -30;
			}
			time = 50;
		}
		if (currentState == autoSideGearDriveStates.WAIT && nextState == autoSideGearDriveStates.DRIVEBACK) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(-1);
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
		}
		if (currentState == autoSideGearDriveStates.DRIVEBACK && nextState == autoSideGearDriveStates.STOP4) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoSideGearDriveStates.STOP4 && nextState == autoSideGearDriveStates.TURN2) {
			if (robot.blueRequest) {
				desiredDistance = 10;
			}
			else if (robot.redRequest) {
				desiredDistance = 10;
			}
			robot.tdt.setDriveAngle(0);
		}
		if (currentState == autoSideGearDriveStates.TURN2 && nextState == autoSideGearDriveStates.DRIVE3) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(1);
		}
		if (currentState == autoSideGearDriveStates.DRIVE3 && nextState == autoSideGearDriveStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		}
}