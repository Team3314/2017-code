package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
//import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.DoubleSolenoid.*;

public class HardwareAbstractionLayer {

	Robot robot;
	DoubleSolenoid solenoid;
	DoubleSolenoid extra1;
	DoubleSolenoid extra2;
	DoubleSolenoid extra3;
	CANTalon intakeTalon;

	public HardwareAbstractionLayer(Robot r){
		
		robot = r;
		solenoid = new DoubleSolenoid(0, 1);
		extra1 = new DoubleSolenoid(2, 3);
		extra2 = new DoubleSolenoid(4, 5);
		extra3 = new DoubleSolenoid(6, 7);
		intakeTalon = new CANTalon(4);
		
	}
}
