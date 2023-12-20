package com.example.springboard02.dto;
import lombok.*;
import java.sql.*;
@Getter
@Setter
public class BoardDto {//변수는 최대한 소문자로 작성
	private int b_num, b_views;
	private String b_title, b_contents, b_id, m_name;
	private Timestamp b_date;
}