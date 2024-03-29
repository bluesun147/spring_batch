package com.example.demo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

@SpringBootTest
public class TestDeptRepository {

    @Autowired
    DeptRepository deptRepository;

    @Test
    @Commit
    public void dept1() {
        for (int i = 1; i < 101; i++) {
            deptRepository.save(new Dept(i, "dName_" + String.valueOf(i), "loc_" + String.valueOf(i)));
        }
    }
}