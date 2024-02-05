package com.example.multidb.db1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Dept1")
public class Dept1 {
    @Id
    private Integer deptno;
    private String dname;
    private String loc;
}