package com.example.entity;

import java.io.Serializable;

/**
 * @author YuHaoyang
 * @description
 * @create 2025/4/15 16:17
 */
public class Teacher extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;
    private String phone;
    private String email;
    private String title;

}
