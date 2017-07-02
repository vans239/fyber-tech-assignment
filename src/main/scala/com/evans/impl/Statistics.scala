package com.evans.impl

import com.evans.WindowAnalyzer
import com.evans.model.Measurement

import scala.collection.immutable
import scala.collection.immutable.TreeSet

/**
  * Created by vans239 on 02/07/17.
  */
object Statistics {
  /**
    * Counts in-flight elements
    * Memory consumption O(1)
    * Performance O(1)
    */
  def sizeAnalyzer[A]: WindowAnalyzer[A, Int] =
    new FoldWindowAnalyzer[A, Int](0)( (acc, event) => acc + 1 - event.removed.size)

  /**
    * Sum of in-flight elements
    * Memory consumption O(1)
    * Performance O(1)
    */
  def sumAnalyzer: WindowAnalyzer[Measurement, Double] =
    new FoldWindowAnalyzer[Measurement, Double](0d)(
      (acc, event) =>
        acc + event.added.value - event.removed.map(_.value).sum
    )

  /**
    * Tree based view of in-flight measurements. Tree is sorted by value
    * Memory consumption O(sliding window size)
    * Performance O(log of sliding window size)
    */
  def orderedAnalyzer: WindowAnalyzer[Measurement, TreeSet[Measurement]] = {
    val ordering = Ordering[Double].on[Measurement](_.value)
    val initialValue = immutable.TreeSet.empty(ordering)
    new FoldWindowAnalyzer[Measurement, immutable.TreeSet[Measurement]](
      initialValue)(
      (acc, event) => acc.--(event.removed).+(event.added)
    )
  }

}
