package com.sonarshowcase.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Activity Log DTO - Data Transfer Object for activity log creation requests.
 * Contains only the fields that should be settable by the client.
 */
@Getter
@Setter
public class ActivityLogDto {

    private Long userId;
    private String action;
    private String details;
    private Date timestamp;
    private String ipAddress;
}
