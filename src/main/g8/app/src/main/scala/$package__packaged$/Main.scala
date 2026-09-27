package $package$

import $package$.core.Summary
import cats.syntax.all.*
import io.circe.syntax.*

/** $name;format="norm"$ <number>... prints how many numbers it was given and their mean, as JSON. */
object Main {

  def main(args: Array[String]): Unit =
    parse(args.toList) match {
      case Right(xs) => println(Summary.of(xs).asJson.noSpaces)
      case Left(err) =>
        System.err.println(err)
        sys.exit(2)
    }

  /** All or nothing: one bad argument fails the run rather than quietly shifting the mean. */
  def parse(args: List[String]): Either[String, List[Double]] =
    args.traverse(a => a.toDoubleOption.toRight(s"not a number: \$a"))
}
