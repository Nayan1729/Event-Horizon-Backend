package org.springboot.event_horizon.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.utilities.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsService {
    private final String bucketName = "event-horizon-bucket-images";
    private final AmazonS3 client;
    public String saveImageToS3(MultipartFile file) throws ApiException {
            String actualFileName = file.getOriginalFilename();
            UUID uuid = UUID.randomUUID();
            // If abc.png ==> dfdfvdfvdvfvdv.png
            String fileName = uuid.toString()+ actualFileName.substring(actualFileName.lastIndexOf("."));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType()); // Helps S3 understand file type
        try{
                PutObjectResult putObjectResult =  client.putObject(new PutObjectRequest(bucketName,fileName,file.getInputStream(),metadata));
                return fileName;
            }catch (IOException e){
                throw new ApiException("Error Uploading image to S3"+e.getMessage(),400);
            }
        }

        public String getImageUrl(String fileName) {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName,fileName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
            URL url = client.generatePresignedUrl(request);
            return url.toString();
        }

    public void deleteImageFromS3(String fileName) throws ApiException {
        try {
            // Check if the file exists
            if (!client.doesObjectExist(bucketName, fileName)) {
                throw new ApiException("File not found in S3: " + fileName, 404);
            }

            // Delete the file from S3
            client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            System.out.println("Successfully deleted: " + fileName);
        } catch (Exception e) {
            throw new ApiException("Error deleting image from S3: " + e.getMessage(), 500);
        }
    }
    }

