package frc.robot.subsystems;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Elevator extends SubsystemBase {
    
    private static Elevator instance = null;

    private final VictorSPX leader;
    private final VictorSPX follower;

    private final double kG = 0.06; // Change made: 0.06 Previous 0.08 voltage to overcome force of gravity
    private final double kS = 0.002; // Change from: 0.002 voltage to overcome static friction

    private double setpointPercent = 0.0;

    private Elevator() {
        leader = new VictorSPX(11);
        follower = new VictorSPX(12);

        follower.set(VictorSPXControlMode.Follower, 11);
    } 

    public static Elevator getInstance() {
        if (instance == null) {
            instance = new Elevator();
        }

        return instance;
    }

    @Override
    public void periodic() {
        leader.set(VictorSPXControlMode.PercentOutput, -1 * (setpointPercent + kG + Math.copySign(kS, setpointPercent)));
    }

    public Command setControl(double percent) {
        return Commands.runOnce(() -> {
            setpointPercent = percent;
        });
    }
}