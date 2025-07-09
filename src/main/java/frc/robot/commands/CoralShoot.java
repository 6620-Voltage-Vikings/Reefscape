package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Coral;

public class CoralShoot extends Command {
    private final Coral coral;
    private final double speed;

    public CoralShoot(Coral coral, double speed) {
        this.coral = coral;
        this.speed = speed;
        addRequirements(coral);
    }

    @Override
    public void execute() {
        coral.shoot(speed);
    }

    @Override
    public void end(boolean interrupted) {
        coral.stop();
    }

    @Override
    public boolean isFinished() {
        return false; // run while held or until interrupted
    }
}