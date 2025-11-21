package org.example.gestiondesdocuments.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.example.gestiondesdocuments.dto.ErrorResponse;
import org.example.gestiondesdocuments.entite.Document;
import org.example.gestiondesdocuments.repository.DocumentRepository;
import org.example.gestiondesdocuments.service.CloudinaryService;
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
    private final CloudinaryService cloudinaryService;


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


    @PostMapping("/coundinary")
    public ResponseEntity<?> uploadDocumetCloudinary(
            @RequestPart("document") @Valid DocumentUploadRequest request,
            @RequestPart("file") MultipartFile file,
            Authentication authentication

    ){
        try {
            String url = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }


    }


    @GetMapping
    public ResponseEntity<List<DocumentUploadResponse>> getAllDocuments(
    ){
            List<DocumentUploadResponse> list=documentService.getAllDocuments();
       return ResponseEntity.ok(list);
    }
    @GetMapping ("/comptable/status")
    public ResponseEntity<List<DocumentUploadResponse>> getByStatus(
            @RequestParam(value = "status",defaultValue = "EN_ATTENTE",required = false) Document.StatutDocument status
            ){
        List<DocumentUploadResponse> list=documentService.getDocumentsByStatus(status);
        return ResponseEntity.ok(list);
    }
    @GetMapping ("/comptable/valider/{id}")
    public  ResponseEntity<DocumentUploadResponse> validerDocs(@PathVariable("id") Long id){
        DocumentUploadResponse response=documentService.validerDocs(id);
        return ResponseEntity.ok(response);
    }





}
