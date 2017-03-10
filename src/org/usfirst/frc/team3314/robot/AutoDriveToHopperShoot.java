package org.usfirst.frc.team3314.robot;

enum autoDriveToHopperShootStates {
	START,
	
}

public class AutoDriveToHopperShoot {
	autoDriveToHopperShootStates currentState;
	autoDriveToHopperShootStates nextState;
	Robot robot;
	int time;
	double desiredDistance;
	
	
	public AutoDriveToHopperShoot(Robot myrobot) {
		
		
	}
	
	public void update() {
		
	}
	
	public void reset() {
		
	}

}
