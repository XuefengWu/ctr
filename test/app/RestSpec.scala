package app



import org.specs2.mutable.Specification

class RestSpec extends Specification with RestTest {

  new java.io.File("csv").listFiles().map{ f =>
    testRests(f.getAbsolutePath)
  }.head

}