package org.example.progetto.support;

/**
 * Record utilizzato per inviare risposte JSON standardizzate
 * (es. Messaggi di errore o conferme di successo).
 */
public record ResponseMessage(String message) { }