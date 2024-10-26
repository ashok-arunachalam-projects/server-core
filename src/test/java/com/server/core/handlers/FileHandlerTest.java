/*CopyRights 18-10-2024
  Author: Ashok A
 */
package com.server.core.handlers;

import com.server.core.exceptions.FileHandlerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
public class FileHandlerTest
{
    FileHandler fileHandler;
    Path destinationPathToPutImage;
    String imageName = "200-days batch.png";
    String sourceDirectory = "src/test/resources/imagesForTest/";
    String destinationDirectory = "src/test/resources/imagesForTest/destinationDirectoryToPutImage";

    @BeforeClass
    public void beforeClass()
    {
        fileHandler = new FileHandler();
        destinationPathToPutImage = Paths.get(destinationDirectory);
    }

    @Test(groups = {"Home-Server", "UT"})
    public void testSaveFileToDiskAndVerifyUsingGetAllFilesFromDirectory() throws IOException, FileHandlerException
    {
        //Arrange
        FileInputStream fileInputStream = new FileInputStream(sourceDirectory + imageName);
        MultipartFile multipartFile = new MockMultipartFile("Image", imageName, String.valueOf(MediaType.IMAGE_PNG), fileInputStream);

        //Act
        fileHandler.saveFileToDirectory(destinationPathToPutImage, multipartFile);
        int actualNumberOfFiles = fileHandler.getAllFilesFromDirectory(destinationPathToPutImage).size();

        //Assert
        Assert.assertEquals(actualNumberOfFiles, 1);

        //cleaning all images
        Path firstImagePathToDelete = Paths.get(destinationDirectory, imageName);
        fileHandler.deleteFile(firstImagePathToDelete);
    }

    @Test(groups = {"Home-Server", "UT"}, expectedExceptions = FileHandlerException.class)
    public void shouldThrowExceptionWhenMultipartFIleIsNull() throws FileHandlerException
    {
        //Act
        fileHandler.saveFileToDirectory(destinationPathToPutImage, null);
    }

    @Test(groups = {"Home-Server", "UT"}, expectedExceptions = FileHandlerException.class)
    public void shouldThrowExceptionWhenPathIsNull() throws FileHandlerException, IOException
    {
        //Arrange
        FileInputStream fileInputStream = new FileInputStream(sourceDirectory + imageName);
        MultipartFile multipartFile = new MockMultipartFile("Image", imageName, String.valueOf(MediaType.IMAGE_PNG), fileInputStream);

        //Act
        fileHandler.saveFileToDirectory(null, multipartFile);
    }

}
