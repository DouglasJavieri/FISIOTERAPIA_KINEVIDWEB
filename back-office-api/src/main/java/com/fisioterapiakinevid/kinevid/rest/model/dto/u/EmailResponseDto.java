package com.fisioterapiakinevid.kinevid.rest.model.dto.u;

import lombok.Builder;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 17/02/2026
 */
@Data
@Builder
public class EmailResponseDto {
    private String email;

    public EmailResponseDto(String email) {
        this.email = email;
    }
}
