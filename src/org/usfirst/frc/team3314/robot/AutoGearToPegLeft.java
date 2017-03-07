package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGTPLeftStates {
	START,
	DRIVE1,
	STOP1,
	TURN,
	DRIVE2,
	STOP2,
	DONE
}

public class AutoGearToPegLeft {
	autoGTPLeftStates currentState;
	autoGTPLeftStates nextState;
	double desiredDistance = 77.5;
	Robot robot;
	double time = 0;
	
	public AutoGearToPegLeft(Robot myRobot) {
		robot = myRobot;
		currentState = autoGTPLeftStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGTPLeftStates.START;
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
			nextState = autoGTPLeftStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGTPLeftStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGTPLeftStates.TURN;
			}
			break;
		case TURN:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 60) <= .25){
				nextState = autoGTPLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos > (20*Constants.kInToRevConvFactor)){
				nextState = autoGTPLeftStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoGTPLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGTPLeftStates.START && nextState == autoGTPLeftStates.DRIVE1) {
			//robot drives straight forward at 1/2 speed, 3 sec
			robot.hal.gearIntake.set(Value.valueOf(Constants.kDropGearIntake));
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveTrainSpeed(0.5);
		}
		
		if (currentState == autoGTPLeftStates.DRIVE1 && nextState == autoGTPLeftStates.STOP1) {
			//stops robot, 1/2 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoGTPLeftStates.STOP1 && nextState == autoGTPLeftStates.TURN) {
			//robot turns right to angle of peg, 1.5 sec
			robot.tdt.setDriveAngle(60);
			desiredDistance = 30;
			time = 75;
		}
	
		if (currentState == autoGTPLeftStates.TURN && nextState == autoGTPLeftStates.DRIVE2) {
			//robot drives at 1/2 speed, 1 sec
			robot.tdt.setDriveTrainSpeed(.5);
			time = 50;
		}
		if (currentState == autoGTPLeftStates.DRIVE2 && nextState == autoGTPLeftStates.STOP2) {
			//robot stops
			robot.tdt.setDriveTrainSpeed(0);
		}
		
		if (currentState == autoGTPLeftStates.STOP2 && nextState == autoGTPLeftStates.DONE){
			time = 50;
		}
	}
}