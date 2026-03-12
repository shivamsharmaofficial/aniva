package com.aniva.modules.product.controller;

import com.aniva.core.response.ApiResponse;
import com.aniva.infrastructure.storage.ImageStorageService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ImageStorageService storageService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {

        return ApiResponse.success(
                "Image uploaded successfully",
                storageService.upload(file)
        );
    }

    @DeleteMapping
    public ApiResponse<Void> deleteImage(@RequestParam String url) {

        storageService.delete(url);

        return ApiResponse.success(
                "Image deleted",
                null
        );
    }
}