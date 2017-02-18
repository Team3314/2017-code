package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
/** NOT FUNCTIONAL 
 * THE HOPPER IS ON THE OTHER SIDE
 * @author 3314Programming
 *
 */

enum autoGHLeftStates {
	START,
	DRIVE1,
	STOP1,
	TURN1,
	DRIVE2,
	STOP2,
	DRIVEBACK,
	STOP3,
	TURN2,
	DRIVE4,
	STOP4,
	DONE
}

public class AutoGearHopperLeft {
	autoGHLeftStates currentState;
	autoGHLeftStates nextState;
	Robot robot;
	double time = 0;
	
	public AutoGearHopperLeft(Robot myRobot) {
		robot = myRobot;
		currentState = autoGHLeftStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGHLeftStates.START;
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
			robot.ahrs.reset();
			nextState = autoGHLeftStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos > (75.25*Constants.kEncConvFactor)){
				nextState = autoGHLeftStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGHLeftStates.TURN1;
			}
			break;
		case TURN1:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 60) <= .25) {
				nextState = autoGHLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos > (20*Constants.kEncConvFactor)){
				nextState = autoGHLeftStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoGHLeftStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos > (-20*Constants.kEncConvFactor)){
				//enc pos is placeholder, placeholder distance
				nextState = autoGHLeftStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoGHLeftStates.TURN2;
			}
			break;
		case TURN2:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 30) <= .25) {
				nextState = autoGHLeftStates.DRIVE4;
			}
			break;
		case DRIVE4:
			if ((robot.tdt.detectCollision() > Constants.kCollisionThreshold_DeltaG)){
				//enc pos is placeholder, placeholer distance
				nextState = autoGHLeftStates.STOP4;
			}
			break;
		case STOP4:
			if (time <=0){
				nextState = autoGHLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGHLeftStates.START && nextState == autoGHLeftStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoGHLeftStates.DRIVE1 && nextState == autoGHLeftStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoGHLeftStates.STOP1 && nextState == autoGHLeftStates.TURN1) {
			//robot drives forward again at max speed but at angle of hopper, 1.5 sec
			robot.ahrs.reset();
			robot.tdt.setDriveAngle(-60);
			time = 75;
		}
		
		if(currentState == autoGHLeftStates.TURN1 && nextState == autoGHLeftStates.DRIVE2) {
			robot.tdt.setDriveTrainSpeed(1);
		}
		
		if (currentState == autoGHLeftStates.DRIVE2 && nextState == autoGHLeftStates.STOP2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			robot.hal.gearIntake.set(Value.valueOf(Constants.kRetractGearIntake));
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.STOP2 && nextState == autoGHLeftStates.DRIVEBACK){
			//placeholder angle, robot drives forward at fullspeed, 1 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(-1);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.DRIVEBACK && nextState == autoGHLeftStates.STOP3) {
			//stops robot again, 1 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.STOP3 && nextState == autoGHLeftStates.TURN2){
			//place holder angle, robot drives forward at fullspeed, 1 sec
			robot.tdt.setDriveAngle(-30);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.TURN2 && nextState == autoGHLeftStates.DRIVE4) {
			robot.tdt.setDriveTrainSpeed(1);
		}
		
		if (currentState == autoGHLeftStates.DRIVE4 && nextState == autoGHLeftStates.STOP4) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
	}
}