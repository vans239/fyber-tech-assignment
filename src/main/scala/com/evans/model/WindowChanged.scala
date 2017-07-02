package com.evans.model

/**
  * Created by vans239 on 02/07/17.
  */
case class WindowChanged[A](added: A, removed: Seq[A])
