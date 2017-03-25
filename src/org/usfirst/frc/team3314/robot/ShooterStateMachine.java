package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum shooterStates {
	START,
	AGITATE,
	INDEX,
	SHOOT,
	STOP,
	DONE
}

public class ShooterStateMachine {
	shooterStates currentState;
	shooterStates nextState;
	Robot robot;
	double shooterSpeed = 0;
	double desiredSpeed = 3500;
	double time = 0;
	double sensorTime = 50;
	double rpm;
	
	public ShooterStateMachine(Robot myRobot) {
		robot = myRobot;
		currentState = shooterStates.START;
	}
	
	public void reset() {
		//sets shooter back to beginning
		currentState = shooterStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		shooterSpeed = robot.hal.shooterTalon.getSpeed();
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Shooter state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			if (robot.shootRequest) {
				nextState = shooterStates.AGITATE;
			}
			
			break;
			
		case AGITATE:
			if (!robot.shootRequest) {
				nextState = shooterStates.STOP;
			}
			if (/*robot.hal.agitatorSpark.get() == 1percentvbus placeholder  &&*/
			Math.abs(desiredSpeed + robot.hal.shooterTalon.getSpeed()) <= 250) /*rpm placeholder*/ {
				nextState = shooterStates.INDEX;
			}
			break;
			
		case INDEX:
			if (!robot.shootRequest) {
				nextState = shooterStates.STOP;
			}
			
			if (robot.hal.lowerIndexSpark.get() == 1 && robot.hal.upperIndexSpark.get() == 1 /*percentvbus placeholder*/) {
				nextState = shooterStates.SHOOT;
			}
			break;
			
		case SHOOT:
			if (!robot.shootRequest) {
				nextState = shooterStates.STOP;
			}
			break;
			
		case STOP:
			if (robot.hal.agitatorSpark.get() == 0 && robot.hal.lowerIndexSpark.get() == 0 && robot.hal.shooterTalon.get() == 0) {
				nextState = shooterStates.DONE;
			}
			break;
			
		case DONE:
			nextState = shooterStates.START;
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == shooterStates.START && nextState == shooterStates.AGITATE) {
			//agitator + shooter motors started, 1/5 sec
			robot.hal.shooterTalon.set(desiredSpeed);
			time = 10;
		}
		
		if (currentState == shooterStates.AGITATE && nextState == shooterStates.INDEX) {
			//index shooters started, 1/5 sec
			robot.hal.agitatorSpark.set(.75);
			robot.hal.lowerIndexSpark.set(1);
			robot.hal.upperIndexSpark.set(1);
			time = 10;
		}
		
		if (currentState == shooterStates.INDEX && nextState == shooterStates.SHOOT) {
			//prior code allows robot to shoot w/o code needed here
			//if button is let go, robot stop shooting after 1/5 sec
			time = 10;
		}
		
		if (currentState == shooterStates.AGITATE && nextState == shooterStates.STOP) {
			//all motors off
			robot.hal.agitatorSpark.set(0);
			robot.hal.shooterTalon.set(0);
			robot.hal.lowerIndexSpark.set(0);
			robot.hal.upperIndexSpark.set(0);
		}
		if (currentState == shooterStates.INDEX && nextState == shooterStates.STOP) {
				//all motors off
				robot.hal.agitatorSpark.set(0);
				robot.hal.shooterTalon.set(0);
				robot.hal.lowerIndexSpark.set(0);
				robot.hal.upperIndexSpark.set(0);
			}
		if (currentState == shooterStates.SHOOT 	&& nextState == shooterStates.STOP) {
				//all motors off
				robot.hal.agitatorSpark.set(0);
				robot.hal.shooterTalon.set(0);
				robot.hal.lowerIndexSpark.set(0);
				robot.hal.upperIndexSpark.set(0);
			}
	}
}