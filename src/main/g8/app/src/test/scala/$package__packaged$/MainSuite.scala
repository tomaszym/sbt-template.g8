package $package$

class MainSuite extends munit.FunSuite {

  test("numbers parse") {
    assertEquals(Main.parse(List("1", "2.5", "-3e2")), Right(List(1.0, 2.5, -300.0)))
  }

  test("one bad argument fails the whole run and is named") {
    assertEquals(Main.parse(List("1", "two", "3")), Left("not a number: two"))
  }
}
