package com.Shoots.redis;

import com.Shoots.mybatis.mapper.BusinessUserMapper;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisService {

    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void saveAddressData(int businessIdx, String address) {
        String key = "business:" + businessIdx + ":address";
        redisTemplate.opsForValue().set(key, address, 14, TimeUnit.DAYS);
    }

    public Map<Integer, String> getAddressData(List<Integer> businessIdxList) {
        Map<Integer, String> addressMap = new HashMap<>();
        List<String> keys = businessIdxList.stream()
                .map(idx -> "business:" + idx + ":address")
                .collect(Collectors.toList());

        // Redis Pipeline을 사용하여 여러 키 조회
        List<Object> values = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            keys.forEach(key -> connection.get(key.getBytes()));
            return null;
        });

        // Redis에 없는 값들을 저장하기 위한 별도 리스트 생성
        List<String> missingKeys = new ArrayList<>();
        List<String> missingValues = new ArrayList<>();

        for (int i = 0; i < keys.size(); i++) {
            String address = (values.get(i) != null) ? (String) values.get(i) : null;

            if (address == null) {
                address = businessUserMapper.getAddressByBusinessIdx(businessIdxList.get(i));

                if (address != null) {
                    missingKeys.add(keys.get(i));
                    missingValues.add(address);
                }
            }
            addressMap.put(businessIdxList.get(i), address);
        }

        // 조회 후 Redis에 없었던 데이터만 한 번의 Pipeline으로 저장
        if (!missingKeys.isEmpty()) {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (int i = 0; i < missingKeys.size(); i++) {
                    connection.setEx(missingKeys.get(i).getBytes(), TimeUnit.DAYS.toSeconds(7), missingValues.get(i).getBytes());
                }
                return null;
            });
        }

        return addressMap;
    }



    public void importLocationsToRedis() throws IOException {
        ZipSecureFile.setMinInflateRatio(0.001);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("location.xlsx");
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = ((Sheet) sheet).iterator();

        if (rowIterator.hasNext()) rowIterator.next();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            String first = row.getCell(0).getStringCellValue();
            String second = row.getCell(1).getStringCellValue();
            String third = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";

            String nx = null;
            String ny = null;

            if (row.getCell(3).getCellType() == CellType.NUMERIC) {
                nx = String.valueOf(row.getCell(3).getNumericCellValue());
            } else if (row.getCell(3).getCellType() == CellType.STRING) {
                try {
                    nx = String.valueOf(Integer.parseInt(row.getCell(3).getStringCellValue()));
                } catch (NumberFormatException e) {
                    nx = "0";
                }
            }

            if (row.getCell(4).getCellType() == CellType.NUMERIC) {
                ny = String.valueOf(row.getCell(4).getNumericCellValue());
            } else if (row.getCell(4).getCellType() == CellType.STRING) {
                try {
                    ny = String.valueOf(Integer.parseInt(row.getCell(4).getStringCellValue()));
                } catch (NumberFormatException e) {
                    ny = "0";
                }
            }

            String key = "location:" + first + ":" + second + ":" + third;

            if (stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.delete(key);
            }

            stringRedisTemplate.opsForHash().put(key, "x", nx);
            stringRedisTemplate.opsForHash().put(key, "y", ny);
        }

        workbook.close();
        inputStream.close();
    }


    public Map<String, Integer> getLocationData(String first, String second, String third) {
        Map<String, Integer> locationData = new HashMap<>();
        String key = "location:" + first + ":" + second + ":" + third;

        System.out.println("key = " + key);

        String nxStr = (String) stringRedisTemplate.opsForHash().get(key, "x");
        String nyStr = (String) stringRedisTemplate.opsForHash().get(key, "y");

        Integer nx = (nxStr != null) ? Integer.parseInt(nxStr) : 61;
        Integer ny = (nyStr != null) ? Integer.parseInt(nyStr) : 125;

        System.out.println("nx : " + nx);
        System.out.println("ny : " + ny);

        locationData.put("nx", nx);
        locationData.put("ny", ny);

        System.out.println("locationData > " + locationData);
        return locationData;
    }

    public Map<String, Map<String, List<String>>> getLocationOptions() throws IOException {
        ZipSecureFile.setMinInflateRatio(0.001);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("location.xlsx");
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        if (rowIterator.hasNext()) rowIterator.next();

        // 시/도 -> 구/군 -> 읍/면/동 형태의 맵을 만들기 위한 자료구조
        Map<String, Map<String, List<String>>> locationMap = new HashMap<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // 엑셀에서 각 행의 데이터를 읽어들임
            String first = row.getCell(0).getStringCellValue(); // 시/도
            String second = row.getCell(1).getStringCellValue(); // 구/군
            String third = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : ""; // 읍/면/동

            // 시/도가 없으면 맵에 추가
            locationMap.putIfAbsent(first, new HashMap<>());

            // 구/군이 없으면 해당 시/도에 구/군 추가
            locationMap.get(first).putIfAbsent(second, new ArrayList<>());

            // 읍/면/동을 해당 구/군에 추가
            if (!third.isEmpty()) {
                locationMap.get(first).get(second).add(third);
            }
        }

        workbook.close();
        inputStream.close();

        return locationMap;
    }
}
