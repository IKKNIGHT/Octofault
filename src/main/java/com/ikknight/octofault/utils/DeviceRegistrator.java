package com.ikknight.octofault.utils;

import com.ikknight.octofault.core.FaultManager;
import com.ikknight.octofault.utils.monitors.ColorSensorMonitor;
import com.ikknight.octofault.utils.monitors.DcMotorMonitor;
import com.ikknight.octofault.utils.monitors.DeviceMonitor;
import com.ikknight.octofault.utils.monitors.DistanceSensorMonitor;
import com.ikknight.octofault.utils.monitors.IMUMonitor;
import com.ikknight.octofault.utils.monitors.ServoMonitor;
import com.ikknight.octofault.utils.monitors.TouchSensorMonitor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.Set;

/**
 * Automatically detects and registers hardware devices for fault monitoring.
 *
 * Pass the HardwareMap after all devices have been configured.
 */
public abstract class DeviceRegistrator {

    private final HardwareMap hardwareMap;
    private final FaultManager faultManager;

    /**
     * Creates a device registrator.
     *
     * @param hardwareMap Hardware map containing all configured devices
     * @param faultManager Fault manager to register monitors with
     */
    public DeviceRegistrator(HardwareMap hardwareMap, FaultManager faultManager){
        this.hardwareMap = hardwareMap;
        this.faultManager = faultManager;
    }

    /**
     * Registers a single device for monitoring.
     *
     * @param device The hardware device to monitor
     */
    public void registerDevice(HardwareDevice device){
        Set<String> names = hardwareMap.getNamesOf(device);
        if (names.isEmpty()) return;
        String name = names.iterator().next();

        DeviceMonitor<?> monitor = null;

        if (device instanceof DcMotorSimple) {
            monitor = new DcMotorMonitor(name, (DcMotorSimple) device);
        } else if (device instanceof Servo){
            monitor = new ServoMonitor(name, (Servo) device);
        } else if (device instanceof IMU){
            monitor = new IMUMonitor(name, (IMU) device);
        }else if (device instanceof DistanceSensor){
            monitor = new DistanceSensorMonitor(name, (DistanceSensor) device);
        }else if (device instanceof ColorSensor){
            monitor = new ColorSensorMonitor(name, (ColorSensor) device);
        }else if (device instanceof TouchSensor){
            monitor = new TouchSensorMonitor(name, (TouchSensor) device);
        }
        else{
            monitor = registerCustomDevices(device);
        }

        if (monitor != null) {
            faultManager.register(monitor);
            faultManager.getLoggingStream().log(LoggingStream.LogLevel.INFO,"Registered Device with the name of {"+name+"}, with monitor type: "+monitor.getDeviceType());
        }
    }

    /**
     * Override this method to register custom device types.
     *
     * Example:
     * ```java
     * if (device instanceof MyCustomSensor) {
     *     return new MyCustomSensorMonitor(name, (MyCustomSensor) device);
     * }
     * return null;
     * ```
     *
     * @param device The hardware device to create a monitor for
     * @return A device monitor, or null if unsupported
     */
    public abstract DeviceMonitor<?> registerCustomDevices(HardwareDevice device);

    /**
     * Registers all devices of a specific type.
     *
     * @param deviceClass The device class to register
     */
    public <T extends HardwareDevice> void registerAllDevicesOfType(Class<T> deviceClass) {
        for (T device : hardwareMap.getAll(deviceClass)) {
            registerDevice(device);
        }
    }

    /**
     * Registers all motor devices of a specific type.
     *
     * @param motorClass The motor class to register
     */
    public <T extends DcMotorSimple> void registerAllMotorsOfType(Class<T> motorClass) {
        for (T motor : hardwareMap.getAll(motorClass)) {
            registerDevice(motor);
        }
    }

    /**
     * Registers all supported devices from the hardware map.
     */
    public void registerAllDevices(){
        registerAllDevicesOfType(DcMotorEx.class);
        registerAllDevicesOfType(DcMotor.class);
        registerAllDevicesOfType(CRServo.class);
        registerAllMotorsOfType(CRServoImpl.class);
        registerAllMotorsOfType(CRServoImplEx.class);
        registerAllDevicesOfType(HardwareDevice.class);
    }
}