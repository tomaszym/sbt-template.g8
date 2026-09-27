package $package$.core

object Mean {

  /** None for no values rather than NaN, which would pass silently through every sum after it. */
  def of(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None else Some(xs.sum / xs.size)
}
