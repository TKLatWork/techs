package com.techs.api.userauth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RightDto {
    private String id;
    private String name;
    private String type;
    private String urlPattern;
    private String action;
    private String description;
    private String source;
    private String featureId;
}
