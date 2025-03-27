package io.hhplus.tdd.point.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PointLockTest {
	
	@Autowired
	private PointService pointService;

	@Test
	public void chargeLock() throws InterruptedException {
		
		long id = 1L;
		long currentPoint = 1000L;
		long amount = 2000L;

		pointService.charge(id, currentPoint);
		
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		CountDownLatch latch = new CountDownLatch(4);
		List<Future<Boolean>> results = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			Future<Boolean> future = executorService.submit(() -> {
				try {
					System.out.println(Thread.currentThread().getName() + " 충전 시도");
					pointService.charge(id, amount);
					System.out.println(Thread.currentThread().getName() + " 충전 성공");
					return true; // 충전 성공
				} catch (IllegalArgumentException e) {
					System.out.println(Thread.currentThread().getName() + " 충전 실패: " + e.getMessage());
					return false; // 최대 포인트 초과로 실패
				} finally {
					latch.countDown();
				}
			});
			results.add(future);
		}

		latch.await();
		executorService.shutdown();

 		long successCount = results.stream().filter(filter -> {
			try {
				return filter.get();
			} catch (Exception e) {
				return false;
			}
		}).count();

		UserPoint finalPoint = pointService.point(id);

		// 한도는 5000
		assertEquals(2, successCount, "최대 4번 충전 성공입니다.");
		assertEquals(5000L, finalPoint.point(), "최종 포인트는 최대치 5000입니다.");
		
	}
	
}
