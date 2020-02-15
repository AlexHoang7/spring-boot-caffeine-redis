package com.example.caffeine.constant;

public enum ResponseEnum {

    RESPONSE_CODE_00("00"),
    RESPONSE_CODE_01("01"),
    RESPONSE_CODE_02("02"),
    RESPONSE_CODE_99("99"),

    RESPONSE_DESC_00("请求成功"),
    RESPONSE_DESC_01("参数异常"),
    RESPONSE_DESC_99("请求失败"),
    RESPONSE_DESC_02("请求异常");

    ;

    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    ResponseEnum(String value) {
        this.value = value;
    }
}
