package com.evans.impl

import com.evans._
import com.evans.model.{Measurement, TotalStatistics, WindowChanged}

/**
  * Created by vans239 on 02/07/17.
  */
class AggregateAnalyzer extends WindowAnalyzer[Measurement, TotalStatistics] {
  private val counterAnalyzer = Statistics.sizeAnalyzer[Measurement]
  private val orderingAnalyzer = Statistics.orderedAnalyzer
  private val sumAnalyzer = Statistics.sumAnalyzer

  def onWindowChange(windowChanged: WindowChanged[Measurement]): TotalStatistics = {
    val count = counterAnalyzer.onWindowChange(windowChanged)
    val tree = orderingAnalyzer.onWindowChange(windowChanged)
    val sum = sumAnalyzer.onWindowChange(windowChanged)
    val curr = windowChanged.added
    TotalStatistics(curr.timestamp, curr.value, count, tree.head.value, tree.last.value, sum)
  }

}
