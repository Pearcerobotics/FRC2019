package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Arm
{

	TalonSRX arm;
	int canID; 
	
	int currentSetPos;
	int homePos;
	double config_kD, config_kI, config_kP; //PID values for the devixe
	int minSoftLimit, maxSoftLimit; //soft limits
	double softLimitRange; // % to allow the soft limits to be beyond before throwing a error state
	// safety  
	double minCurrent, runningCurrent, maxCurrent, currentRange; // max and min current allowed for safety use JVN for estamates
	double minTemp, runningTemp, maxTemp, currentRange; //min and max motor tempatures
	double minSpeed, runningSpeed, maxSpeed, speedRange;  // min and max angular speeds

	boolean errorSpeed, errorTemp, errorCurrent, errorMinSoftLimit, errorMaxSoftLimit, errorMinHardLimit, errorMaxHardLimit = false;
	boolean warningSpeed, warningTemp, warningCurrent, warningMinSoftLimit, warningMaxSoftLimit, warningMinHardLimit, warningMaxHardLimit = false;


	enum FeedbackDevice{
		CTRE_MagEncoder_Absolute;
	}
	boolean failed = false;

	
	public Arm()
	{
		arm = new TalonSRX(32);
		this.config_kD=1;
		this.config_kI=.0002;
		this.config_kP=7;
		this.minSoftLimit=-500;
		this.maxSoftLimit=900;
		this.FeedbackDevice=CTRE_MagEncoder_Absolute;
		this.softLimitRange = .10;
		this.currentSetPos = -150
		arm.setInverted(false);
		arm.setSensorPhase(true);

		arm.configForwardSoftLimitThreshold(this.maxSoftLimit, 10);
		arm.configForwardSoftLimitEnable(true, 10);

		arm.configReverseSoftLimitThreshold(this.minSoftLimit, 10);
		arm.configReverseSoftLimitEnable(true, 10);

		arm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);

		arm.config_kP(0, this.config_kP, 10);
		arm.config_kI(0, this.config_kI, 10);
		arm.config_kD(0, this.config_kD, 10);
	}

	
	private void checkSafety()
	{
		//todo add checks for all safety items, set safety flags, and call limiting if maxes are hit
	}
	private void limitController()
	{

	}

	public void setPos(int pos)
	{
		currentSetPos = pos;
	}

	public boolean control(double input)
	{
		if (failed == true || arm.getSelectedSensorPosition(0) < this.minSoftLimit-this.minSoftLimit*this.softLimitRange || arm.getSelectedSensorPosition(0) > this.maxSoftLimit+this.errorMaxSoftLimit*this.softLimitRange)
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
