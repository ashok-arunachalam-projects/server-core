/*CopyRights 05-10-2024
  Author: Ashok A
 */

package com.server.core.services.controller;

import com.server.core.handlers.ImageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/home-server")
public class MultiMediaController
{
    private final ImageHandler imageHandler;

    @Autowired
    public MultiMediaController (ImageHandler imageHandler)
    {
        this.imageHandler = imageHandler;
    }

    @GetMapping("/")
    public String sayHello()
    {
        return "Hello-World from Ashok's Home Server";
    }

    @PostMapping("/upload-all-images")
    public ResponseEntity<String> uploadAllImages(@RequestParam("images") List<MultipartFile> images)
    {
        boolean isSuccess;

        isSuccess = imageHandler.saveImageToDisk(images);

        return isSuccess ? ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("SuccessFully Image Saved To Disk")
                : ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body("Images Not Saved to Directory");
    }

    @GetMapping("/get-all-images")
    public ResponseEntity<List<String>> getAllImages()
    {
        List<String> imageList;
        imageList = imageHandler.getAllImages();

        if(imageList == null || imageList.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(imageList);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(imageList);
    }
}
