package com.mild.andyou.application.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiContent {
    private String role;
    private List<GeminiPart> parts;

    public GeminiContent(RoleType role, String prompt) {
        this.parts = new ArrayList<>();
        this.role = role.getValue();
        parts.add(new GeminiPart(prompt));
    }
}
