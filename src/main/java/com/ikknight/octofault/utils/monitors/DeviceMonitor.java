package com.ikknight.octofault.utils.monitors;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for monitoring hardware devices and detecting faults. Extend this class to create monitors for specific device types.
 *
 * @param <T> The type of hardware device being monitored
 */
public abstract class DeviceMonitor<T extends HardwareDevice> {
    protected final String name;
    protected final T device;
    protected boolean isFaulty = false;
    protected final List<String> faultReasons = new ArrayList<>();

    /**
     * Creates a device monitor.
     *
     * @param name Device name from hardware map
     * @param device Hardware device instance
     */
    public DeviceMonitor(String name, T device) {
        this.name = name;
        this.device = device;
    }

    /**
     * Checks device health and updates fault status. Called regularly by the FaultManager to monitor device state.
     */
    public abstract void update();

    /**
     * Gets the device name.
     *
     * @return Device name from hardware map
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the monitored device.
     *
     * @return Hardware device instance
     */
    public T getDevice() {
        return device;
    }

    /**
     * Checks if the device is operating normally.
     *
     * @return True if no faults detected, false otherwise
     */
    public boolean isHealthy() {
        return !isFaulty;
    }

    /**
     * Gets all current fault reasons.
     *
     * @return List of fault descriptions
     */
    public List<String> getFaultReasons() {
        return faultReasons;
    }

    /**
     * Reports a fault condition.
     *
     * @param reason Description of the fault
     */
    protected void reportFault(String reason) {
        isFaulty = true;
        faultReasons.add(reason);
    }

    /**
     * Reports a fault condition with severity level.
     *
     * @param severity Fault severity level
     * @param reason Description of the fault
     */
    protected void reportFault(LoggingStream.LogLevel severity, String reason){
        isFaulty = true;
        faultReasons.add(severity.toString() + ": " + reason);
    }

    /**
     * Clears all fault conditions.
     */
    protected void clearFaults() {
        isFaulty = false;
        faultReasons.clear();
    }

    /**
     * Gets current device state for debugging.
     *
     * @return Formatted string with device status
     */
    public abstract Object getCurrentValue();

    /**
     * Gets the device type name. Return from most specific to least specific (e.g., "DcMotorEx" rather than "DcMotor").
     *
     * @return Device type identifier
     */
    public abstract String getDeviceType();
}