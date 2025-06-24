package com.koreait.spirngSecurityStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiRespDto <T> {
    private  String stattus;
    private  String message;
    private T data;
}
