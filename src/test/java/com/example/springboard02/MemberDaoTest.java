package com.example.springboard02;
import com.example.springboard02.dao.MemberDao;
import com.example.springboard02.dto.MemberDto;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberDaoTest {
	@Autowired
	private MemberDao mDao;

	@Test
	@DisplayName("1. MemberDao id check")//어떤 메소드를 테스트 했는 지 표기
	@Order(1)
	public void selectIdTest() throws Exception{//예외 던지기
		int cnt=mDao.selectId("test");
	}

	@Test
	@DisplayName("2. MemberDao insert")//MemberDao의 insert를 확인
	@Order(2)
	public void insertMemberTest() throws Exception{//예외 던지기
		//화면이 없기 때문에 직접 Dto 를 생성하여 데이터를 넣고 테스트를 수행
		//실제 DB에 저장되지 않음
		MemberDto mDto=new MemberDto();
		mDto.setM_id("user01");
		mDto.setM_pwd("1111");
		mDto.setM_name("user01");
		mDto.setM_birth("2001-04-10");
		mDto.setM_addr("부천시");
		mDto.setM_phone("010-1234-5678");
		mDao.insertMember(mDto);
	}

	@Test
	@DisplayName("3. Get Password")
	@Order(3)
	public void selectPasswordTest() throws Exception{
		String pwd=mDao.selectPwd("Test01");
	}
}