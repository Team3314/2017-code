package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoTestStates {
	//starts, turns reverse light on, turns motor foward, turns motor backwards, finishes
	START,
	SOLENOID,
	MOTORFORWARD,
	MOTORBACK,
	DONE
}

public class AutoTest {
	autoTestStates currentState;
	autoTestStates nextState;
	Robot robot;
	double time = 0;
	
	public AutoTest(Robot myRobot) {
		robot = myRobot;
		currentState = autoTestStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoTestStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Auto state", currentState.toString());
	}
	
	public void calcNext() {
		nextState = currentState;
		
		//all either go to next state immediately or after timer finishes
		switch (currentState) {
		case START:
			nextState = autoTestStates.SOLENOID;
			break;
		case SOLENOID:
			if (time <= 0 ){
				nextState = autoTestStates.MOTORFORWARD;
			}
			break;
		case MOTORFORWARD:
			if (time <= 0 ){
				nextState = autoTestStates.MOTORBACK;
			}
			break;
		case MOTORBACK:
			if (time <= 0 ){
				nextState = autoTestStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoTestStates.START && nextState == autoTestStates.SOLENOID) {	
			//pid + encoder tick test
			/*robot.tdt.lDriveTalon1.changeControlMode(TalonControlMode.Position); //default is PercentVbus
			robot.tdt.lDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			robot.tdt.lDriveTalon1.setPID(0.5, 0.001, 0, 0, 0, 0, 0);
			robot.tdt.lDriveTalon1.setPosition(0);
			robot.tdt.rDriveTalon1.changeControlMode(TalonControlMode.Position); //default is PercentVbus
			robot.tdt.rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			robot.tdt.rDriveTalon1.setPID(0.5, 0.001, 0, 0, 0, 0, 0);
			robot.tdt.rDriveTalon1.setPosition(0); */
			
			//turns reverse light on, 1/2 sec
			robot.hal.solenoid.set(Value.kReverse);
			time = 25;
		}
			
		if (currentState == autoTestStates.SOLENOID && nextState == autoTestStates.MOTORFORWARD) {
			//motors go forward at half speed in gyrolock, 2 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(0.5);
			time = 100;
		}
		
		if (currentState == autoTestStates.MOTORFORWARD && nextState == autoTestStates.MOTORBACK) {
			//motors go backwards at half speed, 2 sec
			robot.tdt.setDriveTrainSpeed(-0.5);
			time = 100;
		}
		
		if (currentState == autoTestStates.MOTORBACK && nextState == autoTestStates.DONE) {
			//turns motors and reverse light off, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			robot.hal.solenoid.set(Value.kOff);
			time = 25;
		}
	}
}