package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
//import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.DoubleSolenoid.*;
//import edu.wpi.first.wpilibj.AnalogGyro;
//import edu.wpi.first.wpilibj.AnalogInput;

public class HardwareAbstractionLayer {

	Robot robot;
	DoubleSolenoid solenoid;
	DoubleSolenoid extra1;
	DoubleSolenoid extra2;
	DoubleSolenoid extra3;
	CANTalon intakeTalon;
	CustomGyro gyro;

	public HardwareAbstractionLayer(Robot r){
		
		robot = r;
		solenoid = new DoubleSolenoid(0, 1);
		extra1 = new DoubleSolenoid(2, 3);
		extra2 = new DoubleSolenoid(4, 5);
		extra3 = new DoubleSolenoid(6, 7);
		intakeTalon = new CANTalon(4);
		
		gyro = new CustomGyro(0, 1, 0.007);
		gyro.calibrate();
		
	}
}
