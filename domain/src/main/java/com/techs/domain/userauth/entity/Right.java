package com.techs.domain.userauth.entity;

import com.techs.domain.userauth.valueobject.RightAction;
import com.techs.domain.userauth.valueobject.RightType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Right {
    private String id;
    private String name;
    private RightType type;
    private String urlPattern;
    private RightAction action;
    private String description;
    private String source;
    private String featureId;
    private boolean builtIn;
}
