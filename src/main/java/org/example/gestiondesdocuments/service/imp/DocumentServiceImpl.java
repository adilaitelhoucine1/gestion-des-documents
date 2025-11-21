package org.example.gestiondesdocuments.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.example.gestiondesdocuments.entite.Document;
import org.example.gestiondesdocuments.entite.Utilisateur;
import org.example.gestiondesdocuments.mapper.DocumentMapper;
import org.example.gestiondesdocuments.repository.DocumentRepository;
import org.example.gestiondesdocuments.repository.UserRepository;
import org.example.gestiondesdocuments.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentMapper documentMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/jpg", "image/png"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    @Transactional
    public DocumentUploadResponse uploadDocument(DocumentUploadRequest request, MultipartFile file, String userEmail) {

        validateFile(file);
        Utilisateur user = getUserWithSociete(userEmail);


        Document document = buildDocument(request, file, user);

        String filePath = saveFileToSystem(file);
        document.setCheminFichier(filePath);

        Document savedDocument = documentRepository.save(document);

        return documentMapper.toUploadResponse(savedDocument);
    }

    private Utilisateur getUserWithSociete(String email) {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getSociete() == null) {
            throw new RuntimeException("L'utilisateur doit être associé à une société");
        }
        return user;
    }

    private Document buildDocument(DocumentUploadRequest request, MultipartFile file, Utilisateur user) {
        Document document = documentMapper.toEntity(request);
        document.setNomFichierOriginal(file.getOriginalFilename());
        document.setTypeFichier(file.getContentType());
        document.setTailleFichier(file.getSize());
        document.setSociete(user.getSociete());
        document.setUploadePar(user);
        return document;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide ou n'existe pas");
        }

        if (file.getOriginalFilename() == null) {
            throw new RuntimeException("Le nom du fichier est invalide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("La taille du fichier dépasse la limite de 10MB");
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);

        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new RuntimeException("Type de fichier non autorisé. Formats acceptés : PDF, JPG, JPEG, PNG");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Type MIME non autorisé. Formats acceptés : PDF et images (JPG, PNG)");
        }
    }

    private String saveFileToSystem(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String uniqueFilename = UUID.randomUUID() + "." + extension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de sauvegarder le fichier: " + e.getMessage());
        }
    }

    public List<DocumentUploadResponse> getAllDocuments(){
       return documentRepository.findAll().stream()
                .map(documentMapper::toUploadResponse).toList();
    }
    public  List<DocumentUploadResponse> getDocumentsByStatus(Document.StatutDocument status){
       return documentRepository.findAll()
               .stream().filter(d->d.getStatut()==status)
               .map(documentMapper::toUploadResponse)
               .toList();
    }

    @Override
    @Transactional
    public DocumentUploadResponse validerDocs(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID: " + id));

        if (document.getStatut() == Document.StatutDocument.VALIDE) {
            throw new RuntimeException("Le document est déjà validé");
        }

        document.setStatut(Document.StatutDocument.VALIDE);
        document.setDateValidation(java.time.LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);

        return documentMapper.toUploadResponse(savedDocument);
    }
}

