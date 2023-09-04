package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class ChickenStatusDto {
    private int countBreakEggs;
    private int countChickensDead;
}
