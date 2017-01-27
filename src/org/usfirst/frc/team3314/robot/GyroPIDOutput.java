package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.PIDOutput;

public class GyroPIDOutput implements PIDOutput {
	Robot robot;

	@Override
	public void pidWrite(double output) {
		robot.tdt.rawLeftSpeed = robot.tdt.desiredSpeed - output;
		robot.tdt.rawRightSpeed = robot.tdt.desiredSpeed + output;	
	}
}