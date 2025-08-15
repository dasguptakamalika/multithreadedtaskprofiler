
Optimized Multithreaded Task Scheduling and CPU Profiling Framework

Author: Kamalika Dasgupta
Advisor: Dr. Amir Akhavan Masoumi
Grade: A+ – CIS 600 Master’s Project, University of Massachusetts Dartmouth

Overview

A Java-based framework comparing single-threaded and multithreaded Monte Carlo Pi estimation, integrated with Intel® VTune Profiler for CPU utilization analysis and D3.js visualizations for performance insights.

Motivation

Inspired by my passion for Advanced Computer Systems and curiosity about thread-level parallelism. Dr. Amir suggested using Pi estimation as a core example, reviewed my code line-by-line, and guided optimization and documentation improvements.

Huge thanks to Dr. Amir Akhavan Masoumi for his vision, patience, and hands-on mentorship.


Key Features

* Optimized multithreaded execution with `ExecutorService`
* Baseline single-threaded version for comparison
* VTune Profiler integration for CPU usage, execution time, and cache behavior
* REST API to serve profiling data
* Interactive D3.js charts with dark mode and PDF export

Results

* Optimized: Engaged multiple CPU cores, balanced workload
* Unoptimized: Single-core bottleneck
* Similar execution time but better resource utilization in optimized version

uture Scope

* Distributed system implementation
* Real-time CPU monitoring with WebSockets
* Advanced metrics (cache misses, memory bandwidth)
* Diverse computational tasks beyond Pi estimation




