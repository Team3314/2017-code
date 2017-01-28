package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGTPLeftStates {
	START,
	DRIVE1,
	STOP1,
	DRIVE2,
	STOP2,
	RETRACT,
	DONE
}

public class AutoGearToPegLeft {
	autoGTPLeftStates currentState;
	autoGTPLeftStates nextState;
	Robot robot;
	double time = 0; //times are placeholder for now
	
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
			robot.hal.gyro.reset();
			nextState = autoGTPLeftStates.DRIVE1;
			break;
		case DRIVE1:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (75.25*robot.encoderConversionFactor)){
			//enc pos is approx
				nextState = autoGTPLeftStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGTPLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (20*robot.encoderConversionFactor)){
			//enc pos is placeholder
				nextState = autoGTPLeftStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoGTPLeftStates.RETRACT;
			}
			break;
		case RETRACT:
			if (time <=0 && robot.hal.gearIntake.get().toString() == "kReverse"){
				nextState = autoGTPLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGTPLeftStates.START && nextState == autoGTPLeftStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoGTPLeftStates.DRIVE1 && nextState == autoGTPLeftStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoGTPLeftStates.STOP1 && nextState == autoGTPLeftStates.DRIVE2) {
			//robot drives forward again at max speed but at angle of peg, 1.5 sec
			robot.tdt.setDriveAngle(23.64); //or 66.36
			robot.tdt.setDriveTrainSpeed(1);
			time = 75;
		}
	
		if (currentState == autoGTPLeftStates.DRIVE2 && nextState == autoGTPLeftStates.STOP2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoGTPLeftStates.STOP2 && nextState == autoGTPLeftStates.RETRACT){
			//retracts gear intake, 1 sec
			robot.hal.gearIntake.set(Value.kReverse);
			time = 50;
		}
	}
}