package com.ikknight.octofault.core;

import com.ikknight.octofault.utils.LoggingStream;
import com.ikknight.octofault.utils.monitors.DeviceMonitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Central manager for device monitoring and fault handling.
 * Manages a collection of device monitors and coordinates fault logging
 * through a configurable logging stream.
 *
 * @see DeviceMonitor
 * @see LoggingStream
 */
public class FaultManager {

    private final Map<String, DeviceMonitor<?>> monitors = new HashMap<>();
    private LoggingStream loggingStream;

    /**
     * Registers a device monitor with the fault manager.
     *
     * @param monitor The device monitor to register
     */
    public void register(DeviceMonitor<?> monitor){
        monitors.put(monitor.getName(), monitor);
    }

    /**
     * Updates all registered monitors and logs any detected faults.
     */
    public void updateAll(){
        for (DeviceMonitor<?> monitor : monitors.values()){
            monitor.update();
            if (!monitor.isHealthy() && loggingStream != null){
                loggingStream.log(monitor.getName() + " is faulty: " + monitor.getFaultReasons().toString()); // [FaultA, FaultB, FaultC, ...]
            }
        }
    }

    /**
     * Sets the logging stream for fault reporting.
     *
     * @param loggingStream The logging stream to use for fault reporting
     * @deprecated Use dependency injection instead
     */
    @Deprecated
    public void setLoggingStream(LoggingStream loggingStream) {
        this.loggingStream = loggingStream;
    }

    /**
     * Retrieves a device monitor by name.
     *
     * @param name The name of the device monitor
     * @return The device monitor, or null if not found
     */
    public DeviceMonitor<?> getMonitor(String name) {
        return monitors.get(name);
    }

    /**
     * Gets all registered device monitors.
     *
     * @return Collection of all device monitors
     */
    public Collection<DeviceMonitor<?>> getAllMonitors() {
        return monitors.values();
    }

    /**
     * Gets the current logging stream.
     *
     * @return The current logging stream, or null if not set
     */
    public LoggingStream getLoggingStream() {
        return loggingStream;
    }

    /**
     * Removes a device monitor by name.
     *
     * @param name The configuration name of the device monitor to remove
     */
    public void removeMonitor(String name) {
        monitors.remove(name);
    }

    /**
     * Removes all registered device monitors.
     */
    public void removeAllMonitors() {
        monitors.clear();
    }
}