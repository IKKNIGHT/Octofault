package com.ikknight.octofault;

import com.ikknight.octofault.core.FaultManager;
import com.ikknight.octofault.utils.DeviceRegistrator;
import com.ikknight.octofault.utils.LoggingStream;
import com.ikknight.octofault.utils.loggingstreams.TelemetryLoggingStream;
import com.ikknight.octofault.utils.monitors.DeviceMonitor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Main class for hardware fault detection and monitoring.
 * Initialize after all devices are registered on the HardwareMap.
 * Call {@link #update()} regularly in your main loop to monitor device health.
 */
public class Octofault {
    HardwareMap hardwareMap;
    LoggingStream loggingStream;
    FaultManager faultManager;
    DeviceRegistrator registrator;

    /**
     * Creates Octofault with telemetry logging.
     *
     * @param hardwareMap Robot's hardware map with all devices
     * @param telemetry Telemetry instance for logging
     */
    public Octofault(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.loggingStream = new TelemetryLoggingStream(telemetry);

        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream);

        registrator = new DeviceRegistrator(hardwareMap, faultManager) {
            @Override
            public DeviceMonitor<?> registerCustomDevices(HardwareDevice device) {
                return null;
            }
        };
        registrator.registerAllDevices();
    }

    /**
     * Creates Octofault with custom logging stream.
     *
     * @param hardwareMap Robot's hardware map with all devices
     * @param loggingStream Custom logging implementation
     */
    public Octofault(HardwareMap hardwareMap, LoggingStream loggingStream) {
        this.hardwareMap = hardwareMap;
        this.loggingStream = loggingStream;
        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream);
        registrator = new DeviceRegistrator(hardwareMap, faultManager) {
            @Override
            public DeviceMonitor<?> registerCustomDevices(HardwareDevice device) {
                return null;
            }
        };
        registrator.registerAllDevices();
    }

    /**
     * Creates Octofault with custom fault manager.
     * Remember to configure the fault manager's logging stream.
     *
     * @param hardwareMap Robot's hardware map with all devices
     * @param faultManager Pre-configured fault manager
     */
    public Octofault(HardwareMap hardwareMap, FaultManager faultManager) {
        this.hardwareMap = hardwareMap;
        this.faultManager = faultManager;
        registrator = new DeviceRegistrator(hardwareMap, faultManager) {
            @Override
            public DeviceMonitor<?> registerCustomDevices(HardwareDevice device) {
                return null;
            }
        };
        registrator.registerAllDevices();
    }

    /**
     * Creates Octofault with custom device registrator and telemetry.
     * Initialize the device registrator before or after calling this constructor.
     *
     * @param hardwareMap Robot's hardware map with all devices
     * @param telemetry Telemetry instance for logging
     * @param registrator Custom device registrator
     */
    public Octofault(HardwareMap hardwareMap, Telemetry telemetry, DeviceRegistrator registrator){
        this.hardwareMap = hardwareMap;
        this.loggingStream = new TelemetryLoggingStream(telemetry);

        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream);
        this.registrator = registrator;
    }

    /**
     * Creates Octofault with custom device registrator and logging stream.
     *
     * @param hardwareMap Robot's hardware map with all devices
     * @param loggingStream Custom logging implementation
     * @param registrator Custom device registrator
     */
    public Octofault(HardwareMap hardwareMap, LoggingStream loggingStream, DeviceRegistrator registrator){
        this.hardwareMap = hardwareMap;
        this.loggingStream = loggingStream;
        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream);
        this.registrator = registrator;
        registrator.registerAllDevices();
    }

    /**
     * Creates Octofault with custom fault manager and device registrator.
     * Configure the fault manager's logging stream before use.
     *
     * @param hardwareMap Robot's hardware map with all devices
     * @param faultManager Pre-configured fault manager
     * @param registrator Custom device registrator
     */
    public Octofault(HardwareMap hardwareMap, FaultManager faultManager, DeviceRegistrator registrator){
        this.hardwareMap = hardwareMap;
        this.faultManager = faultManager;
        this.registrator = registrator;
    }

    /**
     * Updates all device monitors and checks for faults.
     * Call this method regularly in your main loop (e.g., inside OpMode's loop()).
     */
    public void update() {
        long startTime = System.currentTimeMillis();
        faultManager.updateAll();
        faultManager.getLoggingStream().log(LoggingStream.LogLevel.INFO, "FaultManager took " + (System.currentTimeMillis() - startTime) + "ms to update (Tick Complete)");
    }

    /**
     * Gets the fault manager instance.
     *
     * @return The fault manager
     */
    public FaultManager getFaultManager(){
        return faultManager;
    }

    /**
     * Gets the device registrator instance.
     *
     * @return The device registrator
     */
    public DeviceRegistrator getDeviceRegistrator(){
        return registrator;
    }
}