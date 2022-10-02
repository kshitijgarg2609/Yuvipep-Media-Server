package com.tv.yuvipepmediaserver.certificate;

import java.io.IOException;

import com.tv.yuvipepmediaserver.service.StreamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certificate")
public class CertificateImageStreamController {
    @Autowired
    StreamService certificateImageStreamService;

    @RequestMapping(value = "/stream/{name:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImages(@PathVariable("name") String name) throws IOException {

        return certificateImageStreamService.getFileContent(name, "certificates");
    }
}
