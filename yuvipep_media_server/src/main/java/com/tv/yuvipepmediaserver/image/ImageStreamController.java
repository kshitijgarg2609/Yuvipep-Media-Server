package com.tv.yuvipepmediaserver.image;

import java.io.IOException;

import com.tv.yuvipepmediaserver.service.StreamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageStreamController {
    @Autowired
    StreamService imageStreamService;

    @RequestMapping(value = "/stream/{name:.+}", method = RequestMethod.GET)
    @Cacheable(value = "images", unless = "#result!=null or #result.success.data.code!=200")
    public ResponseEntity<byte[]> getImages(@PathVariable("name") String name,
            @RequestParam(name = "isId", required = false, defaultValue = "false") String isId,
            @RequestParam(name = "path", required = false, defaultValue = "images") String path) throws IOException {

        if (isId.equalsIgnoreCase("true")) {
            return imageStreamService.getFileContent(name);
        }

        return imageStreamService.getFileContent(name, path);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image,
            @RequestParam("path") String path) throws IOException {
        return imageStreamService.uploadContent(image, path);
    }

    @RequestMapping(value = "/unlink/{name:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable("name") String name,
             @RequestBody DeleteRequest deleteRequest) throws IOException {
        if (deleteRequest.getFiles() != null) {
            return imageStreamService.deleteContents(deleteRequest.getFiles(), deleteRequest.getPath());
        }
        return imageStreamService.deleteContent(name, deleteRequest.getPath());
    }
}
