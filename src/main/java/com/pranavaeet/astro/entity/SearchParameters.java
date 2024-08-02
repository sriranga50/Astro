package com.pranavaeet.astro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchParameters {
  // private long projectid = 0;
  // private long taskid = 0;
  // private long episodeid = 0;
  // private long kwizzleid = 0;
  private String keyword = "";
  private int page = 1;
  private int size = 10;
}
