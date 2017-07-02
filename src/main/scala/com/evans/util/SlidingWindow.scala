package com.evans.util

import com.evans.model.WindowChanged

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

/**
  * Created by vans239 on 02/07/17.
  */

/**
  * Class to maintain sliding window.
  * For each input element emits changes of sliding window from previous.
  *
  * Memory consumption O(sliding window size)
  * Performance O(log of sliding window size) for each element
  *
  * Important:
  * This implementation properly handle out-of-delivery with some tolerance (less than window size)
  * This implementation has internal buffer to store current sliding window. It doesn't have limit on memory.
  * So it can produce OutOfMemoryException
  *
  * @param extractor method to extract timestamp from base element
  * @param window sliding window size
  * @tparam A base element
  */
class SlidingWindow[A](extractor: A => Long, window: FiniteDuration) {
  require(window.toMillis > 0, "Window should be positive")

  private val queue = new mutable.PriorityQueue[A]()(Ordering[Long].on[A](extractor).reverse)

  def onElement(el: A): WindowChanged[A] = {
    queue.enqueue(el)
    val minTimestamp = extractor(el) - window.toSeconds

    var outdated = Seq.empty[A]
    while (queue.nonEmpty && extractor(queue.head) <= minTimestamp) {
      outdated = outdated.+:(queue.dequeue())
    }

    WindowChanged(el, outdated.reverse)
  }

}
