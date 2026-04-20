package com.fisioterapiakinevid.kinevid.rest.model.dto.pat;

import com.fisioterapiakinevid.kinevid.rest.model.entity.pat.Patient;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.BloodType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.Gender;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {

    private Long id;
    private String firstName;
    private String paternalSurname;
    private String maternalSurname;
    private String fullName;
    private String ci;
    private Gender gender;
    private LocalDate birthDate;
    private Integer age;
    private String phone;
    private String email;
    private String address;
    private BloodType bloodType;
    private String occupation;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String notes;
    private PatientStatus status;

    public PatientResponseDTO(Patient patient) {
        this.id                    = patient.getId();
        this.firstName             = patient.getFirstName();
        this.paternalSurname       = patient.getPaternalSurname();
        this.maternalSurname       = patient.getMaternalSurname();
        this.fullName              = buildFullName(patient);
        this.ci                    = patient.getCi();
        this.gender                = patient.getGender();
        this.birthDate             = patient.getBirthDate();
        this.age                   = calculateAge(patient.getBirthDate());
        this.phone                 = patient.getPhone();
        this.email                 = patient.getEmail();
        this.address               = patient.getAddress();
        this.bloodType             = patient.getBloodType();
        this.occupation            = patient.getOccupation();
        this.emergencyContactName  = patient.getEmergencyContactName();
        this.emergencyContactPhone = patient.getEmergencyContactPhone();
        this.notes                 = patient.getNotes();
        this.status                = patient.getStatus();
    }

    private String buildFullName(Patient patient) {
        StringBuilder sb = new StringBuilder(patient.getFirstName());
        sb.append(" ").append(patient.getPaternalSurname());
        if (patient.getMaternalSurname() != null && !patient.getMaternalSurname().isBlank()) {
            sb.append(" ").append(patient.getMaternalSurname());
        }
        return sb.toString();
    }

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }
}


