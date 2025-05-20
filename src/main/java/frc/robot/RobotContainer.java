// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import java.lang.ModuleLayer.Controller;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Elevator;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class RobotContainer {
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
    private double Slow = 0.25;
   
    private Joystick fightJoystick = new Joystick (1);
    private final CommandXboxController joystick = new CommandXboxController(0);
     // private CommandXboxController controller = new CommandXboxController(0); - Why double?? lol 


    private boolean isLiftRunning = false; 
    private boolean isForward = true;
   
    private static final int YBUTTON = 4;
    private static final int ABUTTON = 1;
    private static final int R1BUTTON = 6;
    private static final int BBUTTON = 2;
    
    private double coral1speed = -0.5; //Coral shoot variable
    private double coral2speed = 0.3; //Coral unstuck variable
   
    private boolean iscoralrunning = false;
   
    PWMVictorSPX lift1 = new PWMVictorSPX(0);
    SparkMax coral1 = new SparkMax(2, MotorType.kBrushless);
    
    /** Elevator subsystem reference */
    private final Elevator elevator;
    
   // NamedCommands.registerCommand("name",Commands.runOnce());

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.RobotCentric forwardStraight = new SwerveRequest.RobotCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);


    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        elevator = Elevator.getInstance();

        NamedCommands.registerCommand("demo", Commands.none());

        autoChooser = AutoBuilder.buildAutoChooser();
        SmartDashboard.putData(autoChooser);
        configureBindings();
    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-joystick.getLeftY() * MaxSpeed*Slow) // Drive forward with negative Y (forward)
                    .withVelocityY(-joystick.getLeftX() * MaxSpeed*Slow)
                    .withRotationalRate(-joystick.getRightX() * MaxAngularRate *Slow) // Drive counterclockwise with negative X (left)
            )
        );

        joystick.a().whileTrue(drivetrain.applyRequest(() -> brake));
        joystick.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-joystick.getLeftY(), -joystick.getLeftX()))
        ));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        joystick.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));



        //TODO Commands for Operator, revert if issues -J
        if (fightJoystick.getRawButtonPressed(YBUTTON)){liftEleUp();}
        if (fightJoystick.getRawButtonPressed(ABUTTON)){lowerEleDown();}
        if (fightJoystick.getRawButtonPressed(BBUTTON)){coral1Shoot();}
        if (fightJoystick.getRawButtonPressed(R1BUTTON)){coral2Shoot();}

        if (isLiftRunning) {
            lift1.set(isForward ? 1.0 : -1.0);
          } else {
            lift1.set(0.0);
          }

        joystick.povUp().onTrue(elevator.setControl(0.2)).onFalse(elevator.setControl(0.0));
        joystick.povDown().onTrue(elevator.setControl(-0.03)).onFalse(elevator.setControl(0.0));
        
        joystick.pov(90).whileTrue(drivetrain.applyRequest(()->
        forwardStraight.withVelocityY(0.0).withVelocityX(-0.5))); //Bot Oriented slow mode
        
        joystick.pov(270).whileTrue(drivetrain.applyRequest(()->
        forwardStraight.withVelocityY(0.0).withVelocityX(0.5))); // Bot Oriented slow mode
        
        drivetrain.registerTelemetry(logger::telemeterize);

        // toggle slow
        joystick.leftBumper().onTrue(Commands.runOnce(() -> {
            if (Slow == 0.25) {
                Slow = 0.75;
            } else {
                Slow = 0.25;
            }
        }));
    }

    //TODO Functions below I moved from Robot.java into here to avoid driving issues. Revert if not working - J
    private void liftEleUp(){
        if (fightJoystick.getRawButtonPressed(YBUTTON)) {
            isLiftRunning = !isLiftRunning; // Toggle motor on/off
            if (isLiftRunning) {
                isForward = true; // Set direction to forward
            }
          }      
    }
    
    private void lowerEleDown(){
    if (fightJoystick.getRawButton(ABUTTON)) {
        isLiftRunning = !isLiftRunning; // Toggle motor on/off
        if (isLiftRunning) {
            isForward = false; // Set direction to reverse
         }
        }
      }
    
    private void coral1Shoot(){
        if(fightJoystick.getRawButton(BBUTTON)){
            iscoralrunning = !iscoralrunning;
            coral1.set(coral1speed);
          } else{
            coral1.set(0.0);
          }
        
    }

    private void coral2Shoot(){
        if(fightJoystick.getRawButton(R1BUTTON)){
            coral1.set(coral2speed);
          } else{
            coral1.set(0.0);
          }
        
    }

    public Command getAutonomousCommand() {
        //return Commands.print("No autonomous command configured");
        return autoChooser.getSelected();
    }
}
