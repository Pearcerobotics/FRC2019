/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1745.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

public class Robot extends TimedRobot
{
	private SendableChooser<String> s_chooser = new SendableChooser<>();
	private SendableChooser<String> t_chooser = new SendableChooser<>();
	private SendableChooser<String> p_chooser = new SendableChooser<>();

	DriveTrain driveTrain;
	Arm arm;
	Lift lift;
	Intake intake;
	Winch winch;
	Trajectory lTrajectory, rTrajectory;
	EncoderFollower lEncoder = new EncoderFollower(), rEncoder = new EncoderFollower();
	AHRS navX;
	
	long deployTime = 0;
	
	double lastVel = 0;

	long start;

	boolean autoLine = false;
	boolean sw = false;
	boolean scale = false;

	String fieldLayout;

	Joystick lJoy = new Joystick(0), rJoy = new Joystick(1), sJoy = new Joystick(2);
	
	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit()
	{
		CameraServer.getInstance().startAutomaticCapture();
				
		s_chooser.addObject("Right", "R");
		s_chooser.addObject("Left", "L");
		s_chooser.addDefault("Center", "C");

		t_chooser.addDefault("Switch", "Switch");
		t_chooser.addObject("Scale", "Scale");
		t_chooser.addObject("Line", "Line");

		p_chooser.addDefault("Scale Capable", "yes");
		p_chooser.addObject("Scale inCapable", "no");

		SmartDashboard.putData("Starting Position", s_chooser);
		SmartDashboard.putData("Target Place", t_chooser);
		SmartDashboard.putData("Partner Capacity", p_chooser);

		driveTrain = new DriveTrain();
		arm = new Arm();
		lift = new Lift();
		intake = new Intake();
		winch = new Winch();


		navX = new AHRS(I2C.Port.kMXP);
		
	}

	public void disabledInit()
	{
		
	}

	public void disabledPeriodic()
	{
//		SmartDashboard.putData(s_chooser);
//		SmartDashboard.putData(t_chooser);
//		SmartDashboard.putData(p_chooser);

		fieldLayout = DriverStation.getInstance().getGameSpecificMessage();
		// System.out.println(fieldLayout);
	}

