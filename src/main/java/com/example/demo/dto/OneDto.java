package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OneDto {
    String one;

    @Override
    public String toString() {
        return one;
    }
}