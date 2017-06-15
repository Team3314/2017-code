package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.PIDOutput;

/*Takes the output of the gyro PID controller and creates a variable from it which is used to speed up one side and slow down the other
to keep the robot driving straight in gyrolock. */

public class GyroPIDOutput implements PIDOutput {
	Robot robot;
	double turnSpeed = 0;
	
	@Override
	public void pidWrite(double output) {
		turnSpeed = output;
	}
}
