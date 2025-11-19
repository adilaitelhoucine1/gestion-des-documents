package org.example.gestiondesdocuments.dto.Auth;

import java.util.List;

public record LoginResponse( String token,
         String email,
         List<String> roles) {

}
