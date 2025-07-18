package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.ikknight.octofault.utils.LoggingStream;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.ServoImplEx;

/**
 * Monitors servo motors for position and PWM issues.
 *
 * Supported devices:
 * - Servo
 * - ServoImpl
 * - ServoImplEx
 *
 * Checks for:
 * - Position values outside [0.0–1.0] range
 * - PWM status
 * - Communication errors
 */
public class ServoMonitor extends DeviceMonitor<Servo> {

    /**
     * Creates a servo monitor.
     *
     * @param name Device name from hardware map
     * @param device Servo instance
     */
    public ServoMonitor(String name, Servo device) {
        super(name, device);
    }

    @Override
    public void update() {
        boolean errorsInThisUpdate = false;

        if (device.getPosition() < 0.0 || device.getPosition() > 1.0 || Double.isNaN(device.getPosition())) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Position out of range (0.0–1.0), Or Servo position is unknown. (either undefined or the servo position is not set.)");
            errorsInThisUpdate = true;
        }

        if (device instanceof ServoImplEx) {
            ServoImplEx advancedServo = (ServoImplEx) device;
            try {
                boolean pwm = advancedServo.isPwmEnabled();
                if (!pwm) {
                    reportFault(LoggingStream.LogLevel.ERROR, name + ": PWM is not enabled for ServoImplEx");
                    errorsInThisUpdate = true;
                }
            }catch (Exception e){
                reportFault(LoggingStream.LogLevel.ERROR, name + ": Error reading PWM status: " + e.getMessage());
                errorsInThisUpdate = true;
            }
        }else {
            if (!device.getController().getPwmStatus().equals(ServoController.PwmStatus.ENABLED)){
                reportFault(LoggingStream.LogLevel.ERROR, name + ": PWM is not enabled for Servo");
                errorsInThisUpdate = true;
            }
        }

        if(!errorsInThisUpdate){
            clearFaults();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Object getCurrentValue() {
        boolean pwmEnabled;
        if (device instanceof ServoImplEx) {
            pwmEnabled = ((ServoImplEx) device).isPwmEnabled();
        } else {
            pwmEnabled = device.getController().getPwmStatus().equals(ServoController.PwmStatus.ENABLED);
        }

        return String.format("Target Position: %.2f, PWM Enabled: %s",
                device.getPosition(),
                pwmEnabled);
    }

    @Override
    public String getDeviceType() {
        if(device instanceof ServoImplEx){
            return "ServoImplEx";
        }else if(device instanceof ServoImpl){
            return "ServoImpl";
        }
        return "Servo";
    }
}