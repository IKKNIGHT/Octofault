package com.ikknight.octofault.utils.loggingstreams;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.ikknight.octofault.utils.LoggingStream;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Logging stream for telemetry. This is the default logging stream for Octofault.
 * @see LoggingStream
 */
public class TelemetryLoggingStream extends LoggingStream {
    FtcDashboard dashboard;
    Telemetry telemetry;

    /**
     * Constructor for TelemetryLoggingStream class.
     * @param telemetry The telemetry to log to.
     */
    public TelemetryLoggingStream(Telemetry telemetry) {
        dashboard = FtcDashboard.getInstance();
        if(!dashboard.isEnabled()){
            dashboard = null;
        }
        this.telemetry = telemetry;
    }

    @Override
    public void log(String message) {
        telemetry.addLine(message);
        if (dashboard != null){
            TelemetryPacket packet = new TelemetryPacket();
            packet.addLine(message);
            dashboard.sendTelemetryPacket(packet);
        }
    }

    @Override
    public void log(LogLevel level, String message) {
        telemetry.addLine(level.toString()+": "+message);
        if (dashboard != null){
            TelemetryPacket packet = new TelemetryPacket();
            packet.addLine(level.toString()+": "+message);
            dashboard.sendTelemetryPacket(packet);
        }
    }

    @Override
    public void log(Object object) {
        telemetry.addLine("OBJECT : "+object.toString());
        if (dashboard != null){
            TelemetryPacket packet = new TelemetryPacket();
            packet.addLine("OBJECT : "+object.toString());
            dashboard.sendTelemetryPacket(packet);
        }
    }

    @Override
    public void log(LogLevel level, Object object) {
        telemetry.addLine(level.toString()+": OBJECT GIVEN : "+object.toString());
        if (dashboard != null){
            TelemetryPacket packet = new TelemetryPacket();
            packet.addLine(level.toString()+": OBJECT GIVEN : "+object.toString());
            dashboard.sendTelemetryPacket(packet);
        }
    }
}
