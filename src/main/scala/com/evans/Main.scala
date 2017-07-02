package com.evans

import scala.collection.immutable.Queue
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.io.Source
import scala.util.Try
import scala.concurrent.duration._

/**
  * Created by vans239 on 01/07/17.
  */
object Main {

  def header: String = s"""T\tV\tN\tRS\tMinV\tMaxV\n---------------------------------------------"""

  def encodeTotalStatistics(statistics: TotalStatistics): String = {
    import statistics._
    //todo precision
    f"$timestamp\t$value\t$count\t$sum%.5f\t$min\t$max"
  }

  def decodeMeasurement(line: String): Try[Measurement] = Try {
    val Array(timestamp, value) = line.split("[ \t]")
    Measurement(timestamp.toLong, value.toDouble)
  }

  def process(window: FiniteDuration)(source: Source): Unit = {
    val statistics = new AggregateAnalyzer(window)
    //todo we filter out unparseable measurements
    println(header)
    source.getLines()
      .flatMap(l => decodeMeasurement(l).toOption)
      .map {
        measurement =>
          statistics.add(measurement)
          statistics.statistics()
      }.map(encodeTotalStatistics)
       .foreach(println)
  }

  def main(args: Array[String]): Unit = {
    val filename = "data_scala.txt"
    //todo handle unknown file
    val source = Source.fromFile(filename)
    IOUtils.using(source)(process(60.seconds))
  }

}

trait Analyzer[A] {

  def add(measurement: Measurement): Unit

  def statistics(): A
}

class SlidingAnalyzer[A](analyzer: Iterable[Measurement] => A, window: FiniteDuration) extends Analyzer[A] {
  require(window.toMillis > 0, "Window should be positive")

  private val queue = new mutable.PriorityQueue[Measurement]()(Ordering[Long].on[Measurement](_.timestamp).reverse)

  override def add(measurement: Measurement): Unit = {
    queue.enqueue(measurement)
    val minTimestamp = measurement.timestamp - window.toSeconds

    while (queue.nonEmpty && queue.head.timestamp < minTimestamp) {
      queue.dequeue()
    }
  }

  override def statistics(): A = analyzer(queue)
}

//todo too big out-of-order delivery

//add log rolling window (queue performance)
//todo use tree map
/**
  * Add method - amortized O(1)
  * Statistics method - O(1)
  */
class SlidingCounterAnalyzer(window: FiniteDuration) extends SlidingAnalyzer[Int](_.size, window)

/**
  * Add method - amortized O(1)
  * Statistics method - O(rolling window)
  */
class SlidingMinAnalyzer(window: FiniteDuration) extends SlidingAnalyzer[Double](_.map(_.value).min, window)

/**
  * Add method - amortized O(1)
  * Statistics method - O(rolling window)
  */
class SlidingMaxAnalyzer(window: FiniteDuration) extends SlidingAnalyzer[Double](_.map(_.value).max, window)

/**
  * We can loose accuracy during sum
  * Add method - amortized O(1)
  * Statistics method - O(rolling window)
  */
class SlidingSumAnalyzer(window: FiniteDuration) extends SlidingAnalyzer[Double](_.map(_.value).sum, window)

class AggregateAnalyzer(window: FiniteDuration) extends Analyzer[TotalStatistics] {
  private val counter = new SlidingCounterAnalyzer(window)
  private val min = new SlidingMinAnalyzer(window)
  private val max = new SlidingMaxAnalyzer(window)
  private val sum = new SlidingSumAnalyzer(window)

  override def add(measurement: Measurement): Unit = {
    counter.add(measurement)
    min.add(measurement)
    max.add(measurement)
    sum.add(measurement)
  }

  override def statistics(): TotalStatistics = {
    //todo
    TotalStatistics(-1l, -1, counter.statistics(), min.statistics(), max.statistics(), sum.statistics())
  }
}

//we can use other algorithms based on our ratio for add and statistics requests

case class TotalStatistics(timestamp: Long, value: Double, count: Int, min: Double, max: Double, sum: Double)

object IOUtils {
  type Closeable = {def close(): Unit}

  def using[A <: Closeable](a: A)(f: A => Unit): Unit = {
    import scala.language.reflectiveCalls
    try f(a) finally a.close()
  }
}
