package com.vertx.verticle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String _id;
    @JsonProperty(value = "login_id")
    private String loginId;
    private String name;
    private String city;
}
