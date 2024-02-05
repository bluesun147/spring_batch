package com.example.multidb;

import com.example.multidb.db1.Dept1;
import com.example.multidb.db1.Dept1Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

@Commit
@SpringBootTest
class MultiDbApplicationTests {

    @Autowired
    Dept1Repository dept1Repository;

    @Test
    public void first() {

        for (int i=1; i<101; i++) {
            Dept1 Dept1= new Dept1(i, String.valueOf(i), String.valueOf(i));
            dept1Repository.save(Dept1);
        }

    }

}
