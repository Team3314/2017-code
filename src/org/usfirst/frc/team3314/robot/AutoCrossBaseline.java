package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoCrossBaselineStates {
	START,
	DRIVE,
	STOP,
	DONE
}

public class AutoCrossBaseline {
	autoCrossBaselineStates currentState;
	autoCrossBaselineStates nextState;
	Robot robot;
	double time = 0;
	double desiredDistance = 80;

	public AutoCrossBaseline(Robot myRobot) {
		robot = myRobot;
		currentState = autoCrossBaselineStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoCrossBaselineStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time --;

		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			robot.ahrs.reset();
			nextState = autoCrossBaselineStates.DRIVE;
			break;
		case DRIVE:
			if (robot.tdt.avgEncPos > (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoCrossBaselineStates.STOP;
			}
			break;
		case STOP:
			if (time <= 0){
				nextState = autoCrossBaselineStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoCrossBaselineStates.START && nextState == autoCrossBaselineStates.DRIVE) {
			//robot drives straight forward at max speed, 4 sec
			robot.tdt.resetDriveEncoders();
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(0.25);
		}
		
		if (currentState == autoCrossBaselineStates.DRIVE && nextState == autoCrossBaselineStates.STOP) {
			//stops robot, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
	}
}