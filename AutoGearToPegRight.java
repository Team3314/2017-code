package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGTPRightStates {
	START,
	DRIVE1,
	STOP1,
	TURN,
	DRIVE2,
	STOP2,
	RETRACT,
	DONE
}

public class AutoGearToPegRight {
	autoGTPRightStates currentState;
	autoGTPRightStates nextState;
	Robot robot;
	double time = 0; //times are placeholder for now
	
	public AutoGearToPegRight(Robot myRobot) {
		robot = myRobot;
		currentState = autoGTPRightStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGTPRightStates.START;
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
			robot.ahrs.reset();
			nextState = autoGTPRightStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos > (75.25*Constants.kEncConvFactor)){
				nextState = autoGTPRightStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGTPRightStates.TURN;
			}
			break;
		case TURN:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 60) <= .25){
				nextState = autoGTPRightStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos > (20*Constants.kEncConvFactor)){
				nextState = autoGTPRightStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoGTPRightStates.RETRACT;
			}
			break;
		case RETRACT:
			if (time <=0 && robot.hal.gearIntake.get().toString() == Constants.kRetractGearIntake){
				nextState = autoGTPRightStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGTPRightStates.START && nextState == autoGTPRightStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoGTPRightStates.DRIVE1 && nextState == autoGTPRightStates.STOP1) {
			//stops robot, 1/2 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoGTPRightStates.STOP1 && nextState == autoGTPRightStates.TURN) {

			//robot drives forward again at max speed but at angle of peg, 1.5 sec
			robot.tdt.setDriveAngle(-60); //or 66.36
			time = 75;
		}
	
		if (currentState == autoGTPRightStates.TURN && nextState == autoGTPRightStates.DRIVE2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(1);
			time = 50;
		}
		if (currentState == autoGTPRightStates.DRIVE2 && nextState == autoGTPRightStates.STOP2) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		
		if (currentState == autoGTPRightStates.STOP2 && nextState == autoGTPRightStates.RETRACT){
			//retracts gear intake, 1 sec
			robot.hal.gearIntake.set(Value.valueOf(Constants.kRetractGearIntake));
			time = 50;
		}
	}
}