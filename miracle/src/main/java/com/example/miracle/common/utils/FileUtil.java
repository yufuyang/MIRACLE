package com.example.miracle.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.miracle.common.config.MinioConfig;
import com.example.miracle.common.dto.UploadResultDTO;
import com.example.miracle.common.exception.BusinessException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUtil {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 生成文件名
            String fileName = generateFileName(file.getOriginalFilename());
            // 生成文件路径
            String filePath = generateFilePath(folder, fileName);

            // 上传文件
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(args);

            // 返回文件访问路径
            return filePath;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String filePath) {
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filePath)
                    .build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new BusinessException("文件删除失败");
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        return IdUtil.simpleUUID() + suffix;
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String folder, String fileName) {
        String datePath = DateUtil.format(new Date(), "yyyy/MM/dd");
        return String.format("%s/%s/%s", folder, datePath, fileName);
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(String filePath) {
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(filePath)
                    .method(Method.GET)
                    .build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            throw new BusinessException("获取文件URL失败");
        }
    }

    /**
     * 上传文件并返回结果
     */
    public UploadResultDTO uploadFileWithUrl(MultipartFile file, String folder) {
        String filePath = uploadFile(file, folder);
        String fileUrl = getFileUrl(filePath);

        UploadResultDTO result = new UploadResultDTO();
        result.setFilePath(filePath);
        result.setFileUrl(fileUrl);
        return result;
    }
}