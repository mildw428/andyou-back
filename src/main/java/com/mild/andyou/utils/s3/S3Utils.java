package com.mild.andyou.utils.s3;

import com.mild.andyou.utils.AwsUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class S3Utils {

    public final String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir");

    public static String getPresignedUrl(S3Presigner s3Presigner, Bucket bucket, String key, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket.getBucketName())
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    public static void tempFileUpload(Bucket bucket, String key, byte[] bytes) {
        S3Client s3Client = getS3Client(bucket);

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket.getBucketName())
                .key(key)
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(request, RequestBody.fromByteBuffer(byteBuffer));
    }

    public static boolean upload(Bucket bucket, String key, byte[] bytes) {
        try (S3TransferManager s3TransferManager = getS3TransferManager(bucket)) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket.getBucketName()).key(key)
                    .serverSideEncryption(ServerSideEncryption.AES256).build();
            UploadRequest uploadRequest = UploadRequest.builder().putObjectRequest(putObjectRequest)
                    .requestBody(AsyncRequestBody.fromBytes(bytes)).build();

            Upload upload = s3TransferManager.upload(uploadRequest);
            CompletedUpload completedUpload = upload.completionFuture().join();
            PutObjectResponse putObjectResponse = completedUpload.response();

            return putObjectResponse != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    public static byte[] download(Bucket bucket, String key) {
        try (S3TransferManager s3TransferManager = getS3TransferManager(bucket)) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket.getBucketName()).key(key).build();
            DownloadRequest<ResponseBytes<GetObjectResponse>> downloadRequest = DownloadRequest.builder()
                    .getObjectRequest(getObjectRequest).responseTransformer(AsyncResponseTransformer.toBytes()).build();

            Download<ResponseBytes<GetObjectResponse>> download = s3TransferManager.download(downloadRequest);
            CompletedDownload<ResponseBytes<GetObjectResponse>> completedDownload = download.completionFuture().join();
            ResponseBytes<GetObjectResponse> responseBytes = completedDownload.result();

            return responseBytes.asByteArray();
        } catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    private static S3Client getS3Client(Bucket bucket) {
        S3ClientBuilder s3ClientBuilder = S3Client.builder().region(AwsUtils.getAwsRegion());

        if (bucket.getAccessKey() != null && bucket.getSecretKey() != null) {
            s3ClientBuilder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(bucket.getAccessKey(), bucket.getSecretKey())));
        }

        return s3ClientBuilder.build();
    }

    private static S3TransferManager getS3TransferManager(Bucket bucket) {
        S3AsyncClient s3AsyncClient = getS3AsyncClient(bucket);
        return S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
    }

    private static S3AsyncClient getS3AsyncClient(Bucket bucket) {
        S3AsyncClientBuilder builder = S3AsyncClient.builder()
                .region(AwsUtils.getAwsRegion());

        if (bucket.getAccessKey() != null && bucket.getSecretKey() != null) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(bucket.getAccessKey(), bucket.getSecretKey())));
        }

        return builder.build();
    }

}