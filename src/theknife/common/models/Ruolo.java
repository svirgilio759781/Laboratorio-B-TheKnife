package it.uninsubria.theknife.common.models;

import java.io.Serializable;

/**
 * Stefano Virgilio 759781 VA
 * Enum che definisce i ruoli autorizzati all'interno del sistema.
 */
public enum Ruolo implements Serializable {
    Admin,
    Cliente,
    Gestore
}