Requirements:
- for each new element emit statistics for current sliding window
- statistics include min, max, sum, current, size

We need emit statistics for each element, so statistics calculation algorithm should has good performance and memory consumption.

The task can be spitted in few components:
- parsing component
- component maintaining sliding window
- many analyzers to maintain our statistics
- aggregator of results from different analyzers
- output component

Current implementation performance and memory consumption information:
- Sliding window:
  Memory consumption: O(size of sliding window)
  Performance: O(log size of sliding window)

- Sum, size analyzers:
  Memory consumption: O(1)
  Performance: O(1)

- Min, max analyzers:
  Memory consumption: O(size of sliding window)
  Performance: O(log size of sliding window)

Implementation details:
- Sliding window supports out-of-order delivery of events, it can tolerate up to window size delay.
But it is not bounded so the implementation can produce OOM exception.
- Unparseable lines will be skipped
- We lose precision when we sum values. Probably it is better to use big decimal

Future improvements:

- We can optimize algorithms, if we decide to gather statistics peridiocally (not for each element)
- To scale implementation: we can split input stream of events into separate parts, then calculate statistics, them merge statistics from different streams. It is possible, because our statistics are associative.
