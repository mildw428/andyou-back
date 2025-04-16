package com.mild.andyou.application.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiSafetyRating {
    public String category;
    public String probability;
}
