package com.fisioterapiakinevid.kinevid.rest.model.entity.pat;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.BloodType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.Gender;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patient")
public class Patient extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_PATIENT_ID_GENERATOR", sequenceName = "SEQ_PATIENT_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PATIENT_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "first_name", length = 80, nullable = false)
    private String firstName;

    @Basic
    @Column(name = "paternal_surname", length = 80, nullable = false)
    private String paternalSurname;

    @Basic
    @Column(name = "maternal_surname", length = 80)
    private String maternalSurname;

    @Basic
    @Column(name = "ci", length = 20, nullable = false)
    private String ci;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20, nullable = false)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Basic
    @Column(name = "phone", length = 15)
    private String phone;

    @Basic
    @Column(name = "email", length = 80)
    private String email;

    @Basic
    @Column(name = "address", length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 20)
    private BloodType bloodType;

    @Basic
    @Column(name = "occupation", length = 100)
    private String occupation;

    @Basic
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Basic
    @Column(name = "emergency_contact_phone", length = 15)
    private String emergencyContactPhone;

    @Basic
    @Column(name = "notes", length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_status", length = 30, nullable = false)
    private PatientStatus status;

    public int getAge() {
        if (birthDate == null) return 0;
        return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName);
        if (paternalSurname != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(paternalSurname);
        }
        if (maternalSurname != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(maternalSurname);
        }
        return sb.toString();
    }
}


