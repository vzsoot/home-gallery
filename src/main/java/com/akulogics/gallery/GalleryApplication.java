package com.akulogics.gallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@ServletComponentScan
@SpringBootApplication
public class GalleryApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GalleryApplication.class, args);
    }

}
