package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

public class Turret {
	Robot robot;
	double desiredTarget = 0;
	
	public Turret(Robot myRobot) {
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
	
	public void getEncError(double pxlError) { //possible usage
		double degreeError = pxlError * Constants.kPxlToDegConvFactor;
		double encError = degreeError * Constants.kPxlToEncTicksConvFactor;
		double target = encError + robot.hal.turretTalon.getEncPosition();
		desiredTarget = target;
	}
}