package org.example.gestiondesdocuments.mapper;

import org.example.gestiondesdocuments.dto.Documents.DocumentUploadRequest;
import org.example.gestiondesdocuments.dto.Documents.DocumentUploadResponse;
import org.example.gestiondesdocuments.entite.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cheminFichier", ignore = true)
    @Mapping(target = "nomFichierOriginal", ignore = true)
    @Mapping(target = "typeFichier", ignore = true)
    @Mapping(target = "tailleFichier", ignore = true)
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    @Mapping(target = "societe", ignore = true)
    @Mapping(target = "uploadePar", ignore = true)
    @Mapping(target = "validePar", ignore = true)
    @Mapping(target = "dateValidation", ignore = true)
    Document toEntity(DocumentUploadRequest request);

    @Mapping(target = "message", constant = "Document uploadé avec succès")
    DocumentUploadResponse toUploadResponse(Document document);

    @Mapping(target = "message", source = "message")
    DocumentUploadResponse toUploadResponse(Document document, String message);
}
