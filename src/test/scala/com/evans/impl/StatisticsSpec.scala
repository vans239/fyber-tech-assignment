package com.evans.impl

import com.evans.WindowAnalyzer
import com.evans.model.{Measurement, WindowChanged}
import org.scalatest.{Matchers, WordSpecLike}

/**
  * Created by vans239 on 02/07/17.
  */
class StatisticsSpec extends WordSpecLike with Matchers {

  private val data = {
    val a = Measurement(1, 10)
    val b = Measurement(31, 20)
    val c = Measurement(61, 25)
    val d = Measurement(67, 3)
    Seq(
      WindowChanged(a, Seq.empty),
      WindowChanged(b, Seq.empty),
      WindowChanged(c, Seq(a)),
      WindowChanged(d, Seq.empty)
    )
  }

  def validate[A](analyzer: WindowAnalyzer[Measurement, A], expected: Seq[A]): Unit =
    data.map(analyzer.onWindowChange) shouldEqual expected

  "Statistics" should {
    "process size statistics" in {
      validate(Statistics.sizeAnalyzer, Seq(1, 2, 2, 3))
    }

    "process sum statistics" in {
      validate(Statistics.sumAnalyzer, Seq[Double](10, 30, 45, 48))
    }

    "process min/max statistics" in {
      val analyzer = Statistics.orderedAnalyzer
      val minExpected = Seq[Double](10, 10, 20, 3)
      val maxExpected = Seq[Double](10, 20, 25, 25)
      val actual = data.map(analyzer.onWindowChange)
      actual.map(_.head.value) shouldEqual minExpected
      actual.map(_.last.value) shouldEqual maxExpected
    }
  }

}
