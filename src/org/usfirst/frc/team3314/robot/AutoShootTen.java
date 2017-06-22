package org.usfirst.frc.team3314.robot;

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
		//Checks whether requirements to go to next state are fulfilled and switches states if so,
		//executes code assigned to each state every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time--;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoShootTenState.START;
	}
	


	public void calcNext() {
		nextState = currentState;
		switch (currentState) {
			case START:
				nextState = autoShootTenState.TURNTURRET;
				break;
				
			case TURNTURRET:
				//Makes sure turret is turned to target positon before advancing
				//Also contains a timeout to advance if turret does not reach position
				if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) <= 100)  {
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
			//Sets different values for turret, shooter and cam based on which side of the field robot is starting on
			//Shooter is different distances from boiler on different sides of field
			if (robot.blueRequest) {
				robot.shooter.desiredSpeed = 3370;
				robot.cam.desiredPosition = 1088;		
				robot.turret.desiredTarget = 0;
			}
			else if (robot.redRequest) {
				robot.shooter.desiredSpeed = 4000;
				robot.cam.desiredPosition = 1056;		
				robot.turret.desiredTarget = 7.25;
			}
		}
		if (currentState == autoShootTenState.TURNTURRET && nextState == autoShootTenState.SHOOT) {
			//Shoots for entire auto period
			robot.shootRequest = true;
			time = 750; 
		}
		if (currentState == autoShootTenState.SHOOT && nextState == autoShootTenState.DONE) {
			
		}
	}
}
