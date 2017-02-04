package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum shooterStates {
	START,
	AGITATE,
	INDEX,
	SHOOT,
	REVERSE_AGITATE
}

public class ShooterStateMachine {
	shooterStates currentState;
	shooterStates nextState;
	Robot robot;
	double time = 0;
	double sensorTime = 50;
	
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
			nextState = shooterStates.AGITATE;
			break;
		case AGITATE:
			if (robot.hal.agitatorSpark1.get() == 1 && robot.hal.agitatorSpark2.get() == 1 /*percentvbus placeholders*/  &&
			robot.hal.shooterTalon.getEncVelocity() == Constants.kShooter_TargetRPM /*rpm placeholder*/) {
				nextState = shooterStates.INDEX;
			}
			break;
		case INDEX:
			if (robot.hal.indexSpark.get() == 1 /*percentvbus placeholder*/) {
				nextState = shooterStates.SHOOT;
			}
			break;
		case SHOOT:
			if (time <= 0 && robot.hal.agitatorSpark1.get() == -1 && robot.hal.agitatorSpark2.get() == -1) {
				nextState = shooterStates.REVERSE_AGITATE;
			}
			break;
		case REVERSE_AGITATE:
			if (robot.hal.agitatorSpark1.get() == 1 && robot.hal.agitatorSpark2.get() == 1) {
				nextState = shooterStates.SHOOT;
			}
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == shooterStates.START && nextState == shooterStates.AGITATE) {
			robot.hal.agitatorSpark1.set(1);
			robot.hal.agitatorSpark2.set(1);
			robot.hal.shooterTalon.set(Constants.kShooter_TargetRPM);
		}
		
		if (currentState == shooterStates.AGITATE && nextState == shooterStates.INDEX) {
			robot.hal.indexSpark.set(1);
		}
		
		if (currentState == shooterStates.INDEX || currentState == shooterStates.REVERSE_AGITATE &&
		nextState == shooterStates.SHOOT) {
			if (robot.hal.indexSensor.getVoltage() < Constants.kShooter_IndexSensorThreshold) {
				sensorTime = 50;
			}
			else {
				sensorTime --;
			}
			
			if (sensorTime <= 0) {
			robot.hal.agitatorSpark1.set(-1);
			robot.hal.agitatorSpark2.set(-1);
			time = 50;
			}
		}
		
		if (currentState == shooterStates.SHOOT && nextState == shooterStates.REVERSE_AGITATE) {
			robot.hal.agitatorSpark1.set(1);
			robot.hal.agitatorSpark2.set(1);
		}
	}
	
	public void stopShoot() {
		time = 50;
		time --;
  		robot.hal.indexSpark.set(-1);
  		
		//if (robot.hal.agitatorSpark1.get() != 0 || robot.hal.agitatorSpark2.get() != 0 || robot.hal.indexSpark.get() != 0 ||
		//robot.hal.shooterTalon.get() != 0) {
		//	time --;
		//}
		
		if (time <= 0) {
			robot.hal.agitatorSpark1.set(0);
			robot.hal.agitatorSpark2.set(0);
			robot.hal.indexSpark.set(0);
			robot.hal.shooterTalon.set(0);
		}
	}
}