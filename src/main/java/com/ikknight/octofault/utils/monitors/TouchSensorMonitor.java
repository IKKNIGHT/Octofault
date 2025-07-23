package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Monitors touch sensors for invalid readings and hardware faults. Supported devices: - TouchSensor Checks for: - NaN or negative values - Readings outside expected range [0.0–1.0] - Communication errors
 */
public class TouchSensorMonitor extends DeviceMonitor<TouchSensor> {

    /**
     * Creates a touch sensor monitor.
     *
     * @param name Device name from hardware map
     * @param device Touch sensor instance
     */
    public TouchSensorMonitor(String name, TouchSensor device) {
        super(name, device);
    }

    @Override
    public void update() {
        boolean errorsInThisUpdate = false;

        try {
            double value = device.getValue();

            if (Double.isNaN(value)) {
                reportFault(LoggingStream.LogLevel.ERROR, name + ": Touch sensor reading is NaN.");
                errorsInThisUpdate = true;
            } else if (value < 0.0 || value > 1.0) {
                reportFault(LoggingStream.LogLevel.WARNING, name + ": Touch sensor value out of expected range [0.0–1.0].");
                errorsInThisUpdate = true;
            }

        } catch (Exception e) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Exception during touch sensor read: " + e.getMessage());
            errorsInThisUpdate = true;
        }

        if (!errorsInThisUpdate) {
            clearFaults();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Object getCurrentValue() {
        return String.format("Touched: %s, Value: %.2f", device.isPressed(), device.getValue());
    }

    @Override
    public String getDeviceType() {
        return "Touch Sensor";
    }
}