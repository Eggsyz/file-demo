package com.itkezhan.fileserver.modules.file.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itkezhan.fileserver.common.api.CommonPage;
import com.itkezhan.fileserver.common.api.CommonResult;
import com.itkezhan.fileserver.modules.file.model.FileInfo;
import com.itkezhan.fileserver.modules.file.service.FileInfoService;
import com.itkezhan.fileserver.modules.file.service.impl.FileInfoServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author eggsy
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/file")
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoController.class);

    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult upload(MultipartFile file) {
        try {
            fileInfoService.upload(file);
        } catch (Exception e) {
            LOGGER.error("文件上传失败，ERROR：{}", e.getMessage());
            return CommonResult.failed("文件上传失败");
        }
        return CommonResult.success("SUCCESS");
    }

    @ApiOperation(value = "下载文件")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void download(HttpServletResponse response, @Param(value = "id") Long id) {
        try {
            fileInfoService.download(response, id);
        } catch (IOException e) {
            LOGGER.error("文件下载失败，ERROR：{}", e.getMessage());
        }
    }

    @ApiOperation(value = "删除文件")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@Param(value = "id") Long id) {
        if (!fileInfoService.delete(id)) {
            return CommonResult.failed();
        }
        return CommonResult.success("SUCCESS");
    }

    @ApiOperation(value = "文件列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<FileInfo>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<FileInfo> fileList = fileInfoService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(fileList));
    }
}

