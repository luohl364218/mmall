package com.mmall.service.impl;


import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
 * introductions:
 * created by Heylink on 2018/4/24 20:23
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileUploadName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件，上传的文件名{}， 上传路径{}， 新文件名{}", fileName, path, fileUploadName);

        File fileDir = new File(path);
        //如果当前文件路径没有创建
        if (!fileDir.exists()) {
            //设置写权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //创建上传的文件对象
        File targetFile = new File(path, fileUploadName);
        try {
            file.transferTo(targetFile);
            //将targetFile上传到我们的FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到FTP服务器上
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
        }
        return targetFile.getName();
    }
}