	@Override
	public void autonomousInit()
	{
		
		sw = false;
		scale = false;
		autoLine = false;
		fieldLayout = DriverStation.getInstance().getGameSpecificMessage();
		autonDecision();

		lEncoder.setTrajectory(lTrajectory);
		rEncoder.setTrajectory(rTrajectory);

		lEncoder.configureEncoder(driveTrain.getLeftPos(), 4096, .5);
		lEncoder.configurePIDVA(2, 0, 0, .05, .05);

		rEncoder.configureEncoder(driveTrain.getRightPos(), 4096, .5);
		rEncoder.configurePIDVA(2, 0, 0, .05, .05);

		if (sw)
		{
			arm.setPos(250);
			lift.setPos(10000);
		} else if (scale)
		{
			arm.setPos(800);
			lift.setPos(18000);
		}
		navX.zeroYaw();
		
		lEncoder.reset();
		rEncoder.reset();

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic()
	{
		double l = lEncoder.calculate(driveTrain.getLeftPos());
		double r = rEncoder.calculate(driveTrain.getRightPos());

		double gyro = -navX.getYaw();
		double heading = Pathfinder.r2d(lEncoder.getHeading());

		double angleDifference = Pathfinder.boundHalfDegrees(heading - gyro);
		double turn = 50 * (1 / 80) * angleDifference;
		
		SmartDashboard.putNumber("desiredHeading", Pathfinder.boundHalfDegrees(heading));
		SmartDashboard.putNumber("Gyro", gyro);


		driveTrain.setDrive(l + turn, r - turn);

		controlArm();
		controlLift();

		if (lEncoder.isFinished())
		{
			intake.outtake(.5);
		}
	}

	public void teleopInit()
	{
		
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic()
	{
		double throttle = -lJoy.getY();
		double turn = (Math.abs(throttle) < .05) ? lJoy.getZ() * lJoy.getZ() * lJoy.getZ() / Math.abs(lJoy.getZ()) : lJoy.getZ() * (1.6 * Math.abs(throttle));
		
		driveTrain.setDrive(throttle + turn, throttle - turn);
		
		System.out.println(throttle + " " + turn);
		
		controlArm();
		controlLift();
		controlIntake();
		winch.controlWinch(sJoy.getRawAxis(3));
		if (sJoy.getRawButton(1))
		{
			deployTime = winch.deploy();
		}
		
		if(System.currentTimeMillis() - deployTime > 2000)
		{
			winch.off();
		}

		SmartDashboard.putNumber("LiftPos", lift.rLift.getSelectedSensorPosition(0));
		SmartDashboard.putNumber("LiftSetPos", lift.currentSetPos);
		
		SmartDashboard.putNumber("ArmPos", arm.arm.getSelectedSensorPosition(0));
		
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic()
	{
	}

	public void controlArm()
	{
		if (Math.abs(sJoy.getRawAxis(5)) > .2)
		{
			arm.setPos((int) (arm.getPos() - sJoy.getRawAxis(5) * 22));
		}

		if (sJoy.getRawButton(3))
		{
			arm.setPos(-150);
		} else if (sJoy.getRawButton(4))
		{
			arm.setPos(500);
		} else if (sJoy.getRawButton(2))
		{
			arm.setPos(600);
		}
		
		if(sJoy.getRawButton(8))
		{
			arm.home();
		}

		boolean armFailed = arm.control(-sJoy.getRawAxis(5));
		
		//SmartDashboard.putBoolean("Arm status", armFailed);
		
		
	}

	public void controlLift()
	{
		if (Math.abs(sJoy.getRawAxis(1)) > .2)
		{
			lift.setPos((int) (lift.getPos() - sJoy.getRawAxis(1) * 350));
		}

		if (sJoy.getRawButton(3))
		{
			lift.setPos(500);
		} else if (sJoy.getRawButton(4))
		{
			lift.setPos(500);
		} else if (sJoy.getRawButton(2))
		{
			lift.setPos(18000);
		}

		lift.control();
	}

	public void controlIntake()
	{
		if (sJoy.getRawButton(5) || lJoy.getRawButton(5))
		{
			intake.intake();
		} else if (sJoy.getRawAxis(3) > .05)
		{
			intake.outtake(sJoy.getRawAxis(3));
		} else
		{
			intake.rest();
		}
	}

	public void autonDecision()
	{
		
		
		String start = s_chooser.getSelected();
		String target = t_chooser.getSelected();
		String partnerCapacity = p_chooser.getSelected();

		System.out.println(start);
		System.out.println(target);
		System.out.println(partnerCapacity);
		
		if (target.equals("Switch"))
		{
			sw = true;
			if (fieldLayout.charAt(0) == 'R')
			{
				lTrajectory = Pathfinder.readFromCSV(new File("/usr/RSwitch_left_detailed.csv"));
				System.out.println("loaded");
				System.out.println(lTrajectory.segments[0].heading);
				rTrajectory = Pathfinder.readFromCSV(new File("/usr/RSwitch_right_detailed.csv"));
				System.out.println("right");
			} else if (fieldLayout.charAt(0) == 'L')
			{
				lTrajectory = Pathfinder.readFromCSV(new File("/usr/LSwitch_left_detailed.csv"));
				rTrajectory = Pathfinder.readFromCSV(new File("/usr/LSwitch_right_detailed.csv"));
				System.out.println("left");
			}

		}else if (target.equals("Scale"))
		{
			if (fieldLayout.charAt(1) == 'R')
			{
				if (s_chooser.getSelected().charAt(0) == 'R')
				{
					lTrajectory = Pathfinder.readFromCSV(new File("/usr/RScaleSame_left_detailed.csv"));
					rTrajectory = Pathfinder.readFromCSV(new File("/usr/RScaleSame_right_detailed.csv"));
					scale = true;
				} else if (s_chooser.getSelected().charAt(0) == 'L')
				{
					if (p_chooser.getSelected().equals("no"))
					{
						lTrajectory = Pathfinder.readFromCSV(new File("/usr/RScaleOpp_left_detailed.csv"));
						rTrajectory = Pathfinder.readFromCSV(new File("/usr/RScaleOpp_right_detailed.csv"));
						scale = true;
					} else
					{
						autoLine = true;
					}
				}
			} else if (fieldLayout.charAt(1) == 'L')
			{
				if (s_chooser.getSelected().charAt(0) == 'L')
				{
					lTrajectory = Pathfinder.readFromCSV(new File("/usr/LScaleSame_left_detailed.csv"));
					rTrajectory = Pathfinder.readFromCSV(new File("/usr/LScaleSame_right_detailed.csv"));
					scale = true;
				} else if (s_chooser.getSelected().charAt(0) == 'R')
				{
					if (p_chooser.getSelected().equals("no"))
					{
						lTrajectory = Pathfinder.readFromCSV(new File("/usr/LScaleOpp_left_detailed.csv"));
						rTrajectory = Pathfinder.readFromCSV(new File("/usr/LScaleOpp_right_detailed.csv"));
						scale = true;
					} else
					{
						autoLine = true;
					}
				}
			}
		} else if (target.equals("Line"))
		{
			lTrajectory = Pathfinder.readFromCSV(new File("/usr/AutonLine_left_detailed.csv"));
			rTrajectory = Pathfinder.readFromCSV(new File("/usr/AutonLine_right_detailed.csv"));

			autoLine = true;
		}
		if (autoLine)
		{
			lTrajectory = Pathfinder.readFromCSV(new File("/usr/AutonLine_left_detailed.csv"));
			rTrajectory = Pathfinder.readFromCSV(new File("/usr/AutonLine_right_detailed.csv"));

		}
	}
}
