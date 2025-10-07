package com.atg.autonexo.backend.iam.interfaces.rest.resources;

public record SignInResource(
    String email,
    String password
) {
    
}
