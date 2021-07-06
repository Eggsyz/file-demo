package com.itkezhan.fileserver.modules.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itkezhan.fileserver.modules.file.model.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author eggsy
 * @since 2021-07-05
 */
public interface FileInfoService extends IService<FileInfo> {
    void upload(MultipartFile file) throws Exception;

    void download(HttpServletResponse response, Long id) throws IOException;

    boolean delete(Long id);

    Page<FileInfo> list(String keyword,Integer pageSize, Integer pageNum);
}
