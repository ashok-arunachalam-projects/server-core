/*CopyRights 06-10-2024
  Author: Ashok A
 */

package com.server.core.handlers;

import com.server.core.exceptions.FileHandlerException;
import com.server.core.logger.ConsoleLogger;
import com.server.core.properties.HomeServerBaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Component
public class ImageHandler
{
    private static final String COMPONENT_NAME = "ImageHandler";
    private static final String IMAGE_PATH = "images";
    private final FileHandler fileHandler;

    @Autowired
    public ImageHandler(FileHandler fileHandler)
    {
        this.fileHandler = fileHandler;
    }

    public boolean saveImageToDisk(List<MultipartFile> images)
    {
        boolean isSuccess = true;

        if(images == null || images.isEmpty())
            return false;

        try
        {
            Path uploadImagePath = Paths.get(getBasePath());
            if(!Files.exists(uploadImagePath))
            {
                Files.createDirectories(uploadImagePath);
            }

            for (MultipartFile image : images)
            {
                saveImageToDisk(image, uploadImagePath);
            }
        }
        catch (Exception ex)
        {
            ConsoleLogger.error(COMPONENT_NAME, "Exception while saving image to disk", ex);
            isSuccess = false;
        }

        return isSuccess;
    }

    private void saveImageToDisk(MultipartFile image, Path uploadImagePath)
    {
        try
        {
            if (!Objects.requireNonNull(image.getContentType()).startsWith("image/png"))
            {
                ConsoleLogger.error(COMPONENT_NAME, String.format("Only PNG images are allowed. This file is not an image: %s", image.getOriginalFilename()));
            }
            else
            {
                fileHandler.saveFileToDirectory(uploadImagePath, image);
            }
        }
        catch (FileHandlerException ex)
        {
            ConsoleLogger.error(COMPONENT_NAME, "Unable to save image: "+ image.getOriginalFilename(), ex);
        }
    }

    public List<String> getAllImages()
    {
        List<String> imageAsEncodedStrings = new ArrayList<>();

        try
        {
            Path directoryToGetFile = Paths.get(getBasePath());
            List<File> files = fileHandler.getAllFilesFromDirectory(directoryToGetFile);
            imageAsEncodedStrings = getImagesAsBase64EncodedString(files);

            if (imageAsEncodedStrings.isEmpty())
            {
                ConsoleLogger.error(COMPONENT_NAME, "No images found in the directory");
                return imageAsEncodedStrings;
            }
            ConsoleLogger.info(COMPONENT_NAME, "Successfully got all images from the directory!");
        }
        catch (Exception ex)
        {
            ConsoleLogger.error(COMPONENT_NAME, "Exception while getting images from disk", ex);
        }

        return imageAsEncodedStrings;
    }

    protected  List<String> getImagesAsBase64EncodedString(List<File> files)
    {
        assert files != null;
        List<String> imageAsEncodedStrings = new ArrayList<>();

        for(File file : files)
        {
            try
            {
                if (!(file.getName().endsWith("png") || file.getName().endsWith("jpg")))
                {
                    ConsoleLogger.error(COMPONENT_NAME, String.format("Only PNG images are allowed. This file is not an image: %s", file.getName()));
                    continue;
                }
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                String base64EncodedImageString = Base64.getEncoder().encodeToString(fileBytes);
                imageAsEncodedStrings.add(base64EncodedImageString);
                ConsoleLogger.info(COMPONENT_NAME, String.format("Successfully encoded image to base64 from file: %s", file.getName()));
            }
            catch (IOException ex)
            {
                ConsoleLogger.error(COMPONENT_NAME, String.format("Exception while encoding image to base64 from file: %s", file.getName()), ex);
            }
        }
        return imageAsEncodedStrings;
    }

    protected String getBasePath()
    {
        String basePath = fileHandler.isWindowsOS() ? HomeServerBaseProperties.WINDOWS_BASEPATH : HomeServerBaseProperties.LINUX_BASEPATH;
        return basePath + File.separator + IMAGE_PATH;
    }

}
