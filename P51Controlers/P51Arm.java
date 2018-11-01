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

	//all possible detectable and recoverable errors with the speed controller
	boolean errorSpeed, errorTemp, errorCurrent, errorMinSoftLimit, errorMaxSoftLimit, errorMinHardLimit, errorMaxHardLimit;
	this.errorSpeed = this.errorTemp = this.errorCurrent = this.errorMinSoftLimit = this.errorMaxSoftLimit = this.errorMinHardLimit = this.errorMaxHardLimit = false;
	// all possible detectable warnings with the speed conteollers
	boolean warningSpeed, warningTemp, warningCurrent, warningMinSoftLimit, warningMaxSoftLimit, warningMinHardLimit, warningMaxHardLimit;
	this.warningSpeed = this.warningTem p= this.warningCurrent = this.warningMinSoftLimit = this.warningMaxSoftLimit = this.warningMinHardLimit = this.warningMaxHardLimit = false;

	enum FeedbackDevice{
		CTRE_MagEncoder_Absolute;
	}
	boolean failed = false;

	
	public Arm()
	{
		this.canID = 32;
		this.arm = new TalonSRX(this.canID);
		this.config_kD=1;
		this.config_kI=.0002;
		this.config_kP=7;
		this.minSoftLimit=-500;
		this.maxSoftLimit=900;
		this.FeedbackDevice=CTRE_MagEncoder_Absolute;
		this.softLimitRange = .10;
		this.currentSetPos = -150;

		this.homePos = this.currentSetPos;
		
		this.arm.setInverted(false);
		this.arm.setSensorPhase(true);

		this.arm.configForwardSoftLimitThreshold(this.maxSoftLimit, 10);
		this.arm.configForwardSoftLimitEnable(true, 10);

		this.arm.configReverseSoftLimitThreshold(this.minSoftLimit, 10);
		this.arm.configReverseSoftLimitEnable(true, 10);

		this.arm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);

		this.arm.config_kP(0, this.config_kP, 10);
		this.arm.config_kI(0, this.config_kI, 10);
		this.arm.config_kD(0, this.config_kD, 10);
	}

	
	private void checkSafety()
	{
		//todo add checks for all safety items, set safety flags, and call limiting if maxes are hit
	}
	private void limitController()
	{
		//ToDo: add a method for limiting the speed controller
		//? maybe voltage limit it while its in the error state?

	}

	public void setPos(int pos)
	{
		this.currentSetPos = pos;
	}

	public boolean control(double input)
	{
		if (this.failed == true || this.arm.getSelectedSensorPosition(0) < this.minSoftLimit-this.minSoftLimit*this.softLimitRange || this.arm.getSelectedSensorPosition(0) > this.maxSoftLimit+this.errorMaxSoftLimit*this.softLimitRange)
		{
			this.arm.configForwardSoftLimitEnable(false, 10);
			this.arm.configReverseSoftLimitEnable(false, 10);
			this.arm.set(ControlMode.PercentOutput, input);
			this.failed = true;
			this.arm.setNeutralMode(NeutralMode.Brake);
			return true;
		} else
		{
			this.arm.configForwardSoftLimitEnable(true, 10);
			this.arm.configReverseSoftLimitEnable(true, 10);
			this.arm.set(ControlMode.Position, this.currentSetPos);
			this.arm.setNeutralMode(NeutralMode.Coast);

			return false;
		}
	}

	public int getPos()
	{
		return this.currentSetPos;
	}
	
	public void home()
	{
		this.arm.setSelectedSensorPosition(this.homePos, 0, 10);
		this.failed = false;
		this.currentSetPos = this.homePos;
	}

}
