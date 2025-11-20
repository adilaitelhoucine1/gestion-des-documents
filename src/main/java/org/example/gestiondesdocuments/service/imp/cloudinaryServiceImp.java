package org.example.gestiondesdocuments.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.gestiondesdocuments.service.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class cloudinaryServiceImp implements CloudinaryService {
    private final Cloudinary cloudinary;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/jpg", "image/png"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public String uploadFile(MultipartFile file) throws IOException {

       validateFile(file);
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "auto"
                )
        );

        return uploadResult.get("secure_url").toString();
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

}
