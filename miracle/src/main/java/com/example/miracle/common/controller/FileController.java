package com.example.miracle.common.controller;
import com.example.miracle.common.dto.Result;
import com.example.miracle.common.dto.UploadResultDTO;
import com.example.miracle.common.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileUtil fileUtil;

    @PostMapping("/upload")
    public Result<UploadResultDTO> upload(@RequestParam MultipartFile file, @RequestParam(defaultValue = "common") String folder) {
        return Result.ok(fileUtil.uploadFileWithUrl(file, folder));
    }

    @DeleteMapping
    public Result<?> delete(@RequestParam String filePath) {
        fileUtil.deleteFile(filePath);
        return Result.ok();
    }

    @GetMapping("/url")
    public Result<String> getUrl(@RequestParam String filePath) {
        return Result.ok(fileUtil.getFileUrl(filePath));
    }
}