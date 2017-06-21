package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


enum autoShootCenterGearDriveFeederState{
	START,
	TURNTURRET,
	SHOOT,
	DRIVE,
	DROPGEAR,
	WAIT,
	DRIVEBACK,
	//same here
	STOP,
	TURN,
	DRIVE2,
	TURN2,
	DRIVE3,
	DONE,
}
public class AutoShootCenterGearDriveFeeder {

		autoShootCenterGearDriveFeederState currentState;
		autoShootCenterGearDriveFeederState nextState;
		Robot robot;
		int time;
		double desiredDistance = 0;
		
		public AutoShootCenterGearDriveFeeder(Robot myRobot) {
			robot = myRobot;
			currentState = autoShootCenterGearDriveFeederState.START;
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
			currentState = autoShootCenterGearDriveFeederState.START;
		}
		


		public void calcNext() {
			nextState = currentState;
			switch (currentState) {
				case START:
					robot.tdt.gyroControl.setAbsoluteTolerance(3);
					nextState = autoShootCenterGearDriveFeederState.TURNTURRET;
					break;
					
				case TURNTURRET:
					//Makes sure turret is turned to target position before advancing
					//Also contains timeout so state machine can advance if turret does not reach target
					if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 110 || time <= 0)  {
						nextState = autoShootCenterGearDriveFeederState.SHOOT;
					}
					break;
				case SHOOT:
					if (time <= 0 ) {
						nextState = autoShootCenterGearDriveFeederState.DRIVE;
					}
					break;
				case DRIVE:
					//Stops robot when it has driven the desired distance
					if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveFeederState.DROPGEAR;
					}
					break;
				case DROPGEAR:
					//Makes sure gear intake is open beore moving back
					if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
						nextState = autoShootCenterGearDriveFeederState.WAIT;
					}
					break;
				case WAIT:
					//Makes sure geat has finished falling from robot beofre moving backwards.
					if (time <= 0) {
						nextState = autoShootCenterGearDriveFeederState.DRIVEBACK;
					}
					break;
				case DRIVEBACK:
					//Drives backwards until robot drives desired distance
					if (robot.tdt.avgEncPos <= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveFeederState.STOP;
					}
					break;
				case STOP:
					if (time<= 0) {
						nextState = autoShootCenterGearDriveFeederState.TURN;
					}
					break;
				case TURN:
					//Waits for robot to turn to target angle 
					if (robot.tdt.gyroControl.onTarget()) {
						nextState = autoShootCenterGearDriveFeederState.DRIVE2;
					}
					break;
				case DRIVE2:
					//Stops robot when it has driven the desired distance
					if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveFeederState.TURN2;
					}
					break;
				case TURN2:
					//Waits for robot to turn to target angle 
					if (robot.tdt.gyroControl.onTarget()) {
						nextState = autoShootCenterGearDriveFeederState.DRIVE3;
					}
					break;
				case DRIVE3:
					//Stops robot when it has driven the desired distance
					if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)) {
						nextState = autoShootCenterGearDriveFeederState.DONE;
					}
					break;
				case DONE:
					break;
			}
			
			
		}
		public void doTransition() {
			if (currentState == autoShootCenterGearDriveFeederState.START && nextState == autoShootCenterGearDriveFeederState.TURNTURRET) {	
				//Sets different values for turret, shooter and cam based on which side of the field robot is starting on
			//Shooter is different distances from boiler on different sides of field
				if (robot.blueRequest) {
					robot.shooter.desiredSpeed = 4350;
					robot.cam.desiredPosition = 1500;		
					robot.turret.desiredTarget =.05;
				}
				else if (robot.redRequest) {
					robot.shooter.desiredSpeed = 4400;
					robot.cam.desiredPosition = 1550;		
					robot.turret.desiredTarget = 7.13;
				}
				time = 50;
			}
			if (currentState == autoShootCenterGearDriveFeederState.TURNTURRET && nextState == autoShootCenterGearDriveFeederState.SHOOT) {
				//Shoots for 5 seconds
				robot.shootRequest = true;
				time = 250; 
			}
			if (currentState == autoShootCenterGearDriveFeederState.SHOOT && nextState == autoShootCenterGearDriveFeederState.DRIVE) {
				//Stops shooting and drives straight forward
				robot.shootRequest = false;
				if (robot.blueRequest) {
					desiredDistance = 80;
				}
				else if (robot.redRequest) {
					desiredDistance = 80;
				}
				robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
				robot.tdt.setDriveMode(driveMode.GYROLOCK);
				robot.tdt.setDriveAngle(robot.navx.getYaw());
				robot.tdt.setDriveTrainSpeed(.5);
			 }
			if (currentState == autoShootCenterGearDriveFeederState.DRIVE && nextState == autoShootCenterGearDriveFeederState.DROPGEAR) {
				robot.tdt.setDriveTrainSpeed(0);
				robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
			}
			if (currentState == autoShootCenterGearDriveFeederState.DROPGEAR && nextState == autoShootCenterGearDriveFeederState.WAIT) {
				time = 15;
				robot.tdt.resetDriveEncoders();
				desiredDistance = -54;
				}
			if (currentState == autoShootCenterGearDriveFeederState.WAIT && nextState == autoShootCenterGearDriveFeederState.DRIVEBACK) {
				robot.tdt.setDriveTrainSpeed(-1);
			}
			
			if (currentState == autoShootCenterGearDriveFeederState.DRIVEBACK && nextState == autoShootCenterGearDriveFeederState.STOP) {
				robot.tdt.setDriveTrainSpeed(0);
			}
			if (currentState == autoShootCenterGearDriveFeederState.STOP && nextState == autoShootCenterGearDriveFeederState.TURN) {
				//Turns 90 degrees and drives across field to get around airship
				if (robot.blueRequest) {
					desiredDistance = 102;
					robot.tdt.setDriveAngle(90);
				}
				else if (robot.redRequest) {
					desiredDistance =102;
					robot.tdt.setDriveAngle(-90);
				}
				robot.tdt.setDriveTrainSpeed(0);
				
			}
			
			if (currentState == autoShootCenterGearDriveFeederState.TURN && nextState == autoShootCenterGearDriveFeederState.DRIVE2) {
				robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));//Closes gear intake to protect it
				robot.tdt.resetDriveEncoders();
				robot.tdt.setDriveTrainSpeed(1);
			}
			if (currentState == autoShootCenterGearDriveFeederState.DRIVE2 && nextState == autoShootCenterGearDriveFeederState.TURN2) {
				//Robot turns forward again and drives to far side of field towards loading station
				if (robot.blueRequest) {
					desiredDistance = 288;
					robot.tdt.setDriveAngle(0);
				}
				else if (robot.redRequest) {
					desiredDistance = 288;
					robot.tdt.setDriveAngle(0);
				}
				robot.tdt.setDriveTrainSpeed(0);
			}
			if (currentState == autoShootCenterGearDriveFeederState.TURN2 && nextState == autoShootCenterGearDriveFeederState.DRIVE3) {
				robot.tdt.resetDriveEncoders();
				robot.hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));
				robot.tdt.setDriveTrainSpeed(1);
			}
			if (currentState == autoShootCenterGearDriveFeederState.DRIVE3 && nextState == autoShootCenterGearDriveFeederState.DONE) {
				robot.tdt.setDriveTrainSpeed(0);
			
			}
		 }
	}
