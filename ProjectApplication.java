package com.multithreadedtaskprofiler.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Instant;

@SpringBootApplication
public class ProjectApplication {

	private static final Logger logger = LoggerFactory.getLogger(ProjectApplication.class);
	private static final int INTERVAL = 10_000_000;
	private static final int THREADS = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService executor = Executors.newFixedThreadPool(THREADS);

	private static AtomicInteger circlePoints = new AtomicInteger(0);
	private static AtomicInteger squarePoints = new AtomicInteger(0);

	static class MonteCarloTask implements Callable<Void> {
		private final int iterations;
		private final int taskId;

		MonteCarloTask(int iterations, int taskId) {
			this.iterations = iterations;
			this.taskId = taskId;
		}

		@Override
		public Void call() {
			long startTime = System.nanoTime();
			int localCirclePoints = 0, localSquarePoints = 0;

			for (int i = 0; i < iterations; i++) {
				double randX = ThreadLocalRandom.current().nextDouble(-1, 1);
				double randY = ThreadLocalRandom.current().nextDouble(-1, 1);
				double originDist = randX * randX + randY * randY;

				if (originDist <= 1) localCirclePoints++;
				localSquarePoints++;
			}

			circlePoints.addAndGet(localCirclePoints);
			squarePoints.addAndGet(localSquarePoints);

			long endTime = System.nanoTime();
			long executionTime = endTime - startTime;
			double cpuUtilization = getProcessCpuLoad();

			logger.info("Task ID: {} | Start: {} | End: {} | Execution Time: {} ms | CPU Utilization: {}%",
					taskId, Instant.ofEpochMilli(startTime / 1_000_000),
					Instant.ofEpochMilli(endTime / 1_000_000), executionTime / 1_000_000, cpuUtilization);

			return null;
		}
	}

	public static double getProcessCpuLoad() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		return (double) threadMXBean.getCurrentThreadCpuTime() / (1000_000_000);
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SpringApplication.run(ProjectApplication.class, args);

		logger.info("Starting Pi estimation with {} threads...", THREADS);
		int iterationsPerThread = INTERVAL / THREADS;
		Future<?>[] futures = new Future[THREADS];

		for (int i = 0; i < THREADS; i++) {
			futures[i] = executor.submit(new MonteCarloTask(iterationsPerThread, i + 1));
		}

		for (Future<?> future : futures) {
			future.get();
		}

		executor.shutdown();
		double pi = (4.0 * circlePoints.get()) / squarePoints.get();
		logger.info("Final Estimated Pi value is = {}", pi);
	}
}
