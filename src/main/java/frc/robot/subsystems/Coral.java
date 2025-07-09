package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Coral extends SubsystemBase {
    private final SparkMax coralMotor;
    
    public Coral() {
        coralMotor = new SparkMax(2, MotorType.kBrushless); // match your port & motor type
    }

    public void shoot(double speed) {
        coralMotor.set(speed);
    }

    public void stop() {
        coralMotor.set(0.0);
    }
    
   
}
