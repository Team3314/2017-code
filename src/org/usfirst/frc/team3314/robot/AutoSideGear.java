package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoSideGearStates {
	START,
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

public class AutoSideGear{
	autoSideGearStates currentState;
	autoSideGearStates nextState;
	double desiredDistance;
	Robot robot;
	double time = 0;
	
	public AutoSideGear(Robot myRobot) {
		robot = myRobot;
		currentState = autoSideGearStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoSideGearStates.START;
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
			nextState = autoSideGearStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoSideGearStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoSideGearStates.TURN;
			}
			break;
		case TURN:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 60) <= .25){
				nextState = autoSideGearStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoSideGearStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoSideGearStates.DONE;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kCloseGearIntake) {
				nextState = autoSideGearStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoSideGearStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos * Constants.kRevToInConvFactor <= desiredDistance) {
				nextState = autoSideGearStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoSideGearStates.START && nextState == autoSideGearStates.DRIVE1) {
			//robot drives straight forward at 1/2 speed, 3 sec
			if (robot.redRequest) {
				desiredDistance = 77.5;
			}
			if (robot.blueRequest) {
				desiredDistance = 77.5;
			}
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveTrainSpeed(0.5);
		}
		
		if (currentState == autoSideGearStates.DRIVE1 && nextState == autoSideGearStates.STOP1) {
			//stops robot, 1/2 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoSideGearStates.STOP1 && nextState == autoSideGearStates.TURN) {
			//robot turns right to angle of peg, 1.5 sec
			if (robot.redRequest) {
				robot.tdt.setDriveAngle(60);
				desiredDistance = 30;
			}
			else if (robot.blueRequest) {
				robot.tdt.setDriveAngle(-60);
				desiredDistance = 30;
			}
			time = 75;
		}
	
		if (currentState == autoSideGearStates.TURN && nextState == autoSideGearStates.DRIVE2) {
			//robot drives at 1/2 speed, 1 sec
			robot.tdt.setDriveTrainSpeed(.5);
			time = 50;
		}
		if (currentState == autoSideGearStates.DRIVE2 && nextState == autoSideGearStates.STOP2) {
			//robot stops
			robot.tdt.setDriveTrainSpeed(0);
		}
		
		if (currentState == autoSideGearStates.STOP2 && nextState == autoSideGearStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			time = 25;
		}
		if (currentState == autoSideGearStates.DROPGEAR && nextState == autoSideGearStates.WAIT) {
			if (robot.blueRequest) {
				desiredDistance = -25;
			}
			if (robot.redRequest) {
				desiredDistance = -25;
			}
		}
		if (currentState == autoSideGearStates.WAIT && nextState == autoSideGearStates.DRIVEBACK) {
			robot.tdt.rDriveTalon1.setPosition(0);
			robot.tdt.lDriveTalon1.setPosition(0);
			robot.tdt.setDriveTrainSpeed(-.5);
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
		}
		if (currentState == autoSideGearStates.DRIVEBACK && nextState == autoSideGearStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
	}
}