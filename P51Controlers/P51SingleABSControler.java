package org.usfirst.frc.team1745.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class P51SingleABSControler
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
	double minTemp, runningTemp, maxTemp, tempRange; //min and max motor tempatures
	double minSpeed, runningSpeed, maxSpeed, speedRange;  // min and max angular speeds

	//all possible detectable and recoverable errors with the speed controller
	boolean errorSpeed, errorTemp, errorCurrent, errorMinSoftLimit, errorMaxSoftLimit, errorMinHardLimit, errorMaxHardLimit;
	// all possible detectable warnings with the speed conteollers
	boolean warningSpeed, warningTemp, warningCurrent, warningMinSoftLimit, warningMaxSoftLimit, warningMinHardLimit, warningMaxHardLimit;

	boolean verboseLogging; 

	boolean safetyOverride, controllerLimited;
	boolean failed;

	
	public P51SingleABSControler()
	{
		this.canID = 32;
		this.controller = new TalonSRX(this.canID);
		//set the PID variables
		this.config_kD=1;
		this.config_kI=.0002;
		this.config_kP=7;
		
		this.minSoftLimit=-500;
		this.maxSoftLimit=900;
		
		this.softLimitRange = .10;
		this.currentSetPos = -150;

		this.homePos = this.currentSetPos;
	
		this.controller.setInverted(false);
		this.controller.setSensorPhase(true);
		//start all of our errors and warnings as false
		this.errorSpeed = this.errorTemp = this.errorCurrent = this.errorMinSoftLimit = this.errorMaxSoftLimit =
		this.errorMinHardLimit = this.errorMaxHardLimit = this.failed = this.warningSpeed = this.warningTemp =
		this.warningCurrent = this.warningMinSoftLimit = this.warningMaxSoftLimit = this.warningMinHardLimit =
		this.warningMaxHardLimit = this.safetyOverride = this.controllerLimited = this.verboseLogging = false;

		

	
	}
	public P51SingleABSControler(	int canID, int minSoftLimit,int maxSoftLimit,double softLimitRange,double runningCurrent,
									double maxCurrent, double currentRange, double runningTemp, double maxTemp,
									double tempRange,double runningSpeed,double maxSpeed,double speedRange){
		//Super();
		
		this.canID=canID;
		this.minSoftLimit=minSoftLimit;
		this.maxSoftLimit=maxSoftLimit;
		this.softLimitRange=softLimitRange;
		this.runningCurrent=runningCurrent;
		this.maxCurrent=maxCurrent;
		this.currentRange=currentRange;
		this.runningTemp=runningTemp;
		this.maxTemp=maxTemp;
		this.tempRange=tempRange;
		this.runningSpeed=runningSpeed;
		this.maxSpeed=maxSpeed;
		this.speedRange=speedRange;
		this();

		//set the soft limits for the speed controller
		this.controller.configForwardSoftLimitThreshold(this.maxSoftLimit, 10);
		this.controller.configForwardSoftLimitEnable(true, 10);
		this.controller.configReverseSoftLimitThreshold(this.minSoftLimit, 10);
		this.controller.configReverseSoftLimitEnable(true, 10);
		//Set the Feedback to be an Absolutite encoder								
		this.controller.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);
		
		//set the PID constants on the Speed Controller								
		this.controller.config_kP(0, this.config_kP, 10);
		this.controller.config_kI(0, this.config_kI, 10);
		this.controller.config_kD(0, this.config_kD, 10);
	}
<<<<<<< HEAD:P51Controlers/P51SingleABSControler.java
	/**
	 * 🎵 we can check if we want to 🎵
	 */
=======
	
>>>>>>> ef0788c45406855b4e5d2ae238247e33dbfcda5c:P51Controlers/P51SingleABSControler.java
	private void checkSafety()
	{
		//todo add checks for all safety items, set safety flags, and call limiting if maxes are hit
	}
	/**
	 * in the event of an error or warning we need to place limits on the amount of power the controler is allowed to give out
	 */
	private void toggleLimitController(){
		//ToDo: add a method for limiting the speed controller
		//? maybe voltage limit it while its in the error state?
		//tallons have a auto current limiting?
		this.controllerLimited = true;
<<<<<<< HEAD:P51Controlers/P51SingleABSControler.java

	}
	
	/**
	 * once a warning or error is over we need to switch the controller back to normal operation
	 */
	private void unLimitController(){

		this.controllerLimited = false;
	}
	/**
	 * if the PID goes all wrong turn it off and change to vprecent control
	 */
	public void toggleManualOverride()
	{

	}
	/**
	 * if you are in manual control and are able to change back to PID Control
	 */
	public void togglePIDControl()
	{
=======
>>>>>>> ef0788c45406855b4e5d2ae238247e33dbfcda5c:P51Controlers/P51SingleABSControler.java

	}
	
	/**
	 * once a warning or error is over we need to switch the controller back to normal operation
	 */
	private void unLimitcontroller(){

		this.controllerLimited = false;
	}
	
	public void toggleManualOverride()
	{

	}


	public void setPos(int pos)
	{
		this.currentSetPos = pos;
	}
	/**
	 * @
	 * 
	 * 
	 * 
	 * 
	 */
	public boolean control(double input)
	{
		this.checkSafety();
		if (this.failed == true 
			|| this.controller.getSelectedSensorPosition(0) < this.minSoftLimit-this.minSoftLimit*this.softLimitRange 
			|| this.controller.getSelectedSensorPosition(0) > this.maxSoftLimit+this.errorMaxSoftLimit*this.softLimitRange)
		{
			
			//turn off soft limits as something is wrong with the limits
			this.controller.configForwardSoftLimitEnable(false, 10);
			this.controller.configReverseSoftLimitEnable(false, 10);
			// change to vcontrol
			this.controller.set(ControlMode.PercentOutput, input);
			
			this.failed = true;
			//turn on breaking to assist driver in operaton
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
