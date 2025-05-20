// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import java.io.ObjectInputFilter.Config;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.servohub.ServoHub.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkClosedLoopController;
import frc.robot.generated.TunerConstants;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax; 
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;

import frc.robot.RobotContainer;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

   Timer startTime = new Timer();
  //Turning wheel variables
  SparkMax climb1 = new SparkMax(4,MotorType.kBrushless);
  SparkMaxConfig config = new SparkMaxConfig();
  SparkClosedLoopController maxPid = climb1.getClosedLoopController();
  RelativeEncoder encoder = climb1.getEncoder();
  //Controllers
  XboxController controller = new XboxController(0);
  private Joystick fightJoystick = new Joystick (1);

  //Algae control
  private static final int X_BUTTON = 3;
  private static final int R3BUTTON = 10;
  double algae1 = 0.3;
  double algae2 = -0.5;
  private boolean algae = false;

  // Tolerance for stopping the motor

  private static final double LIFTUP = -0.5;
  private static final int YBUTTON = 4;
  private boolean LIFTUP1 = false;
  private boolean isLiftRunning = false; 
  private boolean isForward = true;

  private final RobotContainer m_robotContainer;
  double slowSpeed = 0.7;
  double reallySlowspeed = 0.5;
  Boolean superSlowmode = false;
  Boolean isslowspeed = false;

  PWMVictorSPX lift1 = new PWMVictorSPX(0);
  private static final int ABUTTON = 1;
  VictorSPXConfiguration config2 = new VictorSPXConfiguration();
  boolean isPullingup = false;
  boolean button3statelast = false;
  boolean islift1 = false;
  private boolean lastButtonState = false;

  //Code for coral
  SparkMax coral1 = new SparkMax(2, MotorType.kBrushless);
  private double coral1speed = -0.5; //Coral shoot variable
  private double coral2speed = 0.3; //Coral unstuck variable
  private static final int R1BUTTON = 6;
  private static final int BBUTTON = 2;
  private boolean iscoralrunning = false;
  private double autocorla = -0.45; //Autonomous coral out variable

 

  public Robot() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run(); 
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
    startTime.reset();
    startTime.start();
  }

  @Override
  public void autonomousPeriodic() {
    if(startTime.get()>0 && startTime.get()<=1.99){
      coral1.set(0.0);
    }else if(startTime.get()>1.99 && startTime.get()<=3){
      coral1.set(autocorla);
    } else if( startTime.get()>3 && startTime.get()<=10){
      coral1.set(0.0);
    } else if (startTime.get()>13.5 && startTime.get()<=15){
      coral1.set(autocorla);
    } else if(startTime.get()>15){
      coral1.set(0.0);
    }

    //if(startTime.get()<=5){
      //coral1.set(coral1speed);
    //}else if(startTime.get()> 5 && startTime.get()<=15){
      //coral1.set(0.0);
    //}
  }

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    startTime.stop();
  }

  @Override
  public void teleopPeriodic() {}
    //TODO Moved into RobotContainer.java revert if issues.
  //   if (fightJoystick.getRawButtonPressed(YBUTTON)) {
  //     isLiftRunning = !isLiftRunning; // Toggle motor on/off
  //     if (isLiftRunning) {
  //         isForward = true; // Set direction to forward
  //     }
  //   }
  
  //   if (fightJoystick.getRawButtonPressed(ABUTTON)) {
  //     isLiftRunning = !isLiftRunning; // Toggle motor on/off
  //     if (isLiftRunning) {
  //         isForward = false; // Set direction to reverse
  //     }
  //   }
  
  // // Set motor output based on toggle state
  //   if (isLiftRunning) {
  //     lift1.set(isForward ? 1.0 : -1.0);
  //   } else {
  //     lift1.set(0.0);
  //   }
  //   // Check for button press (transition from not pressed to pressed)
  //   if (fightJoystick.getRawButton(X_BUTTON)){
  //     algae = !algae;
  //     climb1.set(algae1);
  //   } else if (fightJoystick.getRawButton(R3BUTTON)){
  //     climb1.set(algae2);
  //   } else{
  //     climb1.set(0.0);
  //   }
    

  //   //Shoot coral out
  //   if(fightJoystick.getRawButton(BBUTTON)){
  //     iscoralrunning = !iscoralrunning;
  //     coral1.set(coral1speed);
  //   }else if(fightJoystick.getRawButton(R1BUTTON)){
  //     coral1.set(coral2speed);
  //   } else{
  //     coral1.set(0.0);
  //   }
  // }

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}

  @Override
  public void robotInit(){
    

    SmartDashboard.putString("Limelight","http://10:66.20.11:5800/stream.mjpg");
     try {
            CameraServer.startAutomaticCapture(); // Starts the first available USB camera
        } catch (Exception e) {
            DriverStation.reportError("Camera failed to start: " + e.getMessage(), false);
        }
  }
}
