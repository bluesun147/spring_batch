package com.example.demo.custom;

import com.example.demo.dto.OneDto;
import org.springframework.batch.item.file.transform.LineAggregator;

// 커스텀 라인 aggregator
// but! 추천하는 커스텀 방법은 x
// 가장 올바른 커스텀 방법은 reader와 writer 사이에 processor 이용해서 하는 것

public class CustomPassThroughLineAggregator<T> implements LineAggregator<T> {
    @Override
    public String aggregate(T item) {

        // 원하는대로 커스텀
        // instanceof - 인스턴스의 실제 타입(클래스) 확인
        if (item instanceof OneDto) {
            return item.toString() + "_item";
        }
        return item.toString();
    }
}