package com.dsvn.starterkit.services;

import com.dsvn.starterkit.domains.models.DataUri;

public interface FileService {

    String uploadFile(String fileName, DataUri dataUri);

    boolean deleteFile(String filePath);
}
