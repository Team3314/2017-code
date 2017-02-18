package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.PIDOutput;

public class GyroPIDOutput implements PIDOutput {
	Robot robot;
	double turnSpeed = 0;
	
	@Override
	public void pidWrite(double output) {
		turnSpeed = output;
	}
}