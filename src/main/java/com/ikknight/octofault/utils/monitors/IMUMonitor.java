package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/**
 * Monitors IMU sensors for orientation and angular velocity issues.
 * Supported devices:
 * - IMU (universal interface for BNO055, BHI260AP, etc.)
 * Checks for:
 * - NaN orientation values (yaw/pitch/roll)
 * - Null angular velocity data
 * - Communication errors
 */
public class IMUMonitor extends DeviceMonitor<IMU> {

    /**
     * Creates an IMU monitor.
     *
     * @param name Device name from hardware map
     * @param device IMU instance
     */
    public IMUMonitor(String name, IMU device) {
        super(name, device);
    }

    @Override
    public void update() {
        boolean errorsInThisUpdate = false;

        try {
            AngularVelocity velocity = device.getRobotAngularVelocity(AngleUnit.DEGREES);
            if (velocity == null) {
                reportFault(LoggingStream.LogLevel.WARNING, name + ": Angular velocity data unavailable");
                errorsInThisUpdate = true;
            }

            YawPitchRollAngles angles = device.getRobotYawPitchRollAngles();
            if (angles == null) {
                reportFault(LoggingStream.LogLevel.ERROR, name + ": YawPitchRollAngles data unavailable");
                errorsInThisUpdate = true;
            } else {
                double yaw = angles.getYaw(AngleUnit.DEGREES);
                double pitch = angles.getPitch(AngleUnit.DEGREES);
                double roll = angles.getRoll(AngleUnit.DEGREES);

                if (Double.isNaN(yaw) || Double.isNaN(pitch) || Double.isNaN(roll)) {
                    reportFault(LoggingStream.LogLevel.WARNING, name + ": Yaw, Pitch, or Roll is NaN");
                    errorsInThisUpdate = true;
                }
            }

        } catch (Exception e) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Exception during IMU read: " + e.getMessage());
            errorsInThisUpdate = true;
        }

        if (!errorsInThisUpdate) {
            clearFaults();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Object getCurrentValue() {
        try {
            YawPitchRollAngles a = device.getRobotYawPitchRollAngles();
            return String.format("Orientation [°]: Yaw: %.1f, Pitch: %.1f, Roll: %.1f",
                    a.getYaw(AngleUnit.DEGREES),
                    a.getPitch(AngleUnit.DEGREES),
                    a.getRoll(AngleUnit.DEGREES));
        } catch (Exception e) {
            return LoggingStream.LogLevel.DEBUG+": Failed to read IMU: " + e.getMessage();
        }
    }

    @Override
    public String getDeviceType() {
        return "IMU";
    }
}