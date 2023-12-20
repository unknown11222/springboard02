package com.example.springboard02.dto;
import com.fasterxml.jackson.annotation.*;
import lombok.*;
import java.sql.*;
@Getter
@Setter
public class ReplyDto {
	private int r_num, r_bnum;
	private String r_contents, r_id;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")//ajax로 데이터가 넘어갈 때
	private Timestamp r_date;
}