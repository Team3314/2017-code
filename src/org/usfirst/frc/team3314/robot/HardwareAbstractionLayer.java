package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

public class HardwareAbstractionLayer {
	Robot robot;
	
	//speed controllers
	CANTalon turretTalon;
	CANTalon shooterTalon;
	CANTalon adjustTalon; //changes angle of shooter
	Spark intakeSpark;
	Spark indexSpark; //puts balls into shooter
	Spark agitatorSpark1; //this and next move balls around so they dont get stuck
	Spark agitatorSpark2;
	
	//digital io
	DigitalInput autoSelect;
	DigitalInput autoSelect2;
	DigitalInput autoSelect3;
	DigitalInput autoSelect4;
	
	//pneumatics
	Compressor pcm1;
	DoubleSolenoid gearIntake;
	DoubleSolenoid intake;
	DoubleSolenoid driveShifter;
	Solenoid flashlight;
	
	//analog 
	CustomGyro gyro;

	public HardwareAbstractionLayer(Robot r){
		robot = r;
		
		//speed controllers
		turretTalon = new CANTalon(5);
		shooterTalon = new CANTalon(6);
		adjustTalon = new CANTalon(7);
		intakeSpark = new Spark(0);
		indexSpark = new Spark(1);
		agitatorSpark1 = new Spark(2);
		agitatorSpark2 = new Spark(3);
		
		//digital io
		autoSelect = new DigitalInput(0);
		autoSelect2 = new DigitalInput(1);
		autoSelect3 = new DigitalInput(2);
		autoSelect4 = new DigitalInput(3);
		
		//pneumatics
		pcm1 = new Compressor(0);
		gearIntake = new DoubleSolenoid(0, 1);
		intake = new DoubleSolenoid(2, 3);
		driveShifter = new DoubleSolenoid(4, 5);
		flashlight = new Solenoid(6);
		
		pcm1.setClosedLoopControl(true);
		
		//analog
		gyro = new CustomGyro(0, 1, 0.007);
		gyro.calibrate();
	}
	
	public void reset(){
		gyro.reset();
	}
	
	public void getStatus(){
		gyro.update();
	}
}