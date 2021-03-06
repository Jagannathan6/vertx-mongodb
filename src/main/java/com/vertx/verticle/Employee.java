package com.vertx.verticle;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee  {

    @JsonProperty(value = "login_id")
    private String loginId;

    private String name;

    private String city;

}
