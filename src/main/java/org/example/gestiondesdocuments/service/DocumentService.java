package org.example.gestiondesdocuments.service;

import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.example.gestiondesdocuments.entite.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentUploadResponse uploadDocument(DocumentUploadRequest request, MultipartFile file, String userEmail);

    List<DocumentUploadResponse> getAllDocuments();

    List<DocumentUploadResponse> getDocumentsByStatus(Document.StatutDocument status);

    DocumentUploadResponse validerDocs(Long id,String commentaire);
}

