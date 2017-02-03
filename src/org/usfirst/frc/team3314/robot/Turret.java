package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;

public class Turret {
	Robot robot;
	PIDController turretController;
	double desiredTarget;
	
	public Turret(Robot myRobot) {
		//puts turret on pid loop w/ source being encoder on the turret talon and the output being the talon itself
		robot = myRobot;
		turretController = new PIDController(0.5, 0.000025, 0, 0, robot.hal.turretTalon, robot.hal.turretTalon);
	}
	
	public void update() {
		//sets mode + feedback device to ensure pid will work, then talon turns motor to target
		robot.hal.turretTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.turretTalon.set(desiredTarget);
	}
}