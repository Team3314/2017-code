package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

public class HardwareAbstractionLayer {
	Robot robot;
	DoubleSolenoid solenoid;
	
	//following aren't connected yet
	//speed controllers
	CANTalon intakeTalon;
	CANTalon turretTalon;
	
	//digital io
	DigitalInput autoSelect;
	DigitalInput extraIO1;
	DigitalInput extraIO2;
	
	//pneumatics
	Compressor pcm1;
	DoubleSolenoid intake;
	DoubleSolenoid extra1;
	DoubleSolenoid extra2;
	
	//analog + relay
	CustomGyro gyro;
	Relay flashlight;

	public HardwareAbstractionLayer(Robot r){
		robot = r;
		solenoid = new DoubleSolenoid(0, 1);
		
		//following aren't connected yet
		//speed controllers
		intakeTalon = new CANTalon(4);
		turretTalon = new CANTalon(5);
		
		//digital io
		autoSelect = new DigitalInput(0);
		extraIO1 = new DigitalInput(1);
		extraIO2 = new DigitalInput(2);
		
		//pneumatics
		pcm1 = new Compressor(0);
		intake = new DoubleSolenoid(2, 3);
		extra1 = new DoubleSolenoid(4, 5);
		extra2 = new DoubleSolenoid(6, 7);
		
		pcm1.setClosedLoopControl(true);
		
		//analog
		gyro = new CustomGyro(0, 1, 0.007);
		gyro.calibrate();
		
		//relay
		flashlight = new Relay(0);
	}
	
	public void reset(){
		gyro.reset();
	}
	
	public void getStatus(){
		gyro.update();
	}
}