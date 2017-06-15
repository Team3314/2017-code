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
		//Checks whether requirements to go to next state are fulfilled and switches states if so,
		//executes code assigned to each state every 20ms
		calcNext();
		doTransition();
		currentState = nextState; //Moves state machine to next state
		time --;

		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		//Resets gyro before driving
		case START:
			robot.navx.reset();
			nextState = autoCrossBaselineStates.DRIVE;
			break;
		case DRIVE:
			//Stops robot when it has driven the desired distance
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
			//Resets drive encoders and robot drives straight forward at max speed
			robot.tdt.resetDriveEncoders();
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(0.25);
		}
		
		if (currentState == autoCrossBaselineStates.DRIVE && nextState == autoCrossBaselineStates.STOP) {
			//Stops robot
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
	}
}
