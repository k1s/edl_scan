import core.Checker
import org.scalatest._

import scala.collection.JavaConverters._
import scala.language.postfixOps

class CheckerTest extends FunSuite {

  val logDirectory= this.getClass.getClassLoader.getResource("log/").getPath.replace("%20", " ")
  val scanDirectory = this.getClass.getClassLoader.getResource("scan/").getPath.replace("%20", " ")
  val EDL = List("A126C003_150513_R3J0", "A106R3J0").sorted
  val EDLwithFiles = "A001C022_140704_R547" :: EDL sorted
  val EmptyEDL = List[String]() asJava
  val checker = new Checker

  def fromLogs(edl: List[String]): List[String] =
    (checker checkLogs(edl.asJava, logDirectory)).asScala.toList.sorted
  def fromScan(edl: List[String]): List[String] =
    (checker checkScan(edl.asJava, scanDirectory)).asScala.toList.sorted
  def fromScanLogs(edl: List[String]): String =
    checker checkScanLogs(edl.asJava, scanDirectory, logDirectory)

  test("Scan test resources without matches") {
    assert(EmptyEDL == (checker checkLogs(EmptyEDL, logDirectory)))
  }

  test("Scan test resources") {
    val expected = List("\n2 tree.txt   |---->   [A106R3J0, A126C003_150513_R3J0]",
      "\n2 inside tree.txt   |---->   [A106R3J0, A126C003_150513_R3J0]",
      "\n2 inside.txt   |---->   [A106R3J0, A126C003_150513_R3J0]").sorted

     assert(expected == fromLogs (EDL))
  }

  test("Scan test resources with files") {
    val expectedWithFiles = List("\n2 tree.txt   |---->   [A106R3J0, A126C003_150513_R3J0]",
      "\n2 inside tree.txt   |---->   [A106R3J0, A001C022_140704_R547, A126C003_150513_R3J0]",
      "\n2 inside.txt   |---->   [A106R3J0, A001C022_140704_R547, A126C003_150513_R3J0]").sorted

    assert(expectedWithFiles == fromLogs (EDLwithFiles))
  }

  test("Scan all logs") {
    println(checker checkLogs(EDLwithFiles.asJava, "/Volumes/DATA/logs/"))
  }

  test("Check scan and not find anything") {
    assert(EDLwithFiles == fromScan (EDLwithFiles))
  }

  test("Check scan and find all") {
    val expected0 = List("A001_C003", "A001_C003_001", "test3", "A026C005").sorted
    assert(List() == fromScan (expected0))
  }

  test("Check scan and not find EXPECTED") {
    val expected1 = List("A001_C003", "A001_C003_001", "test3", "A026C005", "EXPECTED").sorted
    assert(List("EXPECTED") == fromScan (expected1))
  }

  test("Check scan, check logs and find all") {
    val expected = "\nNOT FOUND [A001C022_140704_R547, A106R3J0, A126C003_150513_R3J0]" +
      "\nFOUND IN LOGS {2 inside.txt=[A106R3J0, A001C022_140704_R547, A126C003_150513_R3J0]," +
      " 2 tree.txt=[A106R3J0, A126C003_150513_R3J0]," +
      " 2 inside tree.txt=[A106R3J0, A001C022_140704_R547, A126C003_150513_R3J0]}" +
      "\nNOT FOUND AT ALL []"
    assert(expected == fromScanLogs (EDLwithFiles))
  }

  test("Check scan, check logs and not find EXPECTED") {
    val expected = "\nNOT FOUND [EXPECTED, A001C022_140704_R547, A106R3J0, A126C003_150513_R3J0]" +
      "\nFOUND IN LOGS {2 inside.txt=[A106R3J0, A001C022_140704_R547, A126C003_150513_R3J0]," +
      " 2 tree.txt=[A106R3J0, A126C003_150513_R3J0]," +
      " 2 inside tree.txt=[A106R3J0, A001C022_140704_R547, A126C003_150513_R3J0]}" +
      "\nNOT FOUND AT ALL [EXPECTED]"
    assert(expected == fromScanLogs ("EXPECTED" :: EDLwithFiles))
  }

}
