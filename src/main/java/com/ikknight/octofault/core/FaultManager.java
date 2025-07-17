package com.ikknight.octofault.core;

import com.ikknight.octofault.utils.LoggingStream;
import com.ikknight.octofault.utils.monitors.DeviceMonitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing device faults.
 * @see DeviceMonitor
 * @see LoggingStream
 */
public class FaultManager {

    private final Map<String, DeviceMonitor<?>> monitors = new HashMap<>();
    private LoggingStream loggingStream;

    /**
     * Constructor for FaultManager class.
     * @param monitor The device monitor to be registered.
     */
    public void register(DeviceMonitor<?> monitor){
        monitors.put(monitor.getName(), monitor);
    }

    /**
     * Set the logging stream to be used for logging faults.
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
     * Set the logging stream to be used for logging faults. DO NOT USE THIS METHOD
     * @param loggingStream The logging stream to be used for logging faults.
     */
    public void setLoggingStream(LoggingStream loggingStream) {
        this.loggingStream = loggingStream;
    }

    /**
     * Get a device monitor by name.
     * @param name The name of the device monitor to get.
     * @return The device monitor with the given name.
     */
    public DeviceMonitor<?> getMonitor(String name) {
        return monitors.get(name);
    }

    /**
     * Get all device monitors.
     * @return All device monitors.
     */
    public Collection<DeviceMonitor<?>> getAllMonitors() {
        return monitors.values();
    }

    /**
     * Get the logging stream to be used for logging faults.
     * @return The logging stream to be used for logging faults.
     */
    public LoggingStream getLoggingStream() {
        return loggingStream;
    }

    /**
     * Remove a device monitor by name.
     * @param name Configuration name of the device monitor to remove.
     */
    public void removeMonitor(String name) {
        monitors.remove(name);
    }

    /**
     * Remove all device monitors.
     */
    public void removeAllMonitors() {
        monitors.clear();
    }

}
