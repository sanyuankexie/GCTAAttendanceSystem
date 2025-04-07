package org.sanyuankexie.attendance.service;

import io.minio.*;
import io.minio.http.Method;
import org.apache.http.ssl.SSLContextBuilder;
import org.sanyuankexie.attendance.model.MinioConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;
    @Resource
    private MinioConfig minioConfig;
    private final String defaultBucketName;
    private static final int MAXSIZE = 5242880;
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_PNG = "image/png";

    public MinioService(MinioConfig minioConfig) throws Exception {
        this.minioConfig = minioConfig;
        this.minioClient = MinioClient.builder()
                .endpoint(this.minioConfig.getEndPoint())
                .credentials(this.minioConfig.getAccessKey(), this.minioConfig.getSecretKey())
                .build();
        this.defaultBucketName = this.minioConfig.getBucketName();
        initializeBucket();
    }

    // 初始化存储桶
    private void initializeBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(defaultBucketName)
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(defaultBucketName)
                    .build());
        }
    }

    /**
     * 上传图片
     *
     * @param objectName  对象名称
     * @param stream      文件流
     * @param contentType 文件类型（如：image/jpeg）
     */
    public String upload(String objectName, InputStream stream, String contentType) throws Exception {
        // 校验文件类型
        if (!isAllowedType(contentType)) {
            throw new IllegalArgumentException("仅支持JPEG和PNG格式图片，当前类型：" + contentType);
        }
        // 验证文件扩展名
        if (!isValidExtension(objectName)) {
            throw new IllegalArgumentException("文件扩展名必须为.jpg/.jpeg/.png");
        }
        // 验证是否存在
        if (exists(objectName)) {
            throw new IllegalAccessException("已存在相同图片");
        }
        ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                .bucket(defaultBucketName)
                .object(objectName)
                .stream(stream, -1, MAXSIZE) // -1表示未知大小，5MB分块
                .contentType(contentType)
                .build());
        return "https://api.kexie.space/data/" + defaultBucketName + "/" + objectName;
    }


    @Transactional
    public List<String> uploadImages(MultipartFile[] files) throws Exception {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = null;
            try {
                // 1. 校验基本参数
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("上传文件不能为空");
                }

                // 2. 获取原始文件名并生成唯一标识
                originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    throw new IllegalArgumentException("文件名不能为空");
                }

                String expire = Long.toString(System.currentTimeMillis());
                String objectName = expire + originalFilename;

                // 3. 获取文件流和MIME类型
                InputStream inputStream = file.getInputStream();
                String contentType = file.getContentType();

                // 4. 调用单文件上传方法
                String url = this.upload(objectName, inputStream, contentType);
                urls.add(url);

                // 5. 关闭流（MultipartFile的流由Spring管理，通常无需手动关闭）
            } catch (Exception e) {
                // 可选：记录失败文件信息
                throw new RuntimeException("文件 [" + originalFilename + "] 上传失败: " + e.getMessage());
            }
        }

        return urls;
    }

    /**
     * 校验MIME类型
     */
    private boolean isAllowedType(String contentType) {
        return MIME_JPEG.equalsIgnoreCase(contentType) ||
                MIME_PNG.equalsIgnoreCase(contentType);
    }

    /**
     * 校验文件扩展名
     */
    private boolean isValidExtension(String objectName) {
        String lowerName = objectName.toLowerCase();
        return lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png");
    }


//    public String getUrl(String objectName) throws Exception {
//        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                .method(Method.GET)
//                .bucket(defaultBucketName)
//                .object(objectName)
//                .expiry(7, TimeUnit.DAYS)
//                .build());
//    }

    /**
     * 删除图片
     *
     * @param objectName 对象名称
     */
    @Transactional
    public void delete(String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(defaultBucketName)
                .object(objectName)
                .build());
    }

    /**
     * 检查图片是否存在
     *
     * @param objectName 对象名称
     */
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(defaultBucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}