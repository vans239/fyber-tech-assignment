package com.evans

import java.math.{MathContext, RoundingMode}

import com.evans.impl.{AggregateAnalyzer, FoldWindowAnalyzer}
import com.evans.model.{Measurement, TotalStatistics}
import com.evans.util.IOUtils

import scala.collection.immutable
import scala.collection.immutable.TreeSet
import scala.concurrent.duration.{FiniteDuration, _}
import scala.io.Source
import scala.util.Try

/**
  * Created by vans239 on 01/07/17.
  */
object Main {

  def header: String = s"""T\tV\tN\tRS\tMinV\tMaxV\n---------------------------------------------"""


  def encodeTotalStatistics(statistics: TotalStatistics): String = {
    import statistics._
    f"$timestamp\t$value\t$count\t$sum%5f\t$min\t$max"
  }

  def decodeMeasurement(line: String): Try[Measurement] = Try {
    val Array(timestamp, value) = line.split("[ \t]")
    Measurement(timestamp.toLong, value.toDouble)
  }

  def process(window: FiniteDuration)(source: Source): Unit = {
    val analyzer = new AggregateAnalyzer
    import com.evans.util.CollectionUtils.Implicits._

    println(header)
    source.getLines()
      .flatMap(l => decodeMeasurement(l).toOption)
      .slidingWindow(60.seconds)(_.timestamp)
      .map(analyzer.onWindowChange)
      .map(encodeTotalStatistics)
      .foreach(println)
  }

  def aggregateAnalyzer: WindowAnalyzer[Measurement, TotalStatistics] = new AggregateAnalyzer

  def main(args: Array[String]): Unit = try {
    val filename = args.headOption.getOrElse("data_scala.txt")
    val source = Source.fromFile(filename)
    IOUtils.using(source)(process(60.seconds))
  } catch {
    case e: Throwable =>
      e.printStackTrace()
  }

}
