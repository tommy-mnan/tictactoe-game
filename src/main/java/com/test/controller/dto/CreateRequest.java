package com.test.controller.dto;

import com.test.model.Player;
import lombok.Data;

@Data
public class CreateRequest {
    private Player player;
    private int size;
}
