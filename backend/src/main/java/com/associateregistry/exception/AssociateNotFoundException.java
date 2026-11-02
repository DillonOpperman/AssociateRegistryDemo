package com.associateregistry.exception;

public class AssociateNotFoundException extends RuntimeException {

    public AssociateNotFoundException(Long id) {
        super("Associate " + id + " was not found.");
    }
}
