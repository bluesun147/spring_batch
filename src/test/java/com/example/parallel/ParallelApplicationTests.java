package com.example.parallel;

import com.example.parallel.domain.Dept;
import com.example.parallel.domain.DeptRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

// 실제 db에 저장하기 위해
@Commit
@SpringBootTest
class ParallelApplicationTests {

	@Autowired
	DeptRepository deptRepository;

	@Test
	@Transactional
	void deptSave() {
		List<Dept> deptList = new ArrayList<>();
		for (int i=1; i<10000; i++) {
			deptList.add(new Dept(i, String.valueOf(i), String.valueOf(i)));
		}
		deptRepository.saveAll(deptList);
	}

	@Test
	@Transactional
	void deptDel() {
		deptRepository.deleteAll();
	}
}