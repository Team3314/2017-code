package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;

public class Turret {
	Robot robot;
	PIDController turretController;
	double desiredTarget;
	
	public Turret(Robot myRobot) {
		//source = encoder on turret talon; output = talon itself
		robot = myRobot;
		turretController = new PIDController(Constants.kTurret_kP, Constants.kTurret_kI, Constants.kTurret_kD,
		Constants.kTurret_kF, robot.hal.turretTalon, robot.hal.turretTalon);
		
		robot.hal.turretTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	}
	
	public void update() {
		//talon turns motor to target
		robot.hal.turretTalon.set(desiredTarget);
	}
}