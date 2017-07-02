package com.evans

import com.evans.model.WindowChanged

/**
  * Created by vans239 on 02/07/17.
  */

/**
  * Analyzer for sliding window based on changes of in-flight elements
  * @tparam A base elements
  * @tparam B statistics
  */
trait WindowAnalyzer[A, B] {

  /**
    *
    * @param windowChanged changes of sliding window
    * @return
    */
  def onWindowChange(windowChanged: WindowChanged[A]): B
}
