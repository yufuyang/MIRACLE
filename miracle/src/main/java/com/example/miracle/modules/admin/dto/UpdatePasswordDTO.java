package com.example.miracle.modules.admin.dto;

import lombok.Data;

@Data
public class UpdatePasswordDTO {

    private String oldPassword;

    private String newPassword;
}
