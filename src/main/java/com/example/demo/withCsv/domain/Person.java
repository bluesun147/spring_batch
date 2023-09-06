package com.example.demo.withCsv.domain;

import lombok.*;

// csv 내용 저장할 도메인 클래스
@Setter @Getter @ToString
@NoArgsConstructor
public class Person {
    private String lastName;
    private String firstName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
