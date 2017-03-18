package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

public class Turret {
	Robot robot;
	double desiredTarget = 0;
	double turretPosition;
	
	public Turret(Robot myRobot) {
		robot = myRobot;

		robot.hal.turretTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.turretTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		robot.hal.turretTalon.setPID(Constants.kTurret_kP, Constants.kTurret_kI, Constants.kTurret_kD,
		Constants.kTurret_kF, Constants.kTurret_IZone, Constants.kTurret_RampRate, Constants.kTurret_Profile);
		robot.hal.turretTalon.configMaxOutputVoltage(8);
		robot.hal.turretTalon.enableForwardSoftLimit(true);
		robot.hal.turretTalon.setForwardSoftLimit(7.55);
		robot.hal.turretTalon.enableReverseSoftLimit(true);
		robot.hal.turretTalon.setReverseSoftLimit(0);
	}
	
	public void update() {
		//talon turns motor to target
		if(Math.abs(robot.hal.turretTalon.getClosedLoopError()) > 1000 ) {
			
			robot.hal.turretTalon.setPID(Constants.kTurret_kP,  0,  Constants.kTurret_kD);
		}
		else if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 1000) {
			robot.hal.turretTalon.setPID(Constants.kTurret_kP,  Constants.kTurret_kI,  Constants.kTurret_kD);
		}
		turretPosition = robot.hal.turretTalon.getPosition();
		robot.hal.turretTalon.set(desiredTarget);
	}
	
	public void reset() {
		robot.hal.turretTalon.set(0);
		robot.hal.turretTalon.setPosition(0);
	}
	
	public void getEncError(double pxlError) {
		//converts error in degrees to encoder ticks, then adds it to current position to calculate desired target
		double degreeError = pxlError * Constants.kPxlToDegConvFactor;
		double encError = degreeError * Constants.kDegToEncTicksConvFactor;
		double target = encError + robot.hal.turretTalon.getEncPosition();
		desiredTarget = target;
	}
}