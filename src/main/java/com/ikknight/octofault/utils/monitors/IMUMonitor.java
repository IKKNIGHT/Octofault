package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

/**
 * Monitor class for all devices that extend to IMU. Being
 * <li>IMU</li>
 *
 * Coincidentally, IMU is the universal interface, so the need for BNO055 and BHI260AP specific monitors is not needed.
 * Of course, you can always make your own IMU monitor if needed.
 *
 * <p>This monitor checks for:</p>
 * <ul>
 *     <li>NaN orientation (yaw/pitch/roll)</li>
 *     <li>Null angular velocity</li>
 *     <li>Runtime exceptions during read</li>
 * </ul>
 *
 * @see DeviceMonitor
 */
public class IMUMonitor extends DeviceMonitor<IMU> {

    /**
     * Constructor for IMUMonitor class.
     *
     * @param name   The name of the device being monitored.
     * @param device The device being monitored.
     */
    public IMUMonitor(String name, IMU device) {
        super(name, device);
    }

    /**
     * Polls the IMU data and checks for sanity and fault conditions.
     */
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

    /**
     * Returns a string representation of the IMU's current orientation in degrees.
     *
     * @return The orientation values as a formatted string, or a failure message if not available.
     */
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

    /**
     * Returns the device type as a string.
     *
     * @return The string "IMU".
     */
    @Override
    public String getDeviceType() {
        return "IMU";
    }
}
