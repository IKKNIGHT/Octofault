package com.ikknight.octofault.utils.monitors;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.ikknight.octofault.utils.LoggingStream;

/**
 * Monitors ColorSensor devices for faults and anomalies.
 *
 * Supported devices:
 * - [ColorSensor]
 * - [RevColorSensorV3]
 *
 * Detects the following issues:
 * - Negative or NaN color values
 * - I2C communication failures
 * - White balance inconsistencies (RevColorSensorV3 only)
 *
 * @see DeviceMonitor
 */
public class ColorSensorMonitor extends DeviceMonitor<ColorSensor> {

    /**
     * Creates a new ColorSensorMonitor.
     *
     * @param name The hardware map name of the color sensor
     * @param device The color sensor device instance
     */
    public ColorSensorMonitor(String name, ColorSensor device) {
        super(name, device);
    }

    @Override
    public void update() {
        boolean errorsInThisUpdate = false;

        try {
            int red = device.red();
            int green = device.green();
            int blue = device.blue();
            int alpha = device.alpha();

            if (red < 0 || green < 0 || blue < 0 || alpha < 0) {
                reportFault(LoggingStream.LogLevel.WARNING, name + ": One or more color values are negative.");
                errorsInThisUpdate = true;
            }

            if (Double.isNaN(red) || Double.isNaN(green) || Double.isNaN(blue) || Double.isNaN(alpha)) {
                reportFault(LoggingStream.LogLevel.WARNING, name + ": Color value is NaN.");
                errorsInThisUpdate = true;
            }

            if (device instanceof RevColorSensorV3) {
                RevColorSensorV3 revSensor = (RevColorSensorV3) device;
                I2cDeviceSynch client = (I2cDeviceSynch) ((I2cDeviceSynchDevice<?>) revSensor).getDeviceClient();

                if (!client.isArmed()) {
                    reportFault(LoggingStream.LogLevel.ERROR, name + ": I2C client not armed (communication lost?)");
                    errorsInThisUpdate = true;
                }

                I2cAddr addr = client.getI2cAddress();
                if (addr == null || addr.get8Bit() == 0) {
                    reportFault(LoggingStream.LogLevel.ERROR, name + ": Invalid or missing I2C address.");
                    errorsInThisUpdate = true;
                }

                int total = red + green + blue;
                if (total > 0) {
                    double rNorm = red / (double) total;
                    double gNorm = green / (double) total;
                    double bNorm = blue / (double) total;

                    double threshold = 0.2;
                    if (Math.abs(rNorm - gNorm) > threshold ||
                            Math.abs(rNorm - bNorm) > threshold ||
                            Math.abs(gNorm - bNorm) > threshold) {
                        reportFault(LoggingStream.LogLevel.INFO, name + ": White balance significantly uneven (possible color cast or lighting issue)");
                        errorsInThisUpdate = true;
                    }
                }
            }

        } catch (Exception e) {
            reportFault(LoggingStream.LogLevel.ERROR, name + ": Exception during color read: " + e.getMessage());
            errorsInThisUpdate = true;
        }

        if (!errorsInThisUpdate) {
            clearFaults();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Object getCurrentValue() {
        try {
            return String.format("R: %d, G: %d, B: %d, A: %d",
                    device.red(), device.green(), device.blue(), device.alpha());
        } catch (Exception e) {
            return "Sensor read failed: " + e.getMessage();
        }
    }

    @Override
    public String getDeviceType() {
        return (device instanceof RevColorSensorV3) ? "RevColorSensorV3" : "ColorSensor";
    }
}