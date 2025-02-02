package com.memesphere.domain.image.controller;

import com.memesphere.domain.image.service.ImageService;
import com.memesphere.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/s3")
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이미지 Presigned URL 발급",
            description = "사용자가 이미지를 업로드하고, S3에 저장한 후 해당 이미지에 대한 presigned URL을 발급받습니다. \n" +
                    "업로드할 파일(JPG, JPEG, PNG만 가능)은 'file'이라는 키로 multipart/form-data 형식으로 첨부해야 합니다.")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = imageService.uploadFile(file);
        return ApiResponse.onSuccess(url);
    }
}
