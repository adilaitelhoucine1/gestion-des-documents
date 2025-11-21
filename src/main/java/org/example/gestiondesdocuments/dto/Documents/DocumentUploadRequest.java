package org.example.gestiondesdocuments.dto.Documents;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.gestiondesdocuments.entite.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadRequest {

    @NotBlank(message = "Le numéro de pièce est obligatoire")
    @Size(max = 100, message = "Le numéro de pièce ne doit pas dépasser 100 caractères")
    private String numeroPiece;

    @NotNull(message = "Le type de document est obligatoire")
    private Document.TypeDocument type;

    @Size(max = 100, message = "La catégorie comptable ne doit pas dépasser 100 caractères")
    private String categorieComptable;

    @NotNull(message = "La date de la pièce est obligatoire")
    @PastOrPresent(message = "La date de la pièce ne peut pas être dans le futur")
    private LocalDate datePiece;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être supérieur à 0")
    @Digits(integer = 13, fraction = 2, message = "Le montant doit avoir au maximum 13 chiffres avant la virgule et 2 après")
    private BigDecimal montant;

    @Size(max = 255, message = "Le nom du fournisseur ne doit pas dépasser 255 caractères")
    private String fournisseur;

    @NotBlank(message = "L'exercice comptable est obligatoire")
    @Pattern(regexp = "\\d{4}", message = "L'exercice comptable doit être une année à 4 chiffres")
    private String exerciceComptable;

//    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
//    private String commentaireComptable;


}
