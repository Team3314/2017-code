package org.usfirst.frc.team3314.robot;

//import com.ctre.CANTalon.*;

//import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoCrossBaselineStates {
	START,
	DRIVE,
	STOP,
	DONE
}

public class AutoCrossBaseline {
	autoCrossBaselineStates currentState;
	autoCrossBaselineStates nextState;
	Robot robot;
	//double tolerance = 5;
	//double distance;
	//double desiredDistance;
	double time = 0;

	public AutoCrossBaseline(Robot myRobot) {
		robot = myRobot;
		currentState = autoCrossBaselineStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoCrossBaselineStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		//distance = robot.tdt.getDistance();	
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Auto state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		//most either go to next state immediately or after timer finishes, 1 goes after encoder pos reaches about 25k
		switch (currentState) {
		case START:
			robot.hal.gyro.reset();
			nextState = autoCrossBaselineStates.DRIVE;
			break;
		case DRIVE:
			if (time <= 0 || /*robot.tdt.lDriveTalon1.getEncPosition() > 25000 &&*/ robot.tdt.rDriveTalon1.getEncPosition() > 25000){
			//desiredDistance - distance <= tolerance) {
				nextState = autoCrossBaselineStates.STOP;
			}
			break;
		case STOP:
			if (time <= 0 ){
				nextState = autoCrossBaselineStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoCrossBaselineStates.START && nextState == autoCrossBaselineStates.DRIVE) {
			//robot drives straight forward at max speed, 10 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 500;
		}
		
		if (currentState == autoCrossBaselineStates.DRIVE && nextState == autoCrossBaselineStates.STOP) {
			//stops robot, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			//robot.hal.intake.set(Value.kReverse);
			time = 50;
		}
	}
}