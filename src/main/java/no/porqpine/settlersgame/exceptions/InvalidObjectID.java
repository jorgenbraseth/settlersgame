package no.porqpine.settlersgame.exceptions;

public class InvalidObjectID extends RuntimeException {
    public InvalidObjectID(Long id) {
        super("No gameObject exists with id: " + id);
    }
}
