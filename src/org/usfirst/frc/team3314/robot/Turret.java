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
		//Caps turret motor voltage to prevent overshooting
		robot.hal.turretTalon.configMaxOutputVoltage(8);
		//Prevents turret from trying to turn past hard stops
		robot.hal.turretTalon.enableForwardSoftLimit(true);
		robot.hal.turretTalon.setForwardSoftLimit(7.55);
		robot.hal.turretTalon.enableReverseSoftLimit(true);
		robot.hal.turretTalon.setReverseSoftLimit(0);
	}
	
	public void update() {
		//Removes I term from turret PID controller when error is under 1000 ticks to prevent buildup of I term and overshoot on long turns
		if(Math.abs(robot.hal.turretTalon.getClosedLoopError()) > 1000 ) {
			robot.hal.turretTalon.setPID(Constants.kTurret_kP,  0,  Constants.kTurret_kD);
		}
		else if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 1000) {
			robot.hal.turretTalon.setPID(Constants.kTurret_kP,  Constants.kTurret_kI,  Constants.kTurret_kD);
		}
		turretPosition = robot.hal.turretTalon.getPosition();
		robot.hal.turretTalon.set(desiredTarget);
	}
	/* Resets turret position based on red/blue switch, as the turret is supposed to be turned to the left(zero) 
	when starting on the blue side of the field and to the right (7.55) when starting on the red side of the field*/
	public void reset() {
		if (robot.blueRequest) {
			robot.hal.turretTalon.setPosition(0);
		}
		if (robot.redRequest) {
			robot.hal.turretTalon.setPosition(7.55);
		}
		robot.hal.turretTalon.set(robot.hal.turretTalon.getPosition());
	}
	
	public double getEncError(double degError) {
		//Takes an input in degrees and converts it to encoder ticks so that it can be fed into the encoder
		double encError = degError * Constants.kDegToEncTicksConvFactor;
		desiredTarget = encError + robot.hal.turretTalon.getPosition();
		return desiredTarget;
	}
}
