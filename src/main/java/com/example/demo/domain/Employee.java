package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String number;
    private String email;
    private String department;
    private String salary;


}
