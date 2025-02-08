package com.Shoots.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.annotation.Order;

@Getter
@Setter
public class Weather {
    private String sky; // 하늘상태
    private String time; // 예보시간
    private String temperature; // 기온
    private String precipitation; // 강수량
    private String windSpeed; // 풍속
    private String humidity; // 습도
    private String precipitationType; // 강수형태
    private String windDirection; // 풍향

    private Double windSpeedAsNumber; // 숫자 값 저장


    @Override
    public String toString() {
        return "Weather{" +
                "sky='" + sky + '\'' +
                ", time='" + time + '\'' +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", windSpeed=" + windSpeed +
                ", humidity=" + humidity +
                ", precipitationType=" + precipitationType +
                ", windDirection=" + windDirection +
                '}';
    }

    public Double getWindSpeedAsNumber() {
        return windSpeedAsNumber;
    }

    public void setWindSpeedAsNumber(Double windSpeedAsNumber) {
        this.windSpeedAsNumber = windSpeedAsNumber;
    }
}
