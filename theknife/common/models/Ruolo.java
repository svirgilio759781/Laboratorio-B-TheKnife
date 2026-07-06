/**
 * Stefano Virgilio 759781 VA
 * Enum che definisce i ruoli autorizzati all'interno del sistema.
 */
package it.uninsubria.theknife.common.models;

import java.io.Serializable;

public enum Ruolo implements Serializable {
    Admin,
    Cliente,
    Gestore
}