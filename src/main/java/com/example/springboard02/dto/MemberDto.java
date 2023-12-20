package com.example.springboard02.dto;
import lombok.*;
@Getter
@Setter
public class MemberDto {
	private String m_id, m_pwd, m_name, m_birth, m_addr, m_phone, g_name;
	private int m_point;
}