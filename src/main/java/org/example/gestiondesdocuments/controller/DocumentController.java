package org.example.gestiondesdocuments.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

}
