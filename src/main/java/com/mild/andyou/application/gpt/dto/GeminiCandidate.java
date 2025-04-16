package com.mild.andyou.application.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiCandidate {
  public GeminiContent content;
  public String finishReason;
  public int index;
  public List<GeminiSafetyRating> safetyRatings;
}
