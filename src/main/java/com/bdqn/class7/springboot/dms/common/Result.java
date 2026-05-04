package com.bdqn.class7.springboot.dms.common;

import lombok.Data;

/**
 * 统一响应结果封装：所有 Controller 返回值都包一层，保证前端拿到 { code, msg, data } 结构。
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> ok() { return ok(null); }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(String msg) { return fail(500, msg); }

    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
