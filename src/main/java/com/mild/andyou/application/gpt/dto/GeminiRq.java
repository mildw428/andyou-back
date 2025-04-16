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
public class GeminiRq {
    private List<GeminiContent> contents;

    public void addContents(RoleType role, String content) {
        if(this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.add((new GeminiContent(role, content)));
    }
}
