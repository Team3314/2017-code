package org.usfirst.frc.team3314.robot;

//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;
//import com.ctre.CANTalon;

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
			robot.tdt.lDriveTalon1.set(0);
			robot.tdt.rDriveTalon1.set(0);
			robot.hal.solenoid.set(Value.kReverse);
			time = 50;
		}
			
		if (currentState == autoStates.SOLENOID && nextState == autoStates.MOTORFORWARD) {
			robot.tdt.lDriveTalon1.set(0.5);
			robot.tdt.rDriveTalon1.set(0.5);
			time = 100;
		}
		
		if (currentState == autoStates.MOTORFORWARD && nextState == autoStates.MOTORBACK) {
			robot.tdt.lDriveTalon1.set(-0.5);
			robot.tdt.rDriveTalon1.set(-0.5);
			time = 100;
		}
		
		if (currentState == autoStates.MOTORBACK && nextState == autoStates.DONE) {
			robot.tdt.lDriveTalon1.set(0);
			robot.tdt.rDriveTalon1.set(0);
			robot.hal.solenoid.set(Value.kOff);
			time = 25;
		}
	}
}