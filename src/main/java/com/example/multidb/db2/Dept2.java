package com.example.multidb.db2;

// 들어가는 타깃 db

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="dept2")
@AllArgsConstructor
@NoArgsConstructor
public class Dept2 {
    @Id
    private Integer deptno;
    private String dname;
    private String loc;
}