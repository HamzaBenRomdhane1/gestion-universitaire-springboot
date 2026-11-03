package com.rania.projet.exception;

/**
 * Exception levée lors d'une requête invalide ou incohérence métier (400 Bad Request).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
