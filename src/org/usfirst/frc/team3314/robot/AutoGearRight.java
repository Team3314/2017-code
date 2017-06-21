package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGearRightStates {
	START,
	DRIVE1,
	STOP1,
	TURN,
	STOP2,
	DRIVE2,
	STOP3,
	DROPGEAR,
	WAIT,
	DRIVEBACK,
	STOP4,
	TURN2,
	DRIVE3,
	DONE
}

public class AutoGearRight{
	autoGearRightStates currentState;
	autoGearRightStates nextState;
	double desiredDistance;
	double turnAngle;
	double turnBackAngle;
	Robot robot;
	double time = 0;
	
	public AutoGearRight(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearRightStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGearRightStates.START;
	}
	
	public void update() {
		//Checks whether requirements to go to next state are fulfilled and switches states if so,
		//executes code assigned to each state every 20ms
		calcNext();
		doTransition();
		currentState = nextState;//Moves state machine to next state
		time --;
		SmartDashboard.putString("Auto state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			nextState = autoGearRightStates.DRIVE1;
			break;
		case DRIVE1:
			//Stops robot when it has driven the desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearRightStates.STOP1;
			}
			break;
		case STOP1:
			//Robot waits to make sure it has stopped completely before turning
			if (time <= 0 ){
				nextState = autoGearRightStates.TURN;
			}
			break;
		case TURN:
			//Stops robot's turn when the angle is within the tolerance
			if (robot.tdt.gyroControl.onTarget()){
				nextState = autoGearRightStates.STOP2;
			}
			break;
		case STOP2:
			//Waits so robot has stopped turning before it drives forward
			if (time <= 0 ) {
				nextState = autoGearRightStates.DRIVE2;
			}
			break;
		case DRIVE2:
			//Stops robot once it drives desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearRightStates.STOP3;
			}
			break;
		case STOP3:
			//Robot waits to make sure it has stopped completely before dropping gear
			if (time <=0){
				nextState = autoGearRightStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			//Makes sure gear intake is open beore moving back
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearRightStates.WAIT;
			}
			break;
		case WAIT:
			//Makes sure geat has finished falling from robot beofre moving backards.
			if (time <= 0) {
				nextState = autoGearRightStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			//Stops robot when it drives desired distance backwards
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearRightStates.STOP4;
			}
			break;
		case STOP4:
			//Waits to make sure robot is stopped before turning
			if (time<= 0) {
				nextState = autoGearRightStates.TURN2;
			}
			break;
		case TURN2:
			//Stops robot's turn once it is inside the tolerance
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoGearRightStates.DRIVE3;
			}
			break;
		case DRIVE3:
			//Drives the robot the desired distance across the field
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearRightStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGearRightStates.START && nextState == autoGearRightStates.DRIVE1) {
			//Drives robot straight forward at full speed
			if (robot.blueRequest) {
				desiredDistance = 88;
			}
			else if (robot.redRequest) {
				desiredDistance = 88;
			}
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveTrainSpeed(1);
		}
		if (currentState == autoGearRightStates.DRIVE1 && nextState == autoGearRightStates.STOP1) {
			//Stops robot and resets gyro before turning
			robot.navx.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		
		if (currentState == autoGearRightStates.STOP1 && nextState == autoGearRightStates.TURN) {
			//Robot turns right to angle of peg
			robot.tdt.setDriveAngle(-60);
			//desired distance for next drive state is set
			if (robot.blueRequest) {
				desiredDistance = 26;
			}
			if (robot.redRequest) {
				desiredDistance = 26;
			}
		}

		if (currentState == autoGearRightStates.TURN && nextState == autoGearRightStates.STOP2) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 12;
		}
		if (currentState == autoGearRightStates.STOP2 && nextState == autoGearRightStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(.5);	
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		
		if (currentState == autoGearRightStates.DRIVE2 && nextState == autoGearRightStates.STOP3) {
			time = 13;
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearRightStates.STOP3 && nextState == autoGearRightStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		if (currentState == autoGearRightStates.DROPGEAR && nextState == autoGearRightStates.WAIT) {
			if (robot.blueRequest) {
				desiredDistance = -48;
			}
			if (robot.redRequest) {
				desiredDistance = -48;
			}
			time = 15;
		}
		if (currentState == autoGearRightStates.WAIT && nextState == autoGearRightStates.DRIVEBACK) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(-1);
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		if (currentState == autoGearRightStates.DRIVEBACK && nextState == autoGearRightStates.STOP4) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearRightStates.STOP4 && nextState == autoGearRightStates.TURN2) {
			//Follows different path depending on which side of the field robot starts on to account for 
			//differences between red and blue side of field: Right side of airship is loading station side on blue while
			//It is the boiler side on red. In both cases the robot drives towards the loading station on the other side
			//of the field
			if (robot.blueRequest) {
				desiredDistance = 180;
				robot.tdt.setDriveAngle(0);
			}
			else if (robot.redRequest) {
				desiredDistance = 216;
				robot.tdt.setDriveAngle(-20);
			}
			
		}
		if (currentState == autoGearRightStates.TURN2 && nextState == autoGearRightStates.DRIVE3) {
			robot.tdt.resetDriveEncoders();
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear)); //Shifts up for extra speed
			robot.tdt.setDriveTrainSpeed(.5);
		}
		if (currentState == autoGearRightStates.DRIVE3 && nextState == autoGearRightStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		}
}
