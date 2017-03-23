package org.usfirst.frc.team3314.robot;

import java.io.File;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

enum motionProfileStates {
	START,
	RUNPROFILE,
	DONE
}

public class MotionProfile {
	File myFile = new File("myfile.csv");
	// Arguments:
	// Fit Method:          HERMITE_CUBIC or HERMITE_QUINTIC
	// Sample Count:        SAMPLES_HIGH (100 000)
//	                      SAMPLES_LOW  (10 000)
//	                      SAMPLES_FAST (1 000)
	// Time Step:          Seconds
	// Max Velocity:       m/s
	// Max Acceleration:    m/s/s
	// Max Jerk:             m/s/s/s
	//(Fit Method, Sample Count, Time Step, Max Velocity, Max Acceleration, Max Jerk)

     Trajectory trajectory = Pathfinder.readFromCSV(myFile);
     // Wheelbase Width = 0.5m
     TankModifier modifier = new TankModifier(trajectory).modify(0.6985); // <- inside to inside of tracks ##  0.7493);
     

     // Do something with the new Trajectories...
     Trajectory leftTrajectory = modifier.getLeftTrajectory();
     Trajectory rightTrajectory = modifier.getRightTrajectory();
     
     EncoderFollower left = new EncoderFollower(modifier.getLeftTrajectory());
     EncoderFollower right = new EncoderFollower(modifier.getRightTrajectory());
     
     motionProfileStates currentState;
     motionProfileStates nextState;
     
     double time = 0;
     
     Robot robot;
     
     public MotionProfile(Robot r) {
    	 robot = r;
    	 currentState = motionProfileStates.START;
     }
    	 public void update() {
    			calcNext();
    			doTransition();
    			currentState = nextState;
    			time--;
    		}
    		
    		public void reset() {
    			currentState = motionProfileStates.START;
    		}
    		


    		public void calcNext() {
    			nextState = currentState;
    			switch (currentState) {
    				case START:
    					nextState = motionProfileStates.RUNPROFILE;
    					break;
    					
    				case RUNPROFILE:
    					double l = left.calculate(robot.tdt.leftDrivePosition);
    					double r = right.calculate(robot.tdt.rightDrivePosition);
    					double gyroHeading = robot.ahrs.getAngle();
    					double desiredHeading = Pathfinder.r2d(left.getHeading());
    					
    					double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
    					double turn = 0.025 * angleDifference;
    					
    					robot.tdt.lDriveTalon1.set(l - turn);
    					robot.tdt.rDriveTalon1.set(r + turn);
    					if (left.isFinished() && right.isFinished())  {
    						nextState = motionProfileStates.DONE;
    					}
    					break;
    				case DONE:
    					break;
    			}
    			
    			
    		}
    		public void doTransition() {
    			if (currentState == motionProfileStates.START && nextState == motionProfileStates.RUNPROFILE) {	
    				left.configureEncoder(robot.tdt.leftDrivePosition, 8192 , Constants.kPulleyDiameter);
    				right.configureEncoder(robot.tdt.rightDrivePosition, 8192 , Constants.kPulleyDiameter);
    				left.configurePIDVA(1.0, 0.0, 0.0, 1 / Constants.kMaxVelocity, 0);
    			
    				}
    			if (currentState == motionProfileStates.RUNPROFILE && nextState == motionProfileStates.DONE) {
    				
  
    		}
    	 
     }

}
