package com.multithreadedtaskprofiler.project;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Instant;

@SpringBootApplication
class PiEstimationParallel {
	static final int INTERVAL = 10_000_000;
	static final int THREADS = Runtime.getRuntime().availableProcessors();              // As per my laptop specificatins, 8 Logical Processors
	static final ExecutorService executor = Executors.newFixedThreadPool(THREADS);      // The ref is only constant, i can still change it's   state
	//Also provides a thread pool and mechanisms for managing concurrent tasks efficiently. here it is the threads


	static AtomicInteger circlePoints = new AtomicInteger(0);
	static AtomicInteger squarePoints = new AtomicInteger(0);   //AtomicInteger , so that they wouldn't be interupted by other threads

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

			System.out.printf("Task ID: %d | Start: %s | End: %s | Execution Time: %d ms | CPU Utilization: %.2f%%\n",
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
		int iterationsPerThread = INTERVAL / THREADS;
		Future<?>[] futures = new Future[THREADS];

		System.out.println("Starting Pi estimation with " + THREADS + " threads...");

		for (int i = 0; i < THREADS; i++) {
			futures[i] = executor.submit(new MonteCarloTask(iterationsPerThread, i + 1));
		}

		for (Future<?> future : futures) {
			future.get();
		}

		executor.shutdown();
		double pi = (4.0 * circlePoints.get()) / squarePoints.get();
		System.out.println("Final Estimated Pi value is = " + pi);
	}
}







