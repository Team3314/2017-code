package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;


/*This class creates objects referring to parts on the robot that can be used throughout the code. This central reference makes it 
easier to keep track of objects*/
public class HardwareAbstractionLayer {
	Robot robot;
	
	//speed controllers
	CANTalon turretTalon;
	CANTalon shooterTalon;
	CANTalon camTalon; //changes angle of shooter
	Spark upperIndexSpark;
	Spark intakeSpark;
	Spark lowerIndexSpark; //both put balls into shooter
	Spark agitatorSpark; //move balls around so they dont get stuck
	Spark upperIntakeSpark;
	Spark climberSpark;
	
	//pneumatics
	Compressor pcm1;
	DoubleSolenoid gearIntake;
	DoubleSolenoid driveShifter;
	Solenoid flashlight;
	Solenoid ringLight;
	PowerDistributionPanel pdp;

	public HardwareAbstractionLayer(Robot r){
		robot = r;
		
		//speed controllers
		lowerIndexSpark = new Spark(0);
		upperIntakeSpark = new Spark(1);
		climberSpark = new Spark(2);
		turretTalon = new CANTalon(4);
		shooterTalon = new CANTalon(5);
		camTalon = new CANTalon(6);
		intakeSpark = new Spark(7);
		upperIndexSpark = new Spark(8);
		agitatorSpark = new Spark(9);
		
		upperIndexSpark.setInverted(true);
		upperIntakeSpark.setInverted(true);
		
		
		pdp = new PowerDistributionPanel();
		
		
		shooterTalon.configEncoderCodesPerRev(2048);
		shooterTalon.changeControlMode(TalonControlMode.Speed);
		shooterTalon.reverseSensor(true);
		shooterTalon.setInverted(true);
		shooterTalon.setPID(Constants.kShooter_kP, Constants.kShooter_kI, Constants.kShooter_kD,
		Constants.kShooter_kF, Constants.kShooter_IZone, Constants.kShooter_RampRate, Constants.kShooter_Profile);
		
		
		//pneumatics
		pcm1 = new Compressor(0);
		driveShifter = new DoubleSolenoid(0, 1);
		gearIntake = new DoubleSolenoid(2, 3);

		flashlight = new Solenoid(7);
		ringLight = new Solenoid(6);
		
		pcm1.setClosedLoopControl(true);
	}
}
