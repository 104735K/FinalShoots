package com.Shoots.service.weather;


import com.Shoots.domain.Weather;
import com.Shoots.redis.RedisService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.File;

import java.util.function.Consumer;

import org.apache.poi.openxml4j.util.ZipSecureFile;


@Service
public class WeatherService implements CommandLineRunner {

    @Autowired
    private RedisService redisService;

    // 초단기실황
    private String apiUrl_Ncst = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    // 초단기예보
    private String apiUrl_Fcst = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";

    @Value("${weather.api.key}")
    private String apiKey;

    @Override
    public void run(String... args) throws Exception {
        redisService.importLocationsToRedis();
    }


    public List<Weather> getWeatherForecast(String today, int nx, int ny) throws IOException, ParseException {

        List<Weather> weatherList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String currentTime = sdf.format(calendar.getTime());

        int hour = Integer.parseInt(currentTime.substring(8, 10));
        int minute = Integer.parseInt(currentTime.substring(10, 12));

        if (minute < 30) {
            hour--;
            if (hour < 0) {
                hour = 23;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date todayDate = dateFormat.parse(today);
                Calendar prevDay = Calendar.getInstance();
                prevDay.setTime(todayDate);
                prevDay.add(Calendar.DATE, -1);
                today = dateFormat.format(prevDay.getTime());
            }
            minute = 30;
        } else {
            minute = 30;
        }

        String baseTime = String.format("%02d%02d", hour, minute);

        System.out.println("-------------------------------------- Forecast today: " + today);
        System.out.println("-------------------------------------- Forecast baseTime : " + baseTime);
        System.out.println("-------------------------------------- Forecast = nx : " + nx + " ny : " + ny);

        StringBuilder urlBuilder = new StringBuilder(apiUrl_Fcst);

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apiKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + "1");
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "1000");
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + "JSON");
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + today);
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + baseTime);
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + nx);
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + ny);

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("ForecastResponse code: " + conn.getResponseCode());
        System.out.println("ForecastContent-Type: " + conn.getHeaderField("Content-Type"));

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        System.out.println("Response code: " + conn.getResponseCode());

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(String.valueOf(sb));

            JSONObject response = (JSONObject)jsonObject.get("response");
            JSONObject body = (JSONObject)response.get("body");
            JSONObject items = (JSONObject)body.get("items");

            JSONArray item = (JSONArray)items.get("item");

            Map<String, Weather> weatherMap = new HashMap<>();

            for (Object obj : item) {
                JSONObject data = (JSONObject) obj;

                String category = (String) data.get("category");
                String value = (String) data.get("fcstValue");

                String fcstTime = (String) data.get("fcstTime");

                Weather weather = weatherMap.get(fcstTime);

                if (weather == null) {
                    weather = new Weather();
                    weather.setTime(fcstTime);
                    weatherMap.put(fcstTime, weather);
                }

                switch (category) {
                    case "SKY": // 기온
                        weather.setSky(value);
                        break;
                    case "T1H": // 기온
                        weather.setTemperature(value);
                        break;
                    case "RN1": // 강수량
                        weather.setPrecipitation(value);
                        break;
                    case "WSD": // 풍속
                        weather.setWindSpeed(value);
                        break;
                    case "REH": // 습도
                        weather.setHumidity(value);
                        break;
                    case "PTY": // 강수 형태
                        weather.setPrecipitationType(value);
                        break;
                    case "VEC": // 풍향
                        weather.setWindDirection(value);
                        break;
                    default:
                        break;
                }

                weatherList.add(weather);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherList;
    }

    public Weather getWeather(String today, int nx, int ny) throws IOException, ParseException {

        Weather weather = new Weather();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String currentTime = sdf.format(calendar.getTime());

        String hour = currentTime.substring(8, 10);
        int minute = Integer.parseInt(currentTime.substring(10, 12));

        String baseTime = hour + "00";

        if (minute > 0) {
            int previousHour = Integer.parseInt(hour) - 1;
            if (previousHour < 0) {
                previousHour = 23;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date todayDate = dateFormat.parse(today);
                Calendar prevDay = Calendar.getInstance();
                prevDay.setTime(todayDate);
                prevDay.add(Calendar.DATE, -1);
                today = dateFormat.format(prevDay.getTime());
            }
            baseTime = String.format("%02d00", previousHour);
        }

        System.out.println("-------------------------------------- today: " + today);
        System.out.println("-------------------------------------- baseTime : " + baseTime);
        System.out.println("-------------------------------------- nx : " + nx + " ny : " + ny);

        StringBuilder urlBuilder = new StringBuilder(apiUrl_Ncst);

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apiKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + "1");
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + "1000");
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + "JSON");
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + today);
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + baseTime);
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + nx);
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + ny);

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        System.out.println("Content-Type: " + conn.getHeaderField("Content-Type"));

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        System.out.println("Response code: " + conn.getResponseCode());

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(String.valueOf(sb));

            JSONObject response = (JSONObject)jsonObject.get("response");
            JSONObject body = (JSONObject)response.get("body");
            JSONObject items = (JSONObject)body.get("items");

            JSONArray item = (JSONArray)items.get("item");

            for (Object obj : item) {
                JSONObject data = (JSONObject) obj;
                String category = (String) data.get("category");
                String value = (String) data.get("obsrValue");

                switch (category) {
                    case "T1H": // 기온
                        weather.setTemperature(value);
                        break;
                    case "RN1": // 강수량
                        weather.setPrecipitation(value);
                        break;
                    case "WSD": // 풍속
                        weather.setWindSpeed(value);
                        break;
                    case "REH": // 습도
                        weather.setHumidity(value);
                        break;
                    case "PTY": // 강수 형태
                        weather.setPrecipitationType(value);
                        break;
                    case "VEC": // 풍향
                        weather.setWindDirection(value);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weather;
    }

}
