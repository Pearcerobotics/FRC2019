package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class DriveTrain {

	TalonSRX lfDrive,
			lmDrive,
			lbDrive,
			rfDrive,
			rmDrive,
			rbDrive;
	
	public DriveTrain()
	{
		lfDrive = new TalonSRX(20);
		lmDrive = new TalonSRX(21);
		lbDrive = new TalonSRX(22);
	
		rfDrive = new TalonSRX(23);
		rmDrive = new TalonSRX(24);
		rbDrive = new TalonSRX(25);
		
		lmDrive.follow(lfDrive);
		lbDrive.follow(lfDrive);
		
		rmDrive.follow(rfDrive);
		rbDrive.follow(rfDrive);
		
		rfDrive.setInverted(true);
		rmDrive.setInverted(true);
		rbDrive.setInverted(true);
		
		lfDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		rfDrive.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		
		lfDrive.setSensorPhase(true);
		rfDrive.setSensorPhase(true);
		
		lfDrive.configVoltageCompSaturation(12, 0);
		lmDrive.configVoltageCompSaturation(12, 0);
		lbDrive.configVoltageCompSaturation(12, 0);
		rfDrive.configVoltageCompSaturation(12, 0);
		rmDrive.configVoltageCompSaturation(12, 0);
		rbDrive.configVoltageCompSaturation(12, 0);
		
	}
	
	public int getLeftPos()
	{
		return lfDrive.getSelectedSensorPosition(0);
	}
	
	public int getRightPos()
	{
		return rfDrive.getSelectedSensorPosition(0);
	}
	
	public void setDrive(double left, double right)
	{
		lfDrive.set(ControlMode.PercentOutput, left);
		rfDrive.set(ControlMode.PercentOutput, right);
	}
	
}
