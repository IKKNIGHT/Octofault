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
 * Class for managing device faults.
 * Main paradigm for octofault, please call constructors after all devices are registered on a hardwaremap.
 */
public class Octofault {
    HardwareMap hardwareMap;
    LoggingStream loggingStream;
    FaultManager faultManager;
    DeviceRegistrator registrator;

    /**
     * Constructor for Octofault class.
     * @see DeviceRegistrator
     * @see FaultManager
     * @see LoggingStream
     * @param hardwareMap The hardware map of the robot.
     * @param telemetry The telemetry to log to.
     */
    public Octofault(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.loggingStream = new TelemetryLoggingStream(telemetry);

        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream); // You'll need to make this public or expose a method

        registrator = new DeviceRegistrator(hardwareMap, faultManager) {
            @Override
            public DeviceMonitor<?> registerCustomDevices(HardwareDevice device) {
                // Register custom devices here
                return null;
            }
        };
        registrator.registerAllDevices();
    }

    /**
     * Constructor for Octofault class.
     * @see DeviceRegistrator
     * @see FaultManager
     * @see LoggingStream
     * @param hardwareMap The hardware map of the robot.
     * @param loggingStream The logging stream to log to.
     */
    public Octofault(HardwareMap hardwareMap, LoggingStream loggingStream) {
        this.hardwareMap = hardwareMap;
        this.loggingStream = loggingStream;
        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream);
        registrator = new DeviceRegistrator(hardwareMap, faultManager) {
            @Override
            public DeviceMonitor<?> registerCustomDevices(HardwareDevice device) {
                // Register custom devices here
                return null;
            }
        };
        registrator.registerAllDevices();
    }

    /**
     * Constructor for Octofault class. Remember to set your fault managers' logging stream.
     * @see FaultManager#setLoggingStream(LoggingStream)
     * @see FaultManager
     * @see DeviceRegistrator
     * @see LoggingStream
     * @param hardwareMap The hardware map of the robot.
     * @param faultManager The fault manager to use.
     */
    public Octofault(HardwareMap hardwareMap, FaultManager faultManager) {
        this.hardwareMap = hardwareMap;
        this.faultManager = faultManager;
        registrator = new DeviceRegistrator(hardwareMap, faultManager) {
            @Override
            public DeviceMonitor<?> registerCustomDevices(HardwareDevice device) {
                // Register custom devices here
                return null;
            }
        };
        registrator.registerAllDevices();
    }

    /**
     * Constructor for Octofault class. Be sure to initialise your deviceRegistrator before/after calling the constructor
     * @see DeviceRegistrator
     * @param hardwareMap The hardware map of the robot.
     * @param telemetry The telemetry for octofault to log to.
     * @param registrator The device registrator to use.
     */
    public Octofault(HardwareMap hardwareMap, Telemetry telemetry, DeviceRegistrator registrator){
        this.hardwareMap = hardwareMap;
        this.loggingStream = new TelemetryLoggingStream(telemetry);

        this.faultManager = new FaultManager();
        this.faultManager.setLoggingStream(loggingStream); // You'll need to make this public or expose a method
        this.registrator = registrator; // assuming the user wants to register his own devices
    }

    /**
     * Constructor for Octofault class. Be sure to initialise your deviceRegistrator before/after calling the constructor.
     * @param hardwareMap The hardware map of the robot.
     * @param loggingStream The logging stream to log to.
     * @param registrator The device registrator to use.
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
     * Constructor for Octofault class. Be sure to initialise your deviceRegistrator before/after calling the constructor.Be sure to set your fault managers' logging stream.
     * @param hardwareMap The hardware map of the robot.
     * @param faultManager The fault manager to use.
     * @param registrator The device registrator to use.
     */
    public Octofault(HardwareMap hardwareMap, FaultManager faultManager, DeviceRegistrator registrator){
        this.hardwareMap = hardwareMap;
        this.faultManager = faultManager;
        this.registrator = registrator;
    }

    /**
     * Must be called regularly (e.g. inside loop()) to update all monitors.
     */
    public void update() {
        // get time
        long startTime = System.currentTimeMillis();
        faultManager.updateAll();
        faultManager.getLoggingStream().log(LoggingStream.LogLevel.INFO, "FaultManager took " + (System.currentTimeMillis() - startTime) + "ms to update (Tick Complete)");
    }

    /**
     * Get the fault manager.
     * @return The fault manager.
     */
    public FaultManager getFaultManager(){
        return faultManager;
    }

    /**
     * Get the device registrator
     * @return The device registrator
     */
    public DeviceRegistrator getDeviceRegistrator(){
        return registrator;
    }
    
}
