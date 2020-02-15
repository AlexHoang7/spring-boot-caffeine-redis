package com.example.caffeine.bean;

import com.example.caffeine.constant.ResponseEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @param <T>
 * @author Alex
 */
@Data
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 9111798767061705192L;

    private String code;

    private String message;

    private T data;

    public ResultBean(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultBean(T data) {
        this.code = ResponseEnum.RESPONSE_CODE_00.value();
        this.message = ResponseEnum.RESPONSE_DESC_00.value();
        this.data = data;
    }
}
