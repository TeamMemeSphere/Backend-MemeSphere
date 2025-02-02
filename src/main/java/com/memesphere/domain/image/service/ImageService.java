package com.memesphere.domain.image.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.memesphere.global.apipayload.code.status.ErrorStatus;
import com.memesphere.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private String defaultUrl = "https://s3.amazonaws.com/";

    // S3에 파일을 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        if(file.isEmpty()){
            throw new GeneralException(ErrorStatus.EMPTY_FILE_EXCEPTION);
        }

        validateImageFileExtension(file.getOriginalFilename());

        String fileName=generateFileName(file);
        try{
            amazonS3.putObject(bucket, fileName,file.getInputStream(),getObjectMetadata(file));
            return defaultUrl+fileName;
        } catch(SdkClientException e){
            throw new IOException("error uploading file", e);
        }
    }

    // 파일 확장자 유효성 검사
    private void validateImageFileExtension(String filename) {
        String extension = getFileExtension(filename);

        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");

        if (!allowedExtensions.contains(extension)) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENTION);
        }
    }

    // 파일 확장자 추출
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENTION);  // 확장자가 없으면 예외 발생
        }

        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    // MultipartFile의 사이즈와 타입을 S3에 전달하기 위한 메타데이터 생성
    private ObjectMetadata getObjectMetadata(MultipartFile file){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    // filename 설정 (UUID 이용)
    private String generateFileName(MultipartFile file){
        return UUID.randomUUID().toString() + "-"+file.getOriginalFilename();
    }
}
