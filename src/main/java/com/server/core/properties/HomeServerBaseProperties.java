/*CopyRights 06-10-2024
  Author: Ashok A
 */
package com.server.core.properties;

import java.io.File;

public class HomeServerBaseProperties
{
    private HomeServerBaseProperties(){}

    public static final String WINDOWS_BASEPATH = System.getProperty("user.home") + File.separator + "homeserver";
    public static final String LINUX_BASEPATH  = "/homeserver";
}
