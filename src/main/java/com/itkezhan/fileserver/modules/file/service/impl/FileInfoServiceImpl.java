package com.itkezhan.fileserver.modules.file.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itkezhan.fileserver.modules.file.model.FileInfo;
import com.itkezhan.fileserver.modules.file.mapper.FileInfoMapper;
import com.itkezhan.fileserver.modules.file.service.FileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itkezhan.fileserver.utils.FileUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author eggsy
 * @since 2021-07-05
 */

@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
    @Value("${file.basedir}")
    private String baseDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    /**
     * 实现文件上传功能
     *
     * @param file
     */
    @Override
    @Transactional
    public void upload(MultipartFile file) throws Exception {
        String oriFileName = file.getOriginalFilename();
        if (StrUtil.isEmpty(oriFileName)) {
            LOGGER.error("上传文件不能为空, 文件名: {}", oriFileName);
            throw new Exception("上传文件不能为空");
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriName(oriFileName);
        fileInfo.setCreateTime(new Date());
        fileInfo.setUpdateTime(new Date());
        String path = baseDir + File.separator + oriFileName;
        fileInfo.setPath(path);
        // 查看是否存在相同文件，存在则删除
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FileInfo::getOriName, oriFileName);
        List<FileInfo> fileInfos = list(queryWrapper);
        save(fileInfo);
        if (fileInfos != null && fileInfos.size() > 0) {
            delete(fileInfos.get(0).getId());//删除数据库记录
            FileUtils.deleteFile(path);//删除文件
        }
        FileUtils.saveFile(file, path);
    }


    /**
     * 实现文件下载功能
     *
     * @param response
     * @param id
     */
    @Override
    public void download(HttpServletResponse response, Long id) throws IOException {
        FileInfo fileInfo = getById(id);
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");        //这边可以设置文件下载时的名字，我这边用的是文件原本的名字，可以根据实际场景设置
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getOriName(),"UTF-8"));
        FileUtils.transToStream(fileInfo.getPath(), response.getOutputStream());
    }

    /**
     * 实现文件删除功能
     *
     * @param id
     * @return
     */
    @Override
    public boolean delete(Long id) {
        FileInfo fileInfo = getById(id);
        if (!removeById(id)) {
            return false;
        }
        return FileUtils.deleteFile(fileInfo.getPath());
    }

    /**
     * 根据文件名模糊查询
     *
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public Page<FileInfo> list(String keyword, Integer pageSize, Integer pageNum) {
        Page<FileInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.lambda().like(FileInfo::getOriName, keyword);
            queryWrapper.lambda().or().like(FileInfo::getName, keyword);
        }
        return page(page, queryWrapper);
    }
}
