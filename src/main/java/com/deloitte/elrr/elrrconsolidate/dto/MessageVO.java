package com.deloitte.elrr.elrrconsolidate.dto;

import com.yetanalytics.xapi.model.Statement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class MessageVO {
  private Statement statement;
}
