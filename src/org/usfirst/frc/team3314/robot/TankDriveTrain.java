package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.*;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//get y for both sticks

/*enum driveMode {
	IDLE,
	TANK
}*/

public class TankDriveTrain {

	CANTalon rDriveTalon1;
	CANTalon rDriveTalon2;
	CANTalon lDriveTalon1;
	CANTalon lDriveTalon2;
	Robot robot;
	
	double leftStickInput;
	double rightStickInput;
	//double leftRawSpeed = leftStickInput;
	//double rightRawSpeed = rightStickInput;

	public TankDriveTrain(Robot myRobot) {
	
		robot = myRobot;
		rDriveTalon1 = new CANTalon(0);
		rDriveTalon2 = new CANTalon(2);
		lDriveTalon1 = new CANTalon(1);
		lDriveTalon2 = new CANTalon(3);
		
		lDriveTalon2.changeControlMode(TalonControlMode.Follower);
		lDriveTalon2.set(lDriveTalon1.getDeviceID());
		rDriveTalon2.changeControlMode(TalonControlMode.Follower);
		rDriveTalon2.set(rDriveTalon1.getDeviceID());
		lDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		rDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		
	}
	
	public void setStickInputs(double leftInput, double rightInput) {
		
		leftStickInput = leftInput;
		rightStickInput = rightInput;
		lDriveTalon1.set(leftInput);
		rDriveTalon1.set(rightInput);
	
	}
}