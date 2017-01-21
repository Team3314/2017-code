package org.usfirst.frc.team3314.robot;

//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;

enum autoStates {
	
	START,
	SOLENOID,
	MOTORFORWARD,
	MOTORBACK,
	DONE
	
}

public class AutoTest {
	
	autoStates currentState;
	autoStates nextState;
	Robot robot;
	double time = 0;
	
	public AutoTest(Robot myRobot) {
		robot = myRobot;
		currentState = autoStates.START;
	}
	
	public void reset() {
		currentState = autoStates.START;
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			nextState = autoStates.SOLENOID;
			break;
		case SOLENOID:
			if (time <= 0 ){
				nextState = autoStates.MOTORFORWARD;
			}
			break;
		case MOTORFORWARD:
			if (time <= 0 ){
				nextState = autoStates.MOTORBACK;
			}
			break;
		case MOTORBACK:
			if (time <= 0 ){
				nextState = autoStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoStates.START && nextState == autoStates.SOLENOID) {	
			/*robot.tdt.lDriveTalon1.changeControlMode(TalonControlMode.Position); //default is PercentVbus
			robot.tdt.lDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			robot.tdt.lDriveTalon1.setPID(0.5, 0.001, 0, 0, 0, 0, 0);
			robot.tdt.lDriveTalon1.setPosition(0);
			robot.tdt.rDriveTalon1.changeControlMode(TalonControlMode.Position); //default is PercentVbus
			robot.tdt.rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			robot.tdt.rDriveTalon1.setPID(0.5, 0.001, 0, 0, 0, 0, 0);
			robot.tdt.rDriveTalon1.setPosition(0); trying out the pid + encoder ticks*/
			
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(0);
			robot.hal.solenoid.set(Value.kReverse);
			time = 50;
		}
			
		if (currentState == autoStates.SOLENOID && nextState == autoStates.MOTORFORWARD) {
			robot.tdt.setDriveTrainSpeed(0.5);
			time = 100;
		}
		
		if (currentState == autoStates.MOTORFORWARD && nextState == autoStates.MOTORBACK) {
			robot.tdt.setDriveTrainSpeed(-0.5);
			time = 100;
		}
		
		if (currentState == autoStates.MOTORBACK && nextState == autoStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
			robot.hal.solenoid.set(Value.kOff);
			time = 25;
		}
	}
}