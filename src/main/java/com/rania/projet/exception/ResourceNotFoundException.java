package com.rania.projet.exception;

/**
 * Exception levée lorsqu'une ressource demandée n'existe pas en base de données (404 Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
