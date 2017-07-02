package com.evans.impl

import com.evans.WindowAnalyzer
import com.evans.model.WindowChanged

/**
  * Created by vans239 on 02/07/17.
  */

/**
  * Analyzer, which folds previous state with current update
  * @param initValue initial value
  * @param fold fold function
  * @tparam A base element
  * @tparam B analyzer statistics
  */
class FoldWindowAnalyzer[A, B](initValue: B)(fold: (B, WindowChanged[A]) => B) extends WindowAnalyzer[A, B] {
  private var currValue = initValue

  override def onWindowChange(windowChanged: WindowChanged[A]): B = {
    currValue = fold(currValue, windowChanged)
    currValue
  }

}
