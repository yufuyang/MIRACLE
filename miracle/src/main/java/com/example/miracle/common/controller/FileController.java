package com.example.miracle.common.controller;
import com.example.miracle.common.dto.ResultDTO;
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
    public ResultDTO<UploadResultDTO> upload(@RequestParam MultipartFile file, @RequestParam(defaultValue = "common") String folder) {
        return ResultDTO.ok(fileUtil.uploadFileWithUrl(file, folder));
    }

    @DeleteMapping
    public ResultDTO<?> delete(@RequestParam String filePath) {
        fileUtil.deleteFile(filePath);
        return ResultDTO.ok();
    }

    @GetMapping("/url/{filePath}")
    public ResultDTO<String> getUrl(@RequestParam String filePath) {
        return ResultDTO.ok(fileUtil.getFileUrl(filePath));
    }
}