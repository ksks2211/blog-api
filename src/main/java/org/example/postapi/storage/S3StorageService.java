package org.example.postapi.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

/**
 * @author rival
 * @since 2025-01-28
 */
@Service
@Slf4j
public class S3StorageService implements StorageService{

    private final S3Client s3Client;
    private final String bucket;

    private final String baseUrl;



    public S3StorageService(S3Client s3Client, @Value("${cloud.aws.s3.bucket}") String bucket, @Value("${cdn.baseUrl}") String baseUrl){
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.baseUrl=baseUrl;
    }

    @Override
    public Resource download(String key) {
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(GetObjectRequest.builder()
            .key(key)
            .bucket(bucket)
            .build());
        return new InputStreamResource(object);
    }

    @Override
    public void upload(MultipartFile file, String key) {
        try(InputStream inputStream = file.getInputStream()){
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType()) // 파일 MIME 타입 설정
                    .build(),
                RequestBody.fromInputStream(
                    inputStream, file.getSize() // InputStream과 크기 전달
                ));
        }catch(Exception e){
            log.error("Error while uploading file", e);
            throw new S3FailureException(e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .key(key)
            .bucket(bucket)
            .build());
    }

    @Override
    public String getUrl(String filename) {
        return baseUrl+"/"+filename;
    }


    public void rename(String oldKey, String newKey){
        try{
            // Copy
            CopyObjectRequest request = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(oldKey)
                .destinationBucket(bucket)
                .destinationKey(newKey)
                .build();
            s3Client.copyObject(request);


            // Delete old
            delete(oldKey);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("Failed to rename file", e);
            throw new S3FailureException(e.getMessage());
        }
    }
}
