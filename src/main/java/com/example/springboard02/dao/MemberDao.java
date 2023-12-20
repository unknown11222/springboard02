package com.example.springboard02.dao;
import com.example.springboard02.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface MemberDao {
	int selectId(String mid);//idCheck 용 메소드
	void insertMember(MemberDto mDto);//회원 정보 저장(가입, insert) 메소드
	String selectPwd(String mid);//로그인 비밀번호를 가져오는 메소드
	MemberDto selectMember(String mid);//로그인 성공 시 회원 정보를 가져오는 메소드
	void updateMemberPoint(MemberDto mDto);//회원 point 수정 메소드
}