package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/*
 * introductions:
 * created by Heylink on 2018/4/24 20:23
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
