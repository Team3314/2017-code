package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

public class Turret {
	Robot robot;
	double desiredTarget;
	
	public Turret(Robot myRobot) {
		//source = encoder on turret talon; output = talon itself
		robot = myRobot;

		robot.hal.turretTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.turretTalon.setPID(Constants.kTurret_kP, Constants.kTurret_kI, Constants.kTurret_kD,
		Constants.kTurret_kF, Constants.kTurret_IZone, Constants.kTurret_RampRate, Constants.kTurret_Profile);
	}
	
	public void update() {
		//talon turns motor to target
		robot.hal.turretTalon.set(desiredTarget);
	}
}