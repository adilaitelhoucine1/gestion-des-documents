package org.example.gestiondesdocuments.service;

import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    DocumentUploadResponse uploadDocument(DocumentUploadRequest request, MultipartFile file, String userEmail);
}

