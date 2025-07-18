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
 * Class for identifying devices in the hardwaremap and registering them for sanitization.
 */
public abstract class DeviceRegistrator {

    private final HardwareMap hardwareMap;
    private final FaultManager faultManager;

    /**
     * Constructor for DeviceRegistrator class, pass in the hardwareMap after you have added all your devices to it.
     * @param hardwareMap The hardware map of the robot.(with the devices added)
     * @param faultManager The fault manager of the robot.
     */
    public DeviceRegistrator(HardwareMap hardwareMap, FaultManager faultManager){
        this.hardwareMap = hardwareMap;
        this.faultManager = faultManager;
    }

    /**
     * Register a device to be monitored.
     * @see HardwareDevice
     * @see DeviceMonitor
     * @param device The device to be registered.
     */
    public void registerDevice(HardwareDevice device){
        Set<String> names = hardwareMap.getNamesOf(device);
        if (names.isEmpty()) return;
        String name = names.iterator().next();

        DeviceMonitor<?> monitor = null;

        // assign the monitor with a corresponding device
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
            monitor = registerCustomDevices(device); // if i didn't make a monitor for this device, register the user's custom device here
        }

        if (monitor != null) {
            faultManager.register(monitor);
            faultManager.getLoggingStream().log(LoggingStream.LogLevel.INFO,"Registered Device with the name of {"+name+"}, with monitor type: "+monitor.getDeviceType());
        }

    }

    /**
     * Register a custom device to be monitored. eg : <pre>{@code if (device instanceOf DcMotorSimple){return new DcMotorMonitor(name, (DcMotorSimple) device);}}</pre> Where DcMotorSimple is the device type class that you want to monitor. And DcMotorMonitor is your custom Monitor class that <pre>{@code extends DeviceMonitor<ClassType>}</pre> Consider making a pull request if you wrote one for a new sensor/device.
     * @see HardwareDevice
     * @see DeviceMonitor
     * @see DeviceRegistrator#registerDevice(HardwareDevice)
     * @param device The device to be registered.
     * @return The device monitor for the device. that extends <pre>{@code DeviceMonitor<ClassType>}</pre>
     */
    public abstract DeviceMonitor<?> registerCustomDevices(HardwareDevice device);

    /**
     * Register all devices of a certain type to be monitored.
     * @see HardwareDevice
     * @param deviceClass The device type to be registered.
     */
    public <T extends HardwareDevice> void registerAllDevicesOfType(Class<T> deviceClass) {
        for (T device : hardwareMap.getAll(deviceClass)) {
            registerDevice(device);
        }
    }

    /**
     * Register all DcMotor devices to be monitored.
     * @see HardwareDevice
     * @param motorClass The device type to be registered.
     */
    public <T extends DcMotorSimple> void registerAllMotorsOfType(Class<T> motorClass) {
        for (T motor : hardwareMap.getAll(motorClass)) {
            registerDevice(motor);
        }
    }

    /**
     * Register all devices to be monitored.
     */
    public void registerAllDevices(){

        // register DcMotorSimple devices (DcMotor, DcMotorEx, CRServo, CRServoImpl, CRServoImplEx)
        registerAllDevicesOfType(DcMotorEx.class);
        registerAllDevicesOfType(DcMotor.class);
        registerAllDevicesOfType(CRServo.class);
        registerAllMotorsOfType(CRServoImpl.class);
        registerAllMotorsOfType(CRServoImplEx.class);

        // all other devices extend HardwareDevice
        registerAllDevicesOfType(HardwareDevice.class); // all hardware devices
    }

}


