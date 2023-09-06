//package com.example.demo.withCsv;
//
//import com.example.demo.withCsv.domain.Person;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.item.ItemProcessor;
//
//// https://it-techtree.tistory.com/entry/creating-a-batchservice-using-springboot
//
//// itemReader로 읽어온 데이터 가공 (transform) -> 읽어온 데이터 대문자로 변환
//public class PersonItemProcessor implements ItemProcessor<Person, Person> {
//    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
//    @Override
//    public Person process(final Person people) throws Exception {
//        // 데이터 가공 -> 읽은 데이터 대문자 변환
//        final String firstName = people.getFirstName().toUpperCase();
//        final String lastName = people.getLastName().toUpperCase();
//        final Person transformedPerson = new Person(firstName, lastName);
//        log.info("Converting (" + people + ") into (" + transformedPerson + ")");
//        return transformedPerson;
//    }
//}