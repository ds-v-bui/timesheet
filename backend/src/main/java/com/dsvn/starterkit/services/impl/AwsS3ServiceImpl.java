package com.dsvn.starterkit.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dsvn.starterkit.domains.models.DataUri;
import com.dsvn.starterkit.infrastructure.configuration.AwsProperties;
import com.dsvn.starterkit.services.FileService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsS3ServiceImpl implements FileService {

    @Autowired private AmazonS3 s3Client;

    @Autowired private AwsProperties awsProperties;

    @Override
    public String uploadFile(String fileName, DataUri dataUri) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(dataUri.getMimeType());

        byte[] bytes = Base64.getDecoder().decode(dataUri.getBase64());
        InputStream inputStream = new ByteArrayInputStream(bytes);

        String publicBucketName = awsProperties.getS3().getBucketNamePublic();
        String uploadS3Path = makeFileS3Path(fileName);

        s3Client.putObject(publicBucketName, uploadS3Path, inputStream, metadata);

        return makeFileS3Url(uploadS3Path);
    }

    @Override
    public boolean deleteFile(String filePath) {
        return false;
    }

    private String makeFileS3Path(String fileName) {
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

        return day + "/" + time + "_" + fileName;
    }

    private String makeFileS3Url(String uploadS3Path) {
        return awsProperties.getS3().getBucketNamePublicAccessUrl() + uploadS3Path;
    }

    //    @PostConstruct
    //    public void init() {
    //        Base64Form base64Form = new
    // Base64Form("data:text/csv;base64,IyB0ZXN0MTIzCiMgdGVzdHR0dHR0CiJpZCIsImVtYWlsIiwicGFzc3dvcmQiLCJmaXJzdF9uYW1lIiwibGFzdF9uYW1lIiwicGhvbmVfbnVtYmVyIiwicm9sZSIsImF2YXRhciIsImFkZHJlc3MiLCJiaXJ0aGRheSIsImdlbmRlciIKIjYiLCJ0ZXN0MUBnbWFpbC5jb20iLCIkMmEkMTAkeVRtUER0TE9zdUl3R2xZM1pWdjdMLkpoajdOcmpUWUlTQ3ppdFdzWnJSMDRBRTNVU2o5VHkiLCJmdGVzdDEiLCJsdGVzdDEiLCIwMTIzNDU2IiwidXNlciIsLCwsLCwsLCwsLCwsLCIwIgoiNyIsInRlc3QyQGdtYWlsLmNvbSIsIiQyYSQxMCRsZi8vb2lwVzVpUGVVZlduNjVuTVd1RGk4ekEyb3QzbzF1SlppOUZEMkkxN2FrQ3NOeWpXeSIsImZ0ZXN0MiIsImx0ZXN0MiIsIjAxMjM0NTYiLCJ1c2VyIiwsLCwsLCwsLCwsLCwsIjAiCg==", "test.csv");
    //
    //        DataUri dataUri = new DataUri(base64Form.getDataUri());
    //        String fileUrl = uploadFile(base64Form.getFileName(), dataUri);
    //        return;
    //    }
}
