package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoGearToPegStates {
	START,
	DRIVE,
	STOP,
	RETRACT,
	DONE
}

public class AutoGearToPeg {
	autoGearToPegStates currentState;
	autoGearToPegStates nextState;
	Robot robot;
	double time = 0;
	
	public AutoGearToPeg(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearToPegStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGearToPegStates.START;
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
			nextState = autoGearToPegStates.DRIVE;
			break;
		case DRIVE:
			if (robot.tdt.avgEncPos > (60*Constants.kEncConvFactor)){
				nextState = autoGearToPegStates.STOP;
			}
			break;
		case STOP:
			if (time <=0){
				nextState = autoGearToPegStates.RETRACT;
			}
			break;
		case RETRACT:
			if (time <=0 && robot.hal.gearIntake.get().toString() == Constants.kRetractGearIntake) {
				nextState = autoGearToPegStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGearToPegStates.START && nextState == autoGearToPegStates.DRIVE) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
	
		if (currentState == autoGearToPegStates.DRIVE && nextState == autoGearToPegStates.STOP) {
			//stops robot, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoGearToPegStates.STOP && nextState == autoGearToPegStates.RETRACT) {
			//retracts gear intake, 1 sec
			robot.hal.gearIntake.set(Value.valueOf(Constants.kRetractGearIntake));
			time = 50;
		}
	}
}