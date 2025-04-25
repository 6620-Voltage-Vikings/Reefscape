package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import com.ctre.phoenix6.swerve.SwerveRequest;

public class TestDriveCommand extends Command {
    private final CommandSwerveDrivetrain m_Swerve;
    private final double speedX;
    private final double speedY;
    private final double durationX;
    private final double durationY;
    private final Timer timerX;
    private final Timer timerY;

    public TestDriveCommand(CommandSwerveDrivetrain swerve, double speedX, double speedY, double durationX, double durationY) {
        m_Swerve = swerve;
        this.speedX = speedY; // Switched
        this.speedY = speedX; // Switched
        this.durationX = durationY; // Switched
        this.durationY = durationX; // Switched
        timerX = new Timer();
        timerY = new Timer();
        addRequirements(m_Swerve);
    }

    @Override
    public void initialize() {
        timerX.reset();
        timerY.reset();
        timerX.start();
        timerY.start();
        System.out.println("TestDriveCommand initialized with durations: " + durationX + " and " + durationY);
    }

    @Override
    public void execute() {
        double currentSpeedX = (timerX.hasElapsed(durationX)) ? 0 : speedX;
        double currentSpeedY = (timerY.hasElapsed(durationY)) ? 0 : speedY;

        m_Swerve.setControl(new SwerveRequest.RobotCentric()
            .withVelocityX(currentSpeedX)  // Lateral movement
            .withVelocityY(currentSpeedY)  // Forward/backward movement
            .withRotationalRate(0)         // No rotation
        );

        System.out.println("TestDriveCommand executing with speedX: " + currentSpeedX + " and speedY: " + currentSpeedY);
    }

    @Override
    public boolean isFinished() {
        return timerX.hasElapsed(durationX) && timerY.hasElapsed(durationY);
    }

    @Override
    public void end(boolean interrupted) {
        m_Swerve.setControl(new SwerveRequest.RobotCentric()
            .withVelocityX(0)
            .withVelocityY(0)
            .withRotationalRate(0)
        );
        timerX.stop();
        timerY.stop();
        System.out.println("TestDriveCommand ended");
    }
}
