package com.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TicToe {
    X(1), O(2), D(3);

    private Integer value;
}
