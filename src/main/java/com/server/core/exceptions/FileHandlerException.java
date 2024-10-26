/*CopyRights 17-10-2024
  Author: Ashok A
 */

package com.server.core.exceptions;

public class FileHandlerException extends Exception
{
    public FileHandlerException(String message)
    {
        super(message);
    }

    public FileHandlerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
