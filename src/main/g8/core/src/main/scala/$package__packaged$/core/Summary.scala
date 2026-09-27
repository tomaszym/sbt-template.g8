package $package$.core

import io.circe.{Decoder, Encoder}

final case class Summary(count: Int, mean: Option[Double])

object Summary {

  def of(xs: Seq[Double]): Summary = Summary(xs.size, Mean.of(xs))

  // Written out rather than derived: these names are the wire form, and renaming a field must not change it.
  given Encoder[Summary] = Encoder.forProduct2("count", "mean")(s => (s.count, s.mean))
  given Decoder[Summary] = Decoder.forProduct2("count", "mean")(Summary.apply)
}
