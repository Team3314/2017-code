package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class HardwareAbstractionLayer {
	Robot robot;
	
	//speed controllers
	CANTalon turretTalon;
	CANTalon shooterTalon;
	CANTalon adjustTalon; //changes angle of shooter
	Spark upperIndexSpark;
	Spark intakeSpark;
	Spark lowerIndexSpark; //puts balls into shooter
	Spark agitatorSpark; //move balls around so they dont get stuck
	
	//digital io
	DigitalInput autoSelect; //choose autos with physical binary switches
	DigitalInput autoSelect2;
	DigitalInput autoSelect3;
	DigitalInput autoSelect4;
	
	//pneumatics
	Compressor pcm1;
	DoubleSolenoid gearIntake;
	DoubleSolenoid driveShifter;
	Solenoid flashlight;
	
	//misc
	AnalogInput indexSensor;

	public HardwareAbstractionLayer(Robot r){
		robot = r;
		
		//speed controllers
		turretTalon = new CANTalon(4);
		shooterTalon = new CANTalon(5);
		adjustTalon = new CANTalon(6);
		upperIndexSpark = new Spark(6);
		intakeSpark = new Spark(7);
		lowerIndexSpark = new Spark(8);
		agitatorSpark = new Spark(9);
		
		shooterTalon.configEncoderCodesPerRev(2048);
		shooterTalon.changeControlMode(TalonControlMode.Speed);
		shooterTalon.setPID(Constants.kShooter_kP, Constants.kShooter_kI, Constants.kShooter_kD,
		Constants.kShooter_kF, Constants.kShooter_IZone, Constants.kShooter_RampRate, Constants.kShooter_Profile);
		
		//digital io
		autoSelect = new DigitalInput(0);
		autoSelect2 = new DigitalInput(1);
		autoSelect3 = new DigitalInput(2);
		autoSelect4 = new DigitalInput(3);
		
		//pneumatics
		pcm1 = new Compressor(0);
		driveShifter = new DoubleSolenoid(0, 1);
		gearIntake = new DoubleSolenoid(2, 3);
		flashlight = new Solenoid(4);
		
		pcm1.setClosedLoopControl(true);
		
		//misc
		indexSensor = new AnalogInput(0);
	}
}
