package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class GyroPIDSource implements PIDSource {
	Robot robot;
	double setPoint = 0;
	double currentAngle = 0;

	public GyroPIDSource(Robot r, double setpoint, double gyroangle) {
		
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return null;
	}

	@Override
	public double pidGet() {
		return currentAngle;
	}
	
	public void setPIDInput(double setpoint, double currentangle) {
		currentangle %= 360;
		if(currentangle > 180) {
			currentangle -= 360;
		}
		else if(currentangle < -180) {
			currentangle += 360;
		}
		
		setPoint = setpoint;
		currentAngle = currentangle;	
	}	
}