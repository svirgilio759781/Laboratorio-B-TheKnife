package it.uninsubria.theknife.common.models;

import java.io.Serializable;

/**
 * Stefano Virgilio 759781 VA
 * Enum che definisce i stati delle prenatazioni autorizzati all'interno del sistema.
 */
public enum StatoPrenotazione implements Serializable {
        In_attesa,
        Confermato,
        Cancellato
}
