package com.example.demo.poi.service;

import com.example.demo.poi.entity.UserPoint;
import com.example.demo.poi.repository.UserPointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsService {
    private final UserPointRepository userPointRepository;
    // jackson 제공 (json <-> java 오브젝트)
    private final ObjectMapper objectMapper;

    public Object getUsersPointStats(HttpServletResponse response, boolean excelDownload) {
        List<UserPoint> userPointList = userPointRepository.findAll();

        // true면 엑셀 파일 다운로드
        if (excelDownload) {
            createExcelDownloadResponse(response, userPointList);
            return null;
        }

        // false면 json 형식
        List<Map> userPointMap = userPointList.stream()
                .map(userPoint -> objectMapper.convertValue(userPoint, Map.class))
                .collect(Collectors.toList());

        return userPointMap;
    }

    // 엑셀 다운로드
    private void createExcelDownloadResponse(HttpServletResponse response, List<UserPoint> userPointList) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet1 = workbook.createSheet("시트1");

            // 숫자 포맷은 numberCellStyle을 적용
            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            // 파일명
            final String fileName = "파일 이름";

            // 헤더
            final String[] header = {"번호", "유저코드", "유저명", "결제건수", "결제금액"};
            Row row = sheet1.createRow(0);
            for (int i=0; i< header.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(header[i]);
            }

            // 바디
            for (int i=0; i<userPointList.size(); i++) {
                // 헤더 이후로 데이터 출력되어야 하기 때문에 +1
                row = sheet1.createRow(i + 1);

                UserPoint userPoint = userPointList.get(i);

                Cell cell = null;
                cell = row.createCell(0);
                cell.setCellValue(userPoint.getId());

                cell = row.createCell(1);
                cell.setCellValue(userPoint.getUserCode());

                cell = row.createCell(2);
                cell.setCellValue(userPoint.getUserName());

                cell = row.createCell(3);
                cell.setCellValue(userPoint.getPayCnt());

                cell = row.createCell(4);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(userPoint.getPaySum());
            }

            /////////////////// 두번째 시트

            Sheet sheet2 = workbook.createSheet("시트2");

            // 파일명
            final String fileName2 = "파일 이름";

            // 헤더
            final String[] header2 = {"번호", "유저코드", "유저명", "결제건수", "결제금액"};
            Row row2 = sheet2.createRow(0);
            for (int i=0; i< header2.length; i++) {
                Cell cell = row2.createCell(i);
                cell.setCellValue(header[i]);
            }

            // 바디
            for (int i=0; i<userPointList.size(); i++) {
                // 헤더 이후로 데이터 출력되어야 하기 때문에 +1
                row2 = sheet2.createRow(i + 1);

                UserPoint userPoint = userPointList.get(i);

                Cell cell = null;
                cell = row2.createCell(0);
                cell.setCellValue(userPoint.getId());

                cell = row2.createCell(1);
                cell.setCellValue(userPoint.getUserCode());

                cell = row2.createCell(2);
                cell.setCellValue(userPoint.getUserName());

                cell = row2.createCell(3);
                cell.setCellValue(userPoint.getPayCnt());

                cell = row2.createCell(4);
                cell.setCellStyle(numberCellStyle);
                cell.setCellValue(userPoint.getPaySum());
            }

            response.setContentType("application/vnd.ms-excel");
            // 파일명 URLEncder로 감싸기
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8")+".xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}