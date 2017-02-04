package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;

public class AngleAdjust {
	Robot robot;
	PIDController adjustController;
	double desiredTarget;
	
	public AngleAdjust(Robot myRobot) {
		//source = encoder on adjust talon; output = talon itself
		robot = myRobot;
		adjustController = new PIDController(Constants.kAdjust_kP, Constants.kAdjust_kI, Constants.kAdjust_kD,
		Constants.kAdjust_kF, robot.hal.adjustTalon, robot.hal.adjustTalon);
		
		robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.adjustTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	}
	
	public void update() {
		//talon turns motor to target
		robot.hal.adjustTalon.set(desiredTarget);
	}
}