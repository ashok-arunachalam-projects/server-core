/*CopyRights 17-10-2024
  Author: Ashok A
 */

package com.server.core.handlers;

import com.server.core.exceptions.FileHandlerException;
import com.server.core.logger.ConsoleLogger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileHandler
{
    private static final String COMPONENT_NAME = "FileHandler";

    public void saveFileToDirectory(Path uploadPath, MultipartFile file) throws FileHandlerException
    {
        if(uploadPath == null || file == null)
        {
            throw new FileHandlerException(String.format("Param value should Not be null, uploadPath: %s , MultipartFile: %s", uploadPath, file));
        }
        try
        {
            String filename = file.getOriginalFilename();
            assert filename != null;

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            ConsoleLogger.info(COMPONENT_NAME, String.format("SuccessFully saved images to Disk, File name: %s", filename));
        }
        catch (Exception ex)
        {
            throw new FileHandlerException("Exception while saving image to Disk", ex);
        }
    }

    public boolean isWindowsOS()
    {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    protected List<File> getAllFilesFromDirectory(Path directoryToGetFile) throws FileHandlerException
    {
        try(Stream<Path> pathStream = Files.walk(directoryToGetFile))
        {
            return pathStream.filter(Files::isRegularFile)
                    .map(path -> new File(String.valueOf(path.toFile())))
                    .toList();
        }
        catch (Exception ex)
        {
            throw new FileHandlerException("Exception while getting files from directory", ex);
        }
    }

    protected void deleteFile(Path filesToDelete) throws FileHandlerException
    {
        try
        {
            Files.delete(filesToDelete);
        }
        catch (Exception ex)
        {
            throw new FileHandlerException("Exception while deleting files from directory", ex);
        }
    }
}
