package com.ikknight.octofault.utils.monitors;

import com.qualcomm.robotcore.hardware.VoltageSensor;

/**
 *  Voltage Sensor Monitor, used to monitor voltage sensors.
 */
public class VoltageSensorMonitor extends DeviceMonitor<VoltageSensor> {
    /**
     * Creates a device monitor.
     *
     * @param name   Device name from hardware map
     * @param device Hardware device instance
     */
    public VoltageSensorMonitor(String name, VoltageSensor device) {
        super(name, device);
    }

    @Override
    public void update() {
        
    }

    @Override
    public Object getCurrentValue() {
        return device.getVoltage();
    }

    @Override
    public String getDeviceType() {
        return "Voltage Sensor";
    }
}
