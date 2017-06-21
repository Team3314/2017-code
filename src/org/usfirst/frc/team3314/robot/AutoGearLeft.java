package org.usfirst.frc.team3314.robot;



import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

enum autoGearLeftStates {
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

public class AutoGearLeft{
	autoGearLeftStates currentState;
	autoGearLeftStates nextState;
	double desiredDistance;
	double turnAngle;
	double turnBackAngle;
	Robot robot;
	double time = 0;
	
	public AutoGearLeft(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearLeftStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGearLeftStates.START;
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
			nextState = autoGearLeftStates.DRIVE1;
			break;
		case DRIVE1:
			//Stops robot when it has driven the desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearLeftStates.STOP1;
			}
			break;
		case STOP1:
			//Robot waits to make sure it has stopped completely before turning
			if (time <= 0 ){
				nextState = autoGearLeftStates.TURN;
			}
			break;
		case TURN:
			//Stops robot's turn when the angle is within the tolerance
			if (robot.tdt.gyroControl.onTarget()){
				nextState = autoGearLeftStates.STOP2;
			}
			break;
		case STOP2:
			//Waits so robot has stopped turning before it drives forward
			robot.tdt.resetDriveEncoders();
			if (time <= 0 ) {
				nextState = autoGearLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			//Stops robot once it drives desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearLeftStates.STOP3;
			}
			break;
		case STOP3:
			//Robot waits to make sure it has stopped completely before dropping gear
			if (time <=0){
				nextState = autoGearLeftStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			//Makes sure gear intake is open beore moving back
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearLeftStates.WAIT;
			}
			break;
		case WAIT:
			//Makes sure geat has finished falling from robot beofre moving backards.
			if (time <= 0) {
				nextState = autoGearLeftStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			//Stops robot when it drives desired distance backwards
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearLeftStates.STOP4;
			}
			break;
		case STOP4:
			//Waits to make sure robot is stopped before turning
			if (time<= 0) {
				nextState = autoGearLeftStates.TURN2;
			}
			break;
		case TURN2:
			//Stops robot's turn once it is inside the tolerance
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoGearLeftStates.DRIVE3;
			}
			break;
		case DRIVE3:
			//Drives the robot the desired distance across the field
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoGearLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGearLeftStates.START && nextState == autoGearLeftStates.DRIVE1) {
			//Drives robot straight forward at full speed
			if (robot.blueRequest) {
				desiredDistance = 88;
			}
			else if (robot.redRequest) {
				desiredDistance = 88;
			}
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveTrainSpeed(1);
		}
		if (currentState == autoGearLeftStates.DRIVE1 && nextState == autoGearLeftStates.STOP1) {
			//Stops robot, waits 2/5 of a second
			robot.navx.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 20;
		}
		
		if (currentState == autoGearLeftStates.STOP1 && nextState == autoGearLeftStates.TURN) {
			//robot turns right to angle of peg
			robot.tdt.setDriveAngle(60);
			//desired distance for next drive state is set
			if (robot.blueRequest) {
				desiredDistance = 26;
			}
			if (robot.redRequest) {
				desiredDistance = 26;
			}
		}

		if (currentState == autoGearLeftStates.TURN && nextState == autoGearLeftStates.STOP2) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(0);
			time = 12;
		}
		if (currentState == autoGearLeftStates.STOP2 && nextState == autoGearLeftStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(.5);	
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		
		if (currentState == autoGearLeftStates.DRIVE2 && nextState == autoGearLeftStates.STOP3) {
			time = 7;
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearLeftStates.STOP3 && nextState == autoGearLeftStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		if (currentState == autoGearLeftStates.DROPGEAR && nextState == autoGearLeftStates.WAIT) {
			if (robot.blueRequest) {
				desiredDistance = -48;
			}
			if (robot.redRequest) {
				desiredDistance = -30;
			}
			time = 15;
		}
		if (currentState == autoGearLeftStates.WAIT && nextState == autoGearLeftStates.DRIVEBACK) {
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveTrainSpeed(-1);
			robot.tdt.setDriveAngle(robot.navx.getYaw());
		}
		if (currentState == autoGearLeftStates.DRIVEBACK && nextState == autoGearLeftStates.STOP4) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		if (currentState == autoGearLeftStates.STOP4 && nextState == autoGearLeftStates.TURN2) {
			//Follows different path depending on which side of the field robot starts on to account for 
			//differences between red and blue side of field: Left side of airship is boiler side on blue while
			//It is the loading station side on red. In both cases the robot drives towards the loading station on the 
			//other side of the field
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			if (robot.blueRequest) {
				desiredDistance = 216;
				robot.tdt.setDriveAngle(20);
			}
			else if (robot.redRequest) {
				desiredDistance = 240;
				robot.tdt.setDriveAngle(0);
			}
			
		}
		if (currentState == autoGearLeftStates.TURN2 && nextState == autoGearLeftStates.DRIVE3) {
			robot.tdt.resetDriveEncoders();
			robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));
			robot.tdt.setDriveTrainSpeed(.5);
		}
		if (currentState == autoGearLeftStates.DRIVE3 && nextState == autoGearLeftStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
		}
}
