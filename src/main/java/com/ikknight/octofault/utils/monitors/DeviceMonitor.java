package com.ikknight.octofault.utils.monitors;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for device monitoring.
 * @param <T> The type of device being monitored.
 */
public abstract class DeviceMonitor<T extends HardwareDevice> {
    protected final String name;
    protected final T device;
    protected boolean isFaulty = false;
    protected final List<String> faultReasons = new ArrayList<>();

    /**
     * Constructor for DeviceMonitor class.
     *
     * @param name   The name of the device being monitored.
     * @param device The device being monitored.
     */
    public DeviceMonitor(String name, T device) {
        this.name = name;
        this.device = device;
    }

    /**
     * Poll data + run checks.
     */
    public abstract void update();  // Poll data + run checks

    /**
     * Returns the name of the device being monitored.
     *
     * @return The name of the device.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the device being monitored.
     *
     * @return The device.
     */
    public T getDevice() {
        return device;
    }

    /**
     * Returns whether the device is faulty.
     *
     * @return True if the device is faulty, false otherwise.
     */
    public boolean isHealthy() {
        return !isFaulty;
    }

    /**
     * Returns the list of fault reasons.
     *
     * @return The list of fault reasons.
     */
    public List<String> getFaultReasons() {
        return faultReasons;
    }

    /**
     * Reports a fault to the device.
     *
     * @param reason The reason for the fault.
     */
    protected void reportFault(String reason) {
        isFaulty = true;
        faultReasons.add(reason);
    }

    /**
     * Reports a fault to the device.
     *
     * @param severity The severity of the fault.
     * @param reason The reason for the fault.
     **/
    protected void reportFault(LoggingStream.LogLevel severity, String reason){
        isFaulty = true;
        faultReasons.add(severity.toString() + ": " + reason);
    }


    /**
     * Clears all fault reasons.
     */
    protected void clearFaults() {
        isFaulty = false;
        faultReasons.clear();
    }

    /**
     * Clears all fault reasons.
     */
    public abstract Object getCurrentValue();  // For debugging/logging

    /**
     * Returns the type of device being monitored.
     * also please return from specific to unspecific for example CrServoImplEx extends CRservo so CrServoImplEx is also an instance of CRServo
     * @return The type of device being monitored. for example {@code DistanceSensorMonitor} would return {@code "DistanceSensor"} etc.
     */
    public abstract String getDeviceType();
}
