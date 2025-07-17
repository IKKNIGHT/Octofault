package com.ikknight.octofault.utils.monitors;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Monitor class for all devices that extend to DistanceSensor. Being
 * <li>DistanceSensor</li>
 * <li>Rev2mDistanceSensor</li>
 */
public class DistanceSensorMonitor extends DeviceMonitor<DistanceSensor>{
    /**
     * Constructor for DeviceMonitor class.
     *
     * @param name   The name of the device being monitored.
     * @param device The device being monitored.
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
