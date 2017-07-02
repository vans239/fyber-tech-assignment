package com.evans.util

/**
  * Created by vans239 on 02/07/17.
  */
object IOUtils {
  type Closeable = {def close(): Unit}

  /**
    * Helper function to automatically close resources
    */
  def using[A <: Closeable](a: A)(f: A => Unit): Unit = {
    import scala.language.reflectiveCalls
    try f(a) finally a.close()
  }
}
