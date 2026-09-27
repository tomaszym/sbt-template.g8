package $package$.core

import io.circe.parser.decode
import io.circe.syntax.*

class SummarySuite extends munit.FunSuite {

  test("the wire form names both fields and writes a missing mean as null") {
    assertEquals(Summary.of(Nil).asJson.noSpaces, """{"count":0,"mean":null}""")
  }

  test("what is written reads back") {
    val summary = Summary.of(Seq(1.0, 2.0, 6.0))
    assertEquals(decode[Summary](summary.asJson.noSpaces), Right(summary))
  }
}
