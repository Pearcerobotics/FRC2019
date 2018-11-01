package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class P51SingleControler
{

	TalonSRX controller;
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
	// all possible detectable warnings with the speed conteollers
	boolean warningSpeed, warningTemp, warningCurrent, warningMinSoftLimit, warningMaxSoftLimit, warningMinHardLimit, warningMaxHardLimit;

	enum FeedbackDevice{
		CTRE_MagEncoder_Absolute;
	}
	boolean failed = false;

	
	public P51SingleControler()
	{
		this.canID = 32;
		this.controller = new TalonSRX(this.canID);
		//set the PID variables
		this.config_kD=1;
		this.config_kI=.0002;
		this.config_kP=7;
		
		this.minSoftLimit=-500;
		this.maxSoftLimit=900;
		this.FeedbackDevice=CTRE_MagEncoder_Absolute;
		this.softLimitRange = .10;
		this.currentSetPos = -150;

		this.homePos = this.currentSetPos;
	
		this.controller.setInverted(false);
		this.controller.setSensorPhase(true);
		//start all of our errors and warnings as false
		this.errorSpeed = this.errorTemp = this.errorCurrent = this.errorMinSoftLimit = this.errorMaxSoftLimit = this.errorMinHardLimit = this.errorMaxHardLimit =
		this.warningSpeed = this.warningTemp = this.warningCurrent = this.warningMinSoftLimit = this.warningMaxSoftLimit = this.warningMinHardLimit = this.warningMaxHardLimit = false;

		this.controller.configForwardSoftLimitThreshold(this.maxSoftLimit, 10);
		this.controller.configForwardSoftLimitEnable(true, 10);

		this.controller.configReverseSoftLimitThreshold(this.minSoftLimit, 10);
		this.controller.configReverseSoftLimitEnable(true, 10);

		this.controller.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);

		this.controller.config_kP(0, this.config_kP, 10);
		this.controller.config_kI(0, this.config_kI, 10);
		this.controller.config_kD(0, this.config_kD, 10);
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
		if (this.failed == true || this.controller.getSelectedSensorPosition(0) < this.minSoftLimit-this.minSoftLimit*this.softLimitRange || this.controller.getSelectedSensorPosition(0) > this.maxSoftLimit+this.errorMaxSoftLimit*this.softLimitRange)
		{
			this.controller.configForwardSoftLimitEnable(false, 10);
			this.controller.configReverseSoftLimitEnable(false, 10);
			this.controller.set(ControlMode.PercentOutput, input);
			this.failed = true;
			this.controller.setNeutralMode(NeutralMode.Brake);
			return true;
		} else
		{
			this.controller.configForwardSoftLimitEnable(true, 10);
			this.controller.configReverseSoftLimitEnable(true, 10);
			this.controller.set(ControlMode.Position, this.currentSetPos);
			this.controller.setNeutralMode(NeutralMode.Coast);

			return false;
		}
	}

	public int getSetPos() {
		return this.currentSetPos;
	}
	public int getPos(){
		//todo retun the real position
	}
	
	public void home()
	{
		this.controller.setSelectedSensorPosition(this.homePos, 0, 10);
		this.failed = false;
		this.currentSetPos = this.homePos;
	}

	public double getSpeed() {
		//todo return the real speed
	}
	public double getVoltage() {
		//todo return the Voltage
	}
	public double getCurrent(){
		//todo return the Current
	}

}
