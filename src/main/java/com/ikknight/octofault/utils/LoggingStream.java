package com.ikknight.octofault.utils;

/**
 * Base class for logging fault messages to different outputs.
 * Extend this class to create custom logging implementations.
 */
public abstract class LoggingStream {

    /**
     * Severity levels for log messages.
     */
    public enum LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR;

        @Override
        public String toString() {
            return super.toString();
        }
    }

    /**
     * Logs a message.
     *
     * @param message The message to log
     */
    public abstract void log(String message);

    /**
     * Logs a message with specified severity level.
     *
     * @param level The severity level
     * @param message The message to log
     */
    public abstract void log(LogLevel level, String message);

    /**
     * Logs an object by converting it to string.
     *
     * @param object The object to log
     */
    public abstract void log(Object object);

    /**
     * Logs an object with specified severity level.
     *
     * @param level The severity level
     * @param object The object to log
     */
    public abstract void log(LogLevel level, Object object);
}