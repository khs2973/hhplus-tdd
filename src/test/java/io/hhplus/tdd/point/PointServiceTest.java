package io.hhplus.tdd.point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

	/**
	 * 1. 총 4가지 기능 ( 포인트 조회 / 포인트 충전 / 포인트 사용 / 포인트 내역 조회 ) 작성 여부
	 * 2. 각 기능들에 대해 예외케이스를 검증하는 단위 테스트 작성 여부 ( 최소 스펙 - 정책 관련한 실패 케이스 )
	 * 3. 각 기능별 통합테스트 작성 여부 ( 최소 스펙 - 성공하는지 )
	 * 4. 포인트 관련 기능에 대한 동시성 이슈 제어 및 검증하는 통합 테스트 작성 여부
	 * **/

	@Mock
	private UserPointTable userPointTable;
	
	@Mock
	private PointHistoryTable pointHistoryTable;
	
	@InjectMocks
	private PointService pointService;
	
	@Test
	@DisplayName("사용자 포인트 조회")
	public void searchUser() {
		
		// given
		long id = 1L;
		UserPoint userPoint = new UserPoint(id, 1000, System.currentTimeMillis());
		when(userPointTable.selectById(id)).thenReturn(userPoint);
		
		// when
		UserPoint result = userPointTable.selectById(id);
		
		// then
		assertEquals(1000, result.point());
		
	}
	
	@Test
	@DisplayName("사용자 포인트 충전시 최대 충전량을 초과했을때 - 실패 케이스")
	public void charge() {
		
		long id = 1L;
		long amount = 4000;
		long currentPoint = 2000;
		
		when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, currentPoint, System.currentTimeMillis()));
		
		assertThrows(IllegalArgumentException.class, () -> {
			pointService.charge(id, amount);
		});
		
	}
	
	@Test
	@DisplayName("사용자 포인트 충전 - 성공 케이스")
	public void successCharge() {
		
		long id = 1L;
		long amount = 2000;
		long currentPoint = 1000;
		long sumPoint = 3000;
		
		UserPoint beforePoint = new UserPoint(id, currentPoint, System.currentTimeMillis());
		UserPoint afterPoint = new UserPoint(id, amount + currentPoint, System.currentTimeMillis());
		
		when(userPointTable.selectById(id)).thenReturn(beforePoint);
		when(userPointTable.insertOrUpdate(id, sumPoint)).thenReturn(afterPoint);
		
		UserPoint chargePoint = pointService.charge(id, amount);
		
		assertEquals(sumPoint, chargePoint.point());
	}
	
	@Test
	@DisplayName("사용자가 포인트를 사용했을때 잔액이 부족할 경우")
	public void use() {
		
		long id = 1L;
		long amount = 2000;
		long currentPoint = 1000;
		
		when(userPointTable.selectById(id)).thenReturn(new UserPoint(id, currentPoint, System.currentTimeMillis()));
		
		assertThrows(IllegalArgumentException.class, () -> {
			pointService.use(id, amount);
		});
		
	}
	
	@Test
	@DisplayName("사용자 포인트 사용 - 성공 케이스")
	public void successUse() {
		
		long id = 1L;
		long amount = 1000;
		long currentPoint = 5000;
		long sumPoint = 4000;
		
		UserPoint beforePoint = new UserPoint(id, currentPoint, System.currentTimeMillis());
		UserPoint afterPoint = new UserPoint(id, currentPoint - amount, System.currentTimeMillis());
		
		when(userPointTable.selectById(id)).thenReturn(beforePoint);
		when(userPointTable.insertOrUpdate(id, sumPoint)).thenReturn(afterPoint);
		
		UserPoint useUserPoint = pointService.use(id, amount);
		
		assertEquals(sumPoint, useUserPoint.point());
		
	}
	
	@Test
	@DisplayName("사용자 포인트 사용 내역 조회")
	public void history() {
		
		long userId = 1L;
		
		List<PointHistory> pointHistory = List.of(
				new PointHistory(1L, userId, 3000, TransactionType.CHARGE, System.currentTimeMillis()),
				new PointHistory(2L, userId, 2800, TransactionType.USE, System.currentTimeMillis())
		);
		
		when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(pointHistory);
		
		List<PointHistory> result = pointService.history(userId);
		
		PointHistory firstUser = result.get(0);
		PointHistory secondUser = result.get(1);
		
		assertEquals(1L, firstUser.id());
		assertEquals(2800, secondUser.amount());
		
	}
	
	@Test
	@DisplayName("음수 포인트를 사용했을때")
	void chargeNegativeAmountShouldThrow() {

		long id = 1L;
		long currentPoint = 5000L;
		long amountToUse = -1000L; // 음수 포인트

		// userPointTable.selectById가 호출될 때 현재 포인트를 반환하도록 설정
		UserPoint userPoint = new UserPoint(id, currentPoint, System.currentTimeMillis());
		when(userPointTable.selectById(id)).thenReturn(userPoint);

		assertThrows(IllegalArgumentException.class, () -> {
			pointService.use(id, amountToUse);
		});
		
	}
	
}
