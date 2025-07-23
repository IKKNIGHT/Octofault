package com.ikknight.octofault.utils.monitors;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Monitors distance sensors for invalid readings and communication issues. Supported devices: - DistanceSensor - Rev2mDistanceSensor Checks for: - NaN or negative distance values - Readings outside reasonable range (0–1000cm) - Communication errors
 */
public class DistanceSensorMonitor extends DeviceMonitor<DistanceSensor>{

    /**
     * Creates a distance sensor monitor.
     *
     * @param name Device name from hardware map
     * @param device Distance sensor instance
     */
    public DistanceSensorMonitor(String name, DistanceSensor device) {
        super(name, device);
    }

    @Override
    public void update() {
        try {
            double distance = device.getDistance(DistanceUnit.CM);
            if (Double.isNaN(distance) || distance < 0.0 || distance > 1000.0) {
                reportFault(LoggingStream.LogLevel.WARNING, name + ": Invalid distance reading: " + distance);
            } else {
                clearFaults();
            }
        } catch (Exception e) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Failed to read distance: " + e.getMessage());
        }
    }

    @Override
    public Object getCurrentValue() {
        return "Distance(CM) : "+device.getDistance(DistanceUnit.CM);
    }

    @Override
    public String getDeviceType() {
        if (device instanceof Rev2mDistanceSensor) {
            return "Rev2mDistanceSensor";
        }
        return "Distance Sensor";
    }
}
