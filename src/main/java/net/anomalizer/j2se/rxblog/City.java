package net.anomalizer.j2se.rxblog;

import lombok.Data;

/**
 * A data POJO used to represent some object
 */
@Data
public final class City {
    private Integer cityId;
    private String cityName;
    private Integer stateId;
}
