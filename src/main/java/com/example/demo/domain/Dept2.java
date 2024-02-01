package com.example.demo.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

// 쓰기 용

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Dept2 {
    @Id
    Integer deptNo;
    String dName;
    String loc;
}
