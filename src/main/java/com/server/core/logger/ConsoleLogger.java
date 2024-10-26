/*CopyRights 17-10-2024
  Author: Ashok A
 */

package com.server.core.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleLogger
{
    private static Logger logger;

    private ConsoleLogger(){}

    public static void info(String componentName, final String logMessage)
    {
        logger = LoggerFactory.getLogger(componentName);
        logger.info(logMessage);
    }

    public static void error(String componentName, final String logMessage)
    {
        logger = LoggerFactory.getLogger(componentName);
        logger.error(logMessage);
    }

    public static void error(String componentName, final String logMessage, Throwable throwable)
    {
        logger = LoggerFactory.getLogger(componentName);
        logger.error(logMessage, throwable);
    }
}
