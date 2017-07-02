package com.evans.util

import com.evans.model.WindowChanged

import scala.concurrent.duration.FiniteDuration

/**
  * Created by vans239 on 02/07/17.
  */
object CollectionUtils {
  def withSlidingWindow[A](window: FiniteDuration)
                          (extractor: A => Long)
                          (it: Iterator[A]): Iterator[WindowChanged[A]] = {
    val slidingWindow = new SlidingWindow[A](extractor, window)
    it.map(slidingWindow.onElement)
  }

  object Implicits {

    implicit class IteratorOps[A](it: Iterator[A]) {
      def slidingWindow(window: FiniteDuration)(extractor: A => Long): Iterator[WindowChanged[A]] =
        withSlidingWindow(window)(extractor)(it)
    }

  }
}
