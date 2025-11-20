package org.example.gestiondesdocuments.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.example.gestiondesdocuments.dto.ErrorResponse;
import org.example.gestiondesdocuments.repository.DocumentRepository;
import org.example.gestiondesdocuments.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @RequestPart("document") @Valid DocumentUploadRequest request,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            DocumentUploadResponse response = documentService.uploadDocument(request, file, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erreur",e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur","Une erreur est survenue lors de l'upload du document"));
        }
    }
    @GetMapping
    public ResponseEntity<List<DocumentUploadResponse>> getAllDocuments(){

            List<DocumentUploadResponse> list=documentService.getAllDocuments();
       return ResponseEntity.ok(list);
    }


}
