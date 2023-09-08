package com.example.demo.poi.controller;

import com.example.demo.poi.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

// https://lotuus.tistory.com/145

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    /*
    json 출력 http://localhost:8080/stats/user/point?excelDownload=ㄹfalse
    엑셀 파일 다운 http://localhost:8080/stats/user/point?excelDownload=true
     */
    @GetMapping("user/point")
    public ResponseEntity getUserPointStats(HttpServletResponse response, boolean excelDownload) {
        return ResponseEntity.ok(statsService.getUsersPointStats(response, excelDownload));
    }
}