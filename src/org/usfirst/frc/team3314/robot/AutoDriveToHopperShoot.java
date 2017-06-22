package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoDriveToHopperShootStates {
	START,
	DRIVE1,
	STOP1,
	TURN,
	STOP2,
	DRIVE2,
	DRIVE3,
	SHOOT,
	DONE
	
}

public class AutoDriveToHopperShoot {
	autoDriveToHopperShootStates currentState;
	autoDriveToHopperShootStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
	
	
	public AutoDriveToHopperShoot(Robot myRobot) {
		robot = myRobot;
		currentState = autoDriveToHopperShootStates.START;
	}
	
	public void update() {
		//Sets different values for turret, shooter and cam based on which side of the field robot is starting on
		//Shooter is different distances from boiler on different sides of field
		calcNext();
		doTransition();
		currentState = nextState;//Moves state machine to next state
		time--;
		
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoDriveToHopperShootStates.START;
		
	}
	
	public void calcNext() {
		switch (currentState) {
			case START:
				nextState = autoDriveToHopperShootStates.DRIVE1;
				break;
			case DRIVE1:
				//Stops robot when it has driven the desired distance
				if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
					nextState = autoDriveToHopperShootStates.STOP1;
				}
				break;
			case STOP1:
				//Robot waits to make sure it has stopped completely before turning
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.TURN;
				}
				break;
			case TURN:
				//Stops robot's turn when the angle is within the tolerance
				if (robot.tdt.gyroControl.onTarget()) {
					nextState = autoDriveToHopperShootStates.STOP2;
				}
				break;
			case STOP2:
				//Waits so robot stops turning before driving 
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.DRIVE2;
				}
				break;
			case DRIVE2:
				//Waits for robot to reach desired distance before moving to next state
				if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
					nextState = autoDriveToHopperShootStates.DRIVE3;
				}
				break;
			case DRIVE3:
				//Drives the robot on a timer so it hits the hopper and is fully against the wall
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.SHOOT;
				}
				break;
			case SHOOT:
				if (time <= 0) {
					nextState = autoDriveToHopperShootStates.DONE;
				}
				break;
			case DONE:
				break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoDriveToHopperShootStates.START && nextState == autoDriveToHopperShootStates.DRIVE1 ) {
			//Sets different values for turret, shooter and cam based on which side of the field robot is starting on
			//Shooter is different distances from boiler on different sides of field
			if (robot.blueRequest) {
				robot.turret.desiredTarget = 7.2;
				desiredDistance = 70;
				robot.cam.desiredPosition = 1376;
				robot.shooter.desiredSpeed = 5450;
			}
			else if (robot.redRequest) {
				robot.turret.desiredTarget = 0.1;
				desiredDistance = 70;
				robot.cam.desiredPosition = 1400;
				robot.shooter.desiredSpeed = 4600;
			}
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(1);
			
		}
		if (currentState == autoDriveToHopperShootStates.DRIVE1 && nextState == autoDriveToHopperShootStates.STOP1 ) {
			//Robot stops and waits 1/5 of a second before turning
			robot.tdt.setDriveTrainSpeed(0);
			time = 10;
		}
		if (currentState == autoDriveToHopperShootStates.STOP1 && nextState == autoDriveToHopperShootStates.TURN ) {
			//Robot turns different directions because hopper is on different side of the robot on different
			//Sides of the field
			if (robot.blueRequest) {
				robot.tdt.setDriveAngle(90);
				desiredDistance = -30;
			}
			if (robot.redRequest) {
				robot.tdt.setDriveAngle(-90);
				desiredDistance = -30;
			}
		}
		if (currentState == autoDriveToHopperShootStates.TURN && nextState == autoDriveToHopperShootStates.STOP2 ) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		if (currentState == autoDriveToHopperShootStates.STOP2 && nextState == autoDriveToHopperShootStates.DRIVE2) {
			//Spins up shooter to decrease  time it takes to start shooting
			robot.tdt.setDriveTrainSpeed(-.5);	
			robot.hal.shooterTalon.set(robot.shooter.desiredSpeed);
		}
		if (currentState == autoDriveToHopperShootStates.DRIVE2 && nextState == autoDriveToHopperShootStates.DRIVE3) {
			//Switches to tank drive mode so that robot will not try to mantain angle when hitting the hopper, which 
			//Changes the angle of the robot. This assumed angle is used for the shooter values
			time = 50;
			robot.tdt.setDriveMode(driveMode.TANK);
			robot.tdt.setStickInputs(.5, .5);
		}
		if (currentState == autoDriveToHopperShootStates.DRIVE3 && nextState == autoDriveToHopperShootStates.SHOOT ) {
			time = 1000; // Shoots for remainder of auto period
			robot.tdt.setStickInputs(0, 0);
			robot.shootRequest = true;
		}
		if (currentState == autoDriveToHopperShootStates.SHOOT && nextState == autoDriveToHopperShootStates.DONE ) {
			
		}
	}

}
