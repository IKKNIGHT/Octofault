package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Monitor class for all devices that implement {@link TouchSensor}, including:
 * <li>TouchSensor</li>
 *
 * This monitor checks for:
 * <ul>
 *     <li>Unexpected NaN or negative values from {@code getValue()}</li>
 *     <li>Unusual pressure readings outside of expected [0.0–1.0]</li>
 * </ul>
 *
 * @see DeviceMonitor
 */
public class TouchSensorMonitor extends DeviceMonitor<TouchSensor> {

    /**
     * Constructor for DeviceMonitor class.
     *
     * @param name   The name of the device being monitored.
     * @param device The device being monitored.
     */
    public TouchSensorMonitor(String name, TouchSensor device) {
        super(name, device);
    }

    /**
     * Checks if the sensor is returning valid values.
     * Touch sensors are simple but still can return invalid or erratic signals if damaged or miswired.
     */
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
