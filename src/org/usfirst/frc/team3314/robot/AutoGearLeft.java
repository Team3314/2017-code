package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGearLeftStates {
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

public class AutoGearLeft{
	autoGearLeftStates currentState;
	autoGearLeftStates nextState;
	double desiredDistance;
	double turnAngle;
	double turnBackAngle;
	Robot robot;
	double time = 0;
	
	public AutoGearLeft(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearLeftStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGearLeftStates.START;
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
			nextState = autoGearLeftStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearLeftStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGearLeftStates.TURN;
			}
			break;
		case TURN:
			if (robot.tdt.gyroControl.onTarget()){
				nextState = autoGearLeftStates.STOP2;
			}
			break;
		case STOP2:
			robot.tdt.resetDriveEncoders();
			if (time <= 0 ) {
				nextState = autoGearLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearLeftStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoGearLeftStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearLeftStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoGearLeftStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearLeftStates.STOP4;
			}
			break;
		case STOP4:
			if (time<= 0) {
				nextState = autoGearLeftStates.TURN2;
			}
			break;
		case TURN2:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoGearLeftStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGearLeftStates.START && nextState == autoGearLeftStates.DRIVE1) {
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
		if (currentState == autoGearLeftStates.DRIVE1 && nextState == autoGearLeftStates.STOP1) {
			//stops robot, 1/2 sec
			robot.navx.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		
		if (currentState == autoGearLeftStates.STOP1 && nextState == autoGearLeftStates.TURN) {
			//robot turns right to angle of peg, 1.5 sec
			robot.tdt.setDriveAngle(60);
			if (robot.blueRequest) {
				desiredDistance = 26;
			}
			if (robot.redRequest) {
				desiredDistance = 26;
			}
		}

		if (currentState == autoGearLeftStates.TURN && nextState == autoGearLeftStates.STOP2) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 12;
		}
		if (currentState == autoGearLeftStates.STOP2 && nextState == autoGearLeftStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(.5);	
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		
		if (currentState == autoGearLeftStates.DRIVE2 && nextState == autoGearLeftStates.STOP3) {
			//robot stops
			time = 7;
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearLeftStates.STOP3 && nextState == autoGearLeftStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		if (currentState == autoGearLeftStates.DROPGEAR && nextState == autoGearLeftStates.WAIT) {
			if (robot.blueRequest) {
				desiredDistance = -48;
			}
			if (robot.redRequest) {
				desiredDistance = -30;
			}
			time = 15;
		}
		if (currentState == autoGearLeftStates.WAIT && nextState == autoGearLeftStates.DRIVEBACK) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(-1);
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		if (currentState == autoGearLeftStates.DRIVEBACK && nextState == autoGearLeftStates.STOP4) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearLeftStates.STOP4 && nextState == autoGearLeftStates.TURN2) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			if (robot.blueRequest) {
				desiredDistance = 216;
				robot.tdt.setDriveAngle(20);
			}
			else if (robot.redRequest) {
				desiredDistance = 180;
				robot.tdt.setDriveAngle(0);
			}
			
		}
		if (currentState == autoGearLeftStates.TURN2 && nextState == autoGearLeftStates.DRIVE3) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(.5);
		}
		if (currentState == autoGearLeftStates.DRIVE3 && nextState == autoGearLeftStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		}
}