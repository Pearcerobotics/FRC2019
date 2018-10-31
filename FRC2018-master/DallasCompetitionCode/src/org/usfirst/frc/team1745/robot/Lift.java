package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Lift {
	TalonSRX lLift, rLift;
	
	int currentSetPos = 100;
	
	public Lift()
	{
		lLift = new TalonSRX(28);
		rLift = new TalonSRX(29);
		
		lLift.follow(rLift);
		
		lLift.setInverted(false);
		
		rLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		rLift.setSensorPhase(true);
		rLift.setInverted(true);
		
		rLift.config_kP(0, 1.7, 10);
		rLift.config_kI(0, 0, 10);
		rLift.config_kD(0, .4, 10);
		
		rLift.configForwardSoftLimitThreshold(19000, 10);
		rLift.configForwardSoftLimitEnable(true, 10);
		
		rLift.configReverseSoftLimitThreshold(-20, 10);
		rLift.configReverseSoftLimitEnable(true, 10);
	}
	
	public void setPos(int pos)
	{
		currentSetPos = pos;
	}
	
	public void control()
	{
		rLift.set(ControlMode.Position, currentSetPos);
		if(rLift.getSelectedSensorPosition(0) > 18500 && currentSetPos > 18500)
		{
			rLift.set(ControlMode.PercentOutput, .08);
			rLift.setNeutralMode(NeutralMode.Brake);
			lLift.setNeutralMode(NeutralMode.Brake);
		}
	}

	public int getPos() {
		// TODO Auto-generated method stub
		return currentSetPos;
	}
}
