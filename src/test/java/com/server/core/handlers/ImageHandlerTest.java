/*CopyRights 18-10-2024
  Author: Ashok A
 */

package com.server.core.handlers;

import com.server.core.exceptions.FileHandlerException;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageHandlerTest
{
    ImageHandler imageHandler;
    FileHandler fileHandler;
    Path destinationPathToPutImage;
    String firstImage = "200-days batch.png";
    String secondImage = "WIN_20231108_16_09_59_Pro.jpg";
    String sourceDirectory = "src/test/resources/imagesForTest/";
    String destinationDirectory = "src/test/resources/imagesForTest/destinationDirectoryToPutImage";

    @BeforeClass
    public void beforeClass()
    {
        fileHandler = Mockito.spy(new FileHandler());
        imageHandler = Mockito.spy(new ImageHandler(fileHandler));
        destinationPathToPutImage = Paths.get(destinationDirectory);
        Mockito.when(imageHandler.getBasePath()).thenReturn(destinationDirectory);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=1)
    public void testSaveFileToDisk() throws IOException, FileHandlerException
    {
        //Arrange
        FileInputStream fileInputStream = new FileInputStream(sourceDirectory + firstImage);
        MultipartFile multipartFile = new MockMultipartFile("Image", firstImage, String.valueOf(MediaType.IMAGE_PNG), fileInputStream);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile);

        //Act
        boolean isSuccess = imageHandler.saveImageToDisk(multipartFiles);

        //Assert
        Assert.assertTrue(isSuccess);

        //cleaning all images
        Path firstImagePathToDelete = Paths.get(destinationDirectory, firstImage);
        fileHandler.deleteFile(firstImagePathToDelete);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=2)
    public void testGetAllImagesDisk() throws IOException, FileHandlerException
    {
        //Arrange
        FileInputStream fileInputStream1 = new FileInputStream(sourceDirectory + firstImage);
        MultipartFile multipartFile1 = new MockMultipartFile("Image", firstImage, String.valueOf(MediaType.IMAGE_PNG), fileInputStream1);

        FileInputStream fileInputStream2 = new FileInputStream(sourceDirectory + secondImage);
        MultipartFile multipartFile2 = new MockMultipartFile("Image", secondImage, String.valueOf(MediaType.IMAGE_PNG), fileInputStream2);

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile1);
        multipartFiles.add(multipartFile2);
        imageHandler.saveImageToDisk(multipartFiles);

        //Act
        List<String> listOfImages = imageHandler.getAllImages();

        //Assert
        Assert.assertEquals(listOfImages.size(), 2);

        //cleaning all images
        Path firstImagePathToDelete = Paths.get(destinationDirectory, firstImage);
        fileHandler.deleteFile(firstImagePathToDelete);

        Path secondImagePathToDelete = Paths.get(destinationDirectory, secondImage);
        fileHandler.deleteFile(secondImagePathToDelete);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=3)
    public void testGetImagesAsBase64EncodedString() throws FileHandlerException
    {
        //Arrange
        Path pathToGetImages = Paths.get(sourceDirectory);
        List<File> imageFiles = fileHandler.getAllFilesFromDirectory(pathToGetImages);

        //Act
        List<String> encodedImages = imageHandler.getImagesAsBase64EncodedString(imageFiles);

        //Assert
        Assert.assertNotNull(encodedImages);
        Assert.assertEquals(encodedImages.size(), 2);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=4)
    public void shouldReturnFalseWhenExceptionOccuesWhileSavingImage() throws IOException, FileHandlerException
    {
        //Arrange
        FileInputStream fileInputStream = new FileInputStream(sourceDirectory + firstImage);
        MultipartFile multipartFile = new MockMultipartFile("Image", firstImage, String.valueOf(MediaType.IMAGE_PNG), fileInputStream);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile);
        Mockito.doThrow(RuntimeException.class).when(fileHandler).saveFileToDirectory(Mockito.any(), Mockito.any());

        //Act
        boolean isSuccess = imageHandler.saveImageToDisk(multipartFiles);

        //Assert
        Assert.assertFalse(isSuccess);

        //resetting mock for file handler
        Mockito.reset(fileHandler);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=5)
    public void shouldReturnFalseWhenMultiPartFileIsNull()
    {
        //Act
        boolean isSuccess = imageHandler.saveImageToDisk(null);

        //Assert
        Assert.assertFalse(isSuccess);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=6)
    public void shouldReturnFalseWhenMultiPartFileIsEmpty()
    {
        //Act
        boolean isSuccess = imageHandler.saveImageToDisk(new ArrayList<>());

        //Assert
        Assert.assertFalse(isSuccess);
    }

    @Test(groups = {"Home-Server", "UT"}, priority=7)
    public void shouldNotAddIntoDirectoryOtherThanImages() throws IOException
    {
        //Arrange
        FileInputStream fileInputStream = new FileInputStream(sourceDirectory + "textFile.txt");
        MultipartFile multipartFile = new MockMultipartFile("TextFile", "textFile.txt", String.valueOf(MediaType.TEXT_PLAIN), fileInputStream);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(multipartFile);

        //Act
        imageHandler.saveImageToDisk(multipartFiles);
        List<String> listOfImages = imageHandler.getAllImages();

        //Assert
        Assert.assertTrue(listOfImages.isEmpty());
    }

    @Test(groups = {"Home-Server", "UT"}, priority=8)
    public void shouldReturnEmptyListWhenExceptionInEncoding()
    {
        //Arrange
        Mockito.doThrow(RuntimeException.class).when(imageHandler).getImagesAsBase64EncodedString(Mockito.any());

        //Act
        List<String> listOfImages = imageHandler.getAllImages();

        //Assert
        Assert.assertTrue(listOfImages.isEmpty());

        //resetting mock for file handler
        Mockito.reset(imageHandler);
    }

    @Test(groups = {"Home-Server", "UT"}, expectedExceptions = AssertionError.class, priority=9)
    public void shouldThrowAssertionErrorWhenNullFilesPassedToEncode()
    {
        //Act
        imageHandler.getImagesAsBase64EncodedString(null);
    }
}
