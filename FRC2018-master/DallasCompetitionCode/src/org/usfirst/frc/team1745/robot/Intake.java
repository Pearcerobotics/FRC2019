package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Intake {
	TalonSRX lIntake, rIntake;
	
	public Intake()
	{
		lIntake = new TalonSRX(30);
		rIntake = new TalonSRX(31);
		
	}
	
	public void intake()
	{
		lIntake.set(ControlMode.PercentOutput, -1);
		rIntake.set(ControlMode.PercentOutput, -1);
	}
	
	public void outtake(double input)
	{
			lIntake.set(ControlMode.PercentOutput, input);
			rIntake.set(ControlMode.PercentOutput, input);
	}

	public void rest() {
		// TODO Auto-generated method stub
		lIntake.set(ControlMode.PercentOutput, -.01);
		rIntake.set(ControlMode.PercentOutput, -.01);
	}
}
