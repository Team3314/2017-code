package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoDTHRedStates {
	START,
	DRIVE1,
	STOP1,
	DRIVE2,
	STOP2,
	SHOOT,
	DONE
}

public class AutoDriveToHopperRight {
	autoDTHRedStates currentState;
	autoDTHRedStates nextState;
	Robot robot;
	double time = 0; //times are placeholder for now
	
	public AutoDriveToHopperRight(Robot myRobot) {
		robot = myRobot;
		currentState = autoDTHRedStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoDTHRedStates.START;
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
			nextState = autoDTHRedStates.DRIVE1;
			break;
		case DRIVE1:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (108*robot.encoderConversionFactor)){
			//enc pos is approx
				nextState = autoDTHRedStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoDTHRedStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (32*robot.encoderConversionFactor)){
			//enc pos is placeholder
				nextState = autoDTHRedStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoDTHRedStates.SHOOT;
			}
			break;
		case SHOOT:
			if (time <=0){
				nextState = autoDTHRedStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoDTHRedStates.START && nextState == autoDTHRedStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoDTHRedStates.DRIVE1 && nextState == autoDTHRedStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoDTHRedStates.STOP1 && nextState == autoDTHRedStates.DRIVE2) {
			//robot drives forward again at max speed but at angle of hopper, 1.5 sec
			robot.tdt.setDriveAngle(90);
			robot.tdt.setDriveTrainSpeed(1);
			time = 75;
		}
		
		if (currentState == autoDTHRedStates.DRIVE2 && nextState == autoDTHRedStates.STOP2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoDTHRedStates.STOP2 && nextState == autoDTHRedStates.SHOOT){
			//will shoot balls into the hopper
			time = 50;
		}
	}
}