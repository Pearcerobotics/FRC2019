package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Arm
{

	TalonSRX arm;

	int currentSetPos = -150;
	
	boolean failed = false;

	public Arm()
	{
		arm = new TalonSRX(32);

		arm.setInverted(false);
		arm.setSensorPhase(true);

		arm.configForwardSoftLimitThreshold(900, 10);
		arm.configForwardSoftLimitEnable(true, 10);

		arm.configReverseSoftLimitThreshold(-500, 10);
		arm.configReverseSoftLimitEnable(true, 10);

		arm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);

		arm.config_kP(0, 7, 10);
		arm.config_kI(0, .0002, 10);
		arm.config_kD(0, 1, 10);
	}

	public void setPos(int pos)
	{
		currentSetPos = pos;
	}

	public boolean control(double input)
	{
		if (failed == true || arm.getSelectedSensorPosition(0) < -700 || arm.getSelectedSensorPosition(0) > 1100)
		{
			arm.configForwardSoftLimitEnable(false, 10);
			arm.configReverseSoftLimitEnable(false, 10);
			arm.set(ControlMode.PercentOutput, input);
			failed = true;
			arm.setNeutralMode(NeutralMode.Brake);
			return true;
		} else
		{
			arm.configForwardSoftLimitEnable(true, 10);
			arm.configReverseSoftLimitEnable(true, 10);
			arm.set(ControlMode.Position, currentSetPos);
			arm.setNeutralMode(NeutralMode.Coast);

			return false;
		}
	}

	public int getPos()
	{
		return currentSetPos;
	}
	
	public void home()
	{
		arm.setSelectedSensorPosition(-150, 0, 10);
		failed = false;
		currentSetPos = -150;
	}

}
