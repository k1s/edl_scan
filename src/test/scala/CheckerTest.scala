package test.scala

import java.nio.file.Files

import core.Checker
import org.scalatest._
import scala.io.Source;

class CheckerTest extends FunSuite {

  val logDirectory = Source.fromURL(getClass.getClassLoader.getResource("log/"))

  test("Check scalatest") {
    val checker = new Checker;
    assert(checker.getFound == List("test"))
  }

}
