package com.example.springobservabiltysample;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class Users {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String mobileNumber;
}


