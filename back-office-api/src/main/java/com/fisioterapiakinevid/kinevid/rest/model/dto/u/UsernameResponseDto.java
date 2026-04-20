package com.fisioterapiakinevid.kinevid.rest.model.dto.u;

import lombok.Builder;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 17/02/2026
 */
@Data
@Builder
public class UsernameResponseDto {
    private String username;

    public UsernameResponseDto(String username){
        this.username = username;
    }
}
