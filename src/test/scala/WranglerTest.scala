import core.Wrangler
import org.scalatest._

import scala.collection.JavaConverters._
import scala.language.postfixOps

class WranglerTest extends FunSuite {

  val logDirectory= this.getClass.getClassLoader.getResource("log/").getPath.replace("%20", " ")
  val scanDirectory = this.getClass.getClassLoader.getResource("scan/").getPath.replace("%20", " ")
  val destinationDirectory = this.getClass.getClassLoader.getResource("scan_to/").getPath.replace("%20", " ")

  val EDL = List("A126C003_150513_R3J0", "A106R3J0").sorted
  val EDLwithFiles = "A001C022_140704_R547" :: EDL sorted
  val EmptyEDL = List[String]() asJava

  test("Wrangler empty output without files") {
    Wrangler.scan(EDLwithFiles.asJava, scanDirectory, destinationDirectory, false, false, false)
  }


  test("Wrangler empty output with files") {
    Wrangler.scan(EDLwithFiles.asJava, scanDirectory, destinationDirectory, false, false, true)
  }

  test("Wrangler without files") {
    Wrangler.scan((("A026C005" :: "test" :: Nil) ++ EDLwithFiles).asJava,
      scanDirectory, destinationDirectory, false, false, false)
  }

  test("Wrangler with files") {
    Wrangler.scan((("A026C005" :: "test" :: Nil) ++ EDLwithFiles).asJava,
      scanDirectory, destinationDirectory, false, false, true)
  }

}
