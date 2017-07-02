package com.evans.util

import com.evans.model.WindowChanged
import org.scalatest.{Matchers, WordSpecLike}

/**
  * Created by vans239 on 02/07/17.
  */
class CollectionUtilsSpec extends WordSpecLike with Matchers {
  import com.evans.util.CollectionUtils.Implicits._
  import scala.concurrent.duration._

  "Sliding window" should {
    "process empty input" in {
      Seq.empty[Long].iterator.slidingWindow(60.seconds)(identity).toList shouldEqual List.empty
    }

    "process one big window" in {
      val actual = Seq[Long](1, 2, 3, 4).iterator.slidingWindow(60.seconds)(identity).toList
      val expected = List(
        WindowChanged(1, Seq.empty),
        WindowChanged(2, Seq.empty),
        WindowChanged(3, Seq.empty),
        WindowChanged(4, Seq.empty)
      )
      actual shouldEqual expected
    }

    "process simple sliding window" in {
      val actual = Seq[Long](1, 31, 61).iterator.slidingWindow(60.seconds)(identity).toList
      val expected = List(
        WindowChanged(1, Seq.empty),
        WindowChanged(31, Seq.empty),
        WindowChanged(61, Seq(1))
      )
      actual shouldEqual expected
    }
  }
}
