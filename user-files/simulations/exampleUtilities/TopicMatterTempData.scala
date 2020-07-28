package examplePackageName.exampleUtilities

import java.io.{FileWriter}
import sys.process._
import scala.io.Source

object TopicMatterTempDataConfig {
  val feedname = "/var/tmp/example1-load-testing.csv"
}

object TopicMatterTempData {

  def emptyFile = {
    val writer = new FileWriter(TopicMatterTempDataConfig.feedname, false)
    writer.write("");
    writer.close();
  }

  def initFileWriter = {
    emptyFile
    val writer = new FileWriter(TopicMatterTempDataConfig.feedname, true)
    writer
  }

  val fw = initFileWriter

  def addData(subjectIds: List[String]) = {
    val text = subjectIds.mkString("\n")
    fw.write(s"$text\n")
    fw.flush()
  }

  def dedupe(): List[String] = {
    val uniqList = Source.fromFile(TopicMatterTempDataConfig.feedname).getLines.toList.distinct
    emptyFile
    fw.write("subjectId\n")
    fw.flush()
    addData(uniqList)
    uniqList
  }
}
