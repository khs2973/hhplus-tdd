package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.database.UserPointTable;

@SpringBootTest
public class PointServiceIntegrationTest {

	@Autowired
	private UserPointTable userPointTable;
	
	@Autowired
	private PointService pointService;
	
	private long id = 1L;
	
	@BeforeEach
	void setUp() {
		userPointTable.insertOrUpdate(id, 0); // 초기화
	}
	
	@Test
	@DisplayName("사용자 포인트 조회")
	public void searchUser() {
		
		long id = 1L;
		userPointTable.insertOrUpdate(id, 1000);
		UserPoint result = pointService.point(id);
		assertEquals(1000, result.point());
		
	}
	
	@Test
	@DisplayName("사용자 포인트 충전시 최대 충전량을 초과했을때 - 실패 케이스")
	public void charge() {
		
		long amount = 4000L;
		long currentPoint = 2000L;
		
		userPointTable.insertOrUpdate(id, currentPoint);

		assertThrows(IllegalArgumentException.class, () -> {
			pointService.charge(id, amount);
		});
		
	}
	
	@Test
	@DisplayName("사용자 포인트 충전 - 성공 케이스")
	public void successCharge() {
		
		long amount = 2000L;
		long currentPoint = 1000L;
		long sumPoint = 3000L;
		
		pointService.charge(id, currentPoint);
		
		UserPoint charged = pointService.charge(id, amount);
		assertEquals(sumPoint, charged.point());
		
	}
	
	@Test
	@DisplayName("사용자가 포인트를 사용했을때 잔액이 부족할 경우")
	public void use() {
		
		long amount = 2000L;
		long currentPoint = 1000L;
		
		pointService.charge(id, currentPoint);
		
		assertThrows(IllegalArgumentException.class, () -> {
			pointService.use(id, amount);
		});
		
	}
	
	@Test
	@DisplayName("사용자 포인트 사용 - 성공 케이스")
	public void successUse() {
		
		long amount = 1000L;
		long currentPoint = 5000L;
		long sumPoint = 4000L;
		
		pointService.charge(id, currentPoint);
		UserPoint afterUse = pointService.use(id, amount);
		assertEquals(sumPoint, afterUse.point());
		
	}
	
	@Test
	@DisplayName("사용자 포인트 사용 내역 조회")
	public void history() {
		
		pointService.charge(id, 3000);
		pointService.use(id, 2800);

		List<PointHistory> result = pointService.history(id);

		assertEquals(2, result.size());

		PointHistory first = result.get(0);
		PointHistory second = result.get(1);

		assertEquals(3000, first.amount());
		assertEquals(200, second.amount());
		
	}
	
	@Test
	@DisplayName("음수 포인트를 사용했을때")
	void chargeNegativeAmountShouldThrow() {

		long currentPoint = 5000L;
		long amountToUse = -1000L; // 음수 포인트

		pointService.charge(id, currentPoint);
		
		assertThrows(IllegalArgumentException.class, () -> {
			pointService.use(id, amountToUse);
		});
		
	}
	
}
