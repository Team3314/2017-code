package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoShootTenGearDriveStates {
	START,
	TURNTURRET,
	SHOOT,
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
	SCURVE,
	DONE
}

public class AutoShootTenGearDrive {
	autoShootTenGearDriveStates currentState;
	autoShootTenGearDriveStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
	double desiredAngle = 0;
	
public AutoShootTenGearDrive(Robot myRobot) {
	robot = myRobot;
	currentState = autoShootTenGearDriveStates.START;
	time = 0;
}

public void update() {
	//Checks whether requirements to go to next state are fulfilled and switches states if so,
	//executes code assigned to each state every 20ms
	calcNext();
	doTransition();
	currentState = nextState;//Moves state machine to next state
	time--;
}

public void reset() {
	//sets auto back to beginning
	currentState = autoShootTenGearDriveStates.START;
}

public void calcNext() {
	nextState = currentState;
	switch (currentState) {
		case START:
			nextState = autoShootTenGearDriveStates.TURNTURRET;
			break;
		case TURNTURRET:
			//Makes sure turret is turned to target positon before advancing
			//Also contains a timeout to advance if turret does not reach position
			if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 100  || time <= 0)  {
				nextState = autoShootTenGearDriveStates.SHOOT;
			}
			break;
		case SHOOT:
			//Shoots balls before moving robot - allows for more accurate shooting
			if (time <= 0) {
				nextState = autoShootTenGearDriveStates.DRIVE1;
			}
			break;
		case DRIVE1:
			//Stops robot when it has driven the desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.STOP1;
			}
			break;
		case STOP1:
			if (time <=0) {
				nextState = autoShootTenGearDriveStates.TURN;
			}
			break;
		case TURN:
			//Makes sure robot has reached target angle and has stopped moving quickly before advancing
			//Makes sure robot does not advance to next state if it overshoots turn but passes through target zone
			if (robot.tdt.gyroControl.onTarget() && robot.tdt.avgAbsSpeed <= 10) {
				nextState = autoShootTenGearDriveStates.STOP2;
			}
			break;
		case STOP2:
			if (time <= 0) {
				nextState = autoShootTenGearDriveStates.DRIVE2;
			}
			break;
		case DRIVE2:
			//Stops robot when it has driven the desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.STOP3;
			}
			break;
		case STOP3:
			if (time<= 0 ){
					nextState = autoShootTenGearDriveStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoShootTenGearDriveStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoShootTenGearDriveStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			//Stops robot when it has driven the desired distance
			if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.STOP4;
			}
			break;
		case STOP4:
			if (time<= 0) {
				nextState = autoShootTenGearDriveStates.TURN2;
			}
			break;
		case TURN2:
			if (robot.tdt.gyroControl.onTarget()) {
				nextState = autoShootTenGearDriveStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
				nextState = autoShootTenGearDriveStates.DONE;
			}
			break;
		case DONE:
			break;
	}
}

public void doTransition() {
	if (currentState == autoShootTenGearDriveStates.START && nextState == autoShootTenGearDriveStates.TURNTURRET) {
		robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
		//Sets different values for turret, shooter and cam based on which side of the field robot is starting on
		//Shooter is different distances from boiler on different sides of field
		if (robot.blueRequest) {
			robot.shooter.desiredSpeed = 3500;
			robot.cam.desiredPosition = 1300;		
			robot.turret.desiredTarget = .1;
			desiredDistance = 90;
		}
		else if (robot.redRequest) {
			robot.shooter.desiredSpeed = 3600;
			robot.cam.desiredPosition = 1350;		
			robot.turret.desiredTarget = 7.2;

			desiredDistance = 90;
		}
		robot.hal.shooterTalon.set(robot.shooter.desiredSpeed); 
	}
	if (currentState == autoShootTenGearDriveStates.TURNTURRET && nextState == autoShootTenGearDriveStates.SHOOT) {
		//Shoots for 3.5 seconds
		robot.shootRequest = true;
		time = 175; 
	}
	if (currentState == autoShootTenGearDriveStates.SHOOT && nextState == autoShootTenGearDriveStates.DRIVE1) {
		//Stops shooting and drives straight forward
		robot.shootRequest = false;
		robot.tdt.setDriveMode(driveMode.GYROLOCK);
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveAngle(robot.navx.getYaw());
		robot.tdt.setDriveTrainSpeed(1);
	}
	if (currentState == autoShootTenGearDriveStates.DRIVE1 && nextState == autoShootTenGearDriveStates.STOP1) {
		//Stops robot, waits 2/5 of a second
		robot.navx.reset();
		robot.tdt.setDriveTrainSpeed(0);
		time = 20;
	}
	
	if (currentState == autoShootTenGearDriveStates.STOP1 && nextState == autoShootTenGearDriveStates.TURN) {
		//robot turns right to angle of peg
		if (robot.blueRequest) {
			desiredDistance = 25;
			robot.tdt.setDriveAngle(60);
		}
		if (robot.redRequest) {
			desiredDistance = 25;
			robot.tdt.setDriveAngle(-60);
		}
	}

	if (currentState == autoShootTenGearDriveStates.TURN && nextState == autoShootTenGearDriveStates.STOP2) {
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(0);
		time = 12;
	}
	if (currentState == autoShootTenGearDriveStates.STOP2 && nextState == autoShootTenGearDriveStates.DRIVE2) {
		robot.tdt.setDriveTrainSpeed(.5);	
	}
	
	if (currentState == autoShootTenGearDriveStates.DRIVE2 && nextState == autoShootTenGearDriveStates.STOP3) {
		//robot stops
		time = 13;
		robot.tdt.setDriveTrainSpeed(0);
	}
	if (currentState == autoShootTenGearDriveStates.STOP3 && nextState == autoShootTenGearDriveStates.DROPGEAR) {
		robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
	}
	if (currentState == autoShootTenGearDriveStates.DROPGEAR && nextState == autoShootTenGearDriveStates.WAIT) {
		robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));//Shifts up for extra speed
		//Drives back, away from peg
		if (robot.blueRequest) {
			desiredDistance = -30;
		}
		if (robot.redRequest) {
			desiredDistance = -30;
		}
		time = 15;
	}
	if (currentState == autoShootTenGearDriveStates.WAIT && nextState == autoShootTenGearDriveStates.DRIVEBACK) {
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(-1);
		robot.tdt.setDriveAngle(robot.navx.getYaw());
	}
	if (currentState == autoShootTenGearDriveStates.DRIVEBACK && nextState == autoShootTenGearDriveStates.STOP4) {
		robot.tdt.gyroControl.setAbsoluteTolerance(3); //Increases error band of gyro to increase speed of following turn
		robot.tdt.setDriveTrainSpeed(0);
	}
	if (currentState == autoShootTenGearDriveStates.STOP4 && nextState == autoShootTenGearDriveStates.TURN2) {
		//Drives diagonally across field towards loading station
		if (robot.blueRequest) {
			desiredDistance = 216;
			robot.tdt.setDriveAngle(30);
		}
		else if (robot.redRequest) {
			desiredDistance = 216;
			robot.tdt.setDriveAngle(-30);
		}
		
	}
	if (currentState == autoShootTenGearDriveStates.TURN2 && nextState == autoShootTenGearDriveStates.DRIVE3) {
		robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
		robot.tdt.resetDriveEncoders();
		robot.tdt.setDriveTrainSpeed(1);
	}
	
	if(currentState == autoShootTenGearDriveStates.DRIVE3 && nextState ==autoShootTenGearDriveStates.DONE) {
		robot.tdt.setDriveTrainSpeed(0);
	}
	
	
}
}
