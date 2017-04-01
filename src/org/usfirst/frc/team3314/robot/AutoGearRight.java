package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGearRightStates {
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

public class AutoGearRight{
	autoGearRightStates currentState;
	autoGearRightStates nextState;
	double desiredDistance;
	double turnAngle;
	double turnBackAngle;
	Robot robot;
	double time = 0;
	
	public AutoGearRight(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearRightStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGearRightStates.START;
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
			nextState = autoGearRightStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearRightStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGearRightStates.TURN;
			}
			break;
		case TURN:
			if (robot.tdt.gyroControl.onTarget()){
				nextState = autoGearRightStates.STOP2;
			}
			break;
		case STOP2:
			if (time <= 0 ) {
				nextState = autoGearRightStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearRightStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoGearRightStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearRightStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoGearRightStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearRightStates.STOP4;
			}
			break;
		case STOP4:
			if (time<= 0) {
				nextState = autoGearRightStates.TURN2;
			}
			break;
		case TURN2:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoGearRightStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearRightStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGearRightStates.START && nextState == autoGearRightStates.DRIVE1) {
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));
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
		if (currentState == autoGearRightStates.DRIVE1 && nextState == autoGearRightStates.STOP1) {
			//stops robot, 1/2 sec
			robot.navx.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		
		if (currentState == autoGearRightStates.STOP1 && nextState == autoGearRightStates.TURN) {
			//robot turns right to angle of peg, 1.5 sec
			robot.tdt.setDriveAngle(-60);
			if (robot.blueRequest) {
				desiredDistance = 26;
			}
			if (robot.redRequest) {
				desiredDistance = 26;
			}
		}

		if (currentState == autoGearRightStates.TURN && nextState == autoGearRightStates.STOP2) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 12;
		}
		if (currentState == autoGearRightStates.STOP2 && nextState == autoGearRightStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(.4);	
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		
		if (currentState == autoGearRightStates.DRIVE2 && nextState == autoGearRightStates.STOP3) {
			//robot stops
			time = 13;
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearRightStates.STOP3 && nextState == autoGearRightStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		if (currentState == autoGearRightStates.DROPGEAR && nextState == autoGearRightStates.WAIT) {
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));
			if (robot.blueRequest) {
				desiredDistance = -48;
			}
			if (robot.redRequest) {
				desiredDistance = -48;
			}
			time = 15;
		}
		if (currentState == autoGearRightStates.WAIT && nextState == autoGearRightStates.DRIVEBACK) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(-1);
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		if (currentState == autoGearRightStates.DRIVEBACK && nextState == autoGearRightStates.STOP4) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearRightStates.STOP4 && nextState == autoGearRightStates.TURN2) {
			if (robot.blueRequest) {
				desiredDistance = 180;
				robot.tdt.setDriveAngle(0);
			}
			else if (robot.redRequest) {
				desiredDistance = 216;
				robot.tdt.setDriveAngle(-20);
			}
			
		}
		if (currentState == autoGearRightStates.TURN2 && nextState == autoGearRightStates.DRIVE3) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(.75);
		}
		if (currentState == autoGearRightStates.DRIVE3 && nextState == autoGearRightStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		}
}