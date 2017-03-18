package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoShootTenState{
	START,
	TURNTURRET,
	SHOOT,
	DONE,
}

public class AutoShootTen {

	autoShootTenState currentState;
	autoShootTenState nextState;
	Robot robot;
	int time;
	
	public AutoShootTen(Robot myRobot) {
		robot = myRobot;
		currentState = autoShootTenState.START;
		time = 0;
	
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time--;
	}
	
	public void reset() {
		currentState = autoShootTenState.START;
	}
	


	public void calcNext() {
		nextState = currentState;
		switch (currentState) {
			case START:
				nextState = autoShootTenState.TURNTURRET;
				break;
				
			case TURNTURRET:
				if (Math.abs(robot.hal.turretTalon.getClosedLoopError()*8192) <= 100)  {
					nextState = autoShootTenState.SHOOT;
				}
				break;
			case SHOOT:
				if (time <= 0 ) {
					nextState = autoShootTenState.DONE;
				}
				break;
			case DONE:
				break;
		}
		
		
	}
	public void doTransition() {
		if (currentState == autoShootTenState.START && nextState == autoShootTenState.TURNTURRET) {	
			SmartDashboard.putBoolean("Red", robot.redRequest);
			SmartDashboard.putBoolean("Blue", robot.blueRequest);
			if (robot.blueRequest) {
				robot.shooter.desiredSpeed = 3370;
				robot.cam.desiredPosition = .265625;		
				robot.turret.desiredTarget = 0;
			}
			else if (robot.redRequest) {
				robot.shooter.desiredSpeed = 4000;
				robot.cam.desiredPosition = .2578125;		
				robot.turret.desiredTarget = 7.25;
			}
		}
		if (currentState == autoShootTenState.TURNTURRET && nextState == autoShootTenState.SHOOT) {
			robot.shootRequest = true;
			time = 750; 
		}
		if (currentState == autoShootTenState.SHOOT && nextState == autoShootTenState.DONE) {
			
		}
	}
}
