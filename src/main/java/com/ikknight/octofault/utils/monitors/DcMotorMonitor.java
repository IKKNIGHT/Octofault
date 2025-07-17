package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.*;

/**
 * Monitor class for all devices that extend to DcMotorSimple. Being
 * <li>DcMotor</li>
 * <li>DcMotorEx</li>
 * <li>CRServo</li>
 * <li>CRServoImpl</li>
 * <li>CRServoImplEx</li>
 *
 * @see DeviceMonitor
 */
public class DcMotorMonitor extends DeviceMonitor<DcMotorSimple> {

    private int lastPosition = 0;
    private long lastTime = System.currentTimeMillis();

    public DcMotorMonitor(String name, DcMotorSimple device) {
        super(name, device);
    }

    @Override
    public void update() {
        boolean errorsInThisUpdate = false;

        double power;
        try {
            power = device.getPower();
        } catch (Exception e) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Failed to read power: " + e.getMessage());
            return;
        }

        if (Double.isNaN(power)) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Power is NaN");
            return;
        }

        long now = System.currentTimeMillis();

        if (device instanceof DcMotorEx motorEx) {
            int currentPosition = motorEx.getCurrentPosition();
            double velocity = motorEx.getVelocity();

            if (Math.abs(power) > 0.05) {
                if (currentPosition == lastPosition && (now - lastTime) > 300) {
                    reportFault(LoggingStream.LogLevel.WARNING, name + ": Encoder not moving despite power (stalled)");
                    errorsInThisUpdate = true;
                }
                if (Math.abs(velocity) < 5) {
                    reportFault(LoggingStream.LogLevel.WARNING, name + ": Velocity too low under power (possible stall)");
                    errorsInThisUpdate = true;
                }
            }

            lastPosition = currentPosition;
            lastTime = now;
        }

        else if (device instanceof DcMotor motor) {
            if (motor.isBusy() && Math.abs(power) < 0.15) {
                reportFault(LoggingStream.LogLevel.WARNING, name + ": Motor is busy but power is significantly low.");
                errorsInThisUpdate = true;
            }
        }

        else if (device instanceof CRServo servo) {
            if (Math.abs(power) > 0.5) {
                reportFault(LoggingStream.LogLevel.INFO, name + ": CRServo power high, but no encoder feedback available.");
                errorsInThisUpdate = true;
            }

            if (servo instanceof CRServoImplEx advancedServo) {
                try {
                    boolean pwm = advancedServo.isPwmEnabled();
                    if (!pwm) {
                        reportFault(LoggingStream.LogLevel.ERROR, name + ": PWM is not enabled for CRServoImplEx");
                        errorsInThisUpdate = true;
                    }
                } catch (Exception e) {
                    reportFault(LoggingStream.LogLevel.ERROR, name + ": Error reading PWM status: " + e.getMessage());
                    errorsInThisUpdate = true;
                }
            }
        }

        if (!errorsInThisUpdate) {
            clearFaults();
        }
    }



    @SuppressLint("DefaultLocale")
    @Override
    public Object getCurrentValue() {
        if (device instanceof DcMotorEx ex) {
            return String.format("Power: %.2f, Pos: %d, Vel: %.2f",
                    ex.getPower(), ex.getCurrentPosition(), ex.getVelocity());
        } else if (device instanceof DcMotor motor) {
            return String.format("Power: %.2f, Pos: %d", motor.getPower(), motor.getCurrentPosition());
        } else {
            return "Power: " + device.getPower();
        }
    }

    @Override
    public String getDeviceType() {
        // return from specific to unspecific for example CrServoImplEx extends CRservo so CrServoImplEx is also an instance of CRServo
        if (device instanceof DcMotorEx) {
            return "DcMotorEx";
        }else if (device instanceof CRServoImplEx){
            return "CRServoImplEx";
        }else if (device instanceof CRServoImpl){
            return "CRServoImpl";
        }else if (device instanceof CRServo){
            return "CRServo";
        }
        return "DcMotor";
    }
}
