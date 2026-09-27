package $package$.core

object MeanSuite {

  /** For a test that costs minutes: left out of the default run, asked for by name with `$slow_env$=1 sbt test`.
    * See `build.sbt`.
    */
  val Slow: munit.Tag = new munit.Tag("slow")
}

class MeanSuite extends munit.FunSuite {
  import MeanSuite.Slow

  test("no values have no mean") {
    assertEquals(Mean.of(Nil), None)
  }

  test("the mean of 1 to 4 is 2.5") {
    assertEquals(Mean.of(Seq(1.0, 2.0, 3.0, 4.0)), Some(2.5))
  }

  test("the mean of 1 to n is (n + 1) / 2 for every n up to 5000".tag(Slow)) {
    (1 to 5000).foreach { n =>
      assertEquals(Mean.of((1 to n).map(_.toDouble)), Some((n + 1) / 2.0), s"n = \$n")
    }
  }
}
