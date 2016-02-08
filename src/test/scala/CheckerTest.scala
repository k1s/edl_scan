package test.scala

import scala.collection.JavaConverters._
import core.Checker
import org.scalatest._
import scala.language.postfixOps

class CheckerTest extends FunSuite {

  val logDirectory= this.getClass.getClassLoader.getResource("log/").getPath.replace("%20", " ")
  val EDLs = List("A126C003_150513_R3J0", "A106R3J0")
  val EDL = EDLs asJava
  val EDLwithFiles = "A001C022_140704_R547" :: EDLs asJava
  val EmptyEDL = List[String]() asJava
  val checker = new Checker

  test("Scan test resources without matches") {
    assert((checker checkLogs(EmptyEDL, logDirectory)) == EmptyEDL)
  }

  test("Scan test resources predifined logs") {
    println(checker checkLogs(EDL, logDirectory))
    assert((checker checkLogs(EDL, logDirectory)) == List("test"))
  }

  test("Scan test resources predifined logs with files") {
    println(checker checkLogs(EDLwithFiles, logDirectory))
    assert((checker checkLogs(EDLwithFiles, logDirectory)) == List("test"))
  }

  test("Scan all logs") {
    println(checker checkLogs(EDL, "/Volumes/DATA/logs"))
  }

}
