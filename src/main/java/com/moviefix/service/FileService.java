package com.moviefix.service;

import com.moviefix.dto.MovieDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileService {

    String uploadFile(String path, MultipartFile file) throws IOException;
    InputStream getResourceFile(String path, String fileName) throws IOException;
}