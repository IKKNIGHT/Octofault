package com.ikknight.octofault.utils;

/**
 * Base class for logging streams. To make octofault log into your logging stream, extend this class.
 */
public abstract class LoggingStream {

    /**
     * Log levels for logging streams.
     */
    public enum LogLevel {
        DEBUG,
        INFO,
        WARNING,
        ERROR;

        /**
         * Returns the log level as a string.
         * @return The log level as a string.
         */
        @Override
        public String toString() {
            return super.toString();
        }
    }

    /**
     * Log a message to the logging stream.
     * @param message The message to log.
     */
    public abstract void log(String message);

    /**
     * Log a message to the logging stream with a specific log level.
     * @param message The message to log.
     * @param level The log level to use.
     */
    public abstract void log(LogLevel level, String message);

    /**
     * Log an object to the logging stream.
     * @param object The object to log.
     */
    public abstract void log(Object object);

    /**
     * Log an object to the logging stream with a specific log level.
     * @param object The object to log.
     * @param level The log level to use.
     */
    public abstract void log(LogLevel level, Object object);
}
