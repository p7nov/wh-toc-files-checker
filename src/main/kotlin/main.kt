import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

fun main(args: Array<String>) {
  println("Getting topic files from topics/")
  val topics = getTopicList(args[0])
  println("Done")
  println("Getting toc from .tree file")
  val toc = getTocAsList(args[0]+ args[1])
  println("Done\n...")

  val missedTopics = topics - toc
  if (missedTopics.isNotEmpty()) {
    println("The following ${missedTopics.count()} files from topics/ are not present in TOC:")
    var i = 1
    for(topic in missedTopics.sorted()) println("${i++}. $topic")
  } else {
    println("All files from topics/ are present in TOC.")
  }
}

fun getTopicList(path: String): List<String> {
  val topics = mutableListOf<String>()
  File("$path/topics").walk().forEach {
    topics.add(it.name)
  }
  return topics.filter { it.endsWith(".md") }
}

fun getTocAsList(treePath: String): List<String> {
  val toc = mutableListOf<String>()

  val xmlToc = readXmlToc(treePath)
  val tocElements = xmlToc.getElementsByTagName("toc-element")
  for (i in 0 until tocElements.length) {
    val item = tocElements.item(i)
    if (item.getNodeType().equals(Node.ELEMENT_NODE)) {
      //println(item.attributes.getNamedItem("id")?.nodeValue)
      val idAttr = item.attributes.getNamedItem("id")
      idAttr ?.let { toc.add(idAttr.nodeValue)}
    }
  }
  return toc
}

fun readXmlToc(treePath: String): Document {
  val xmlFile = File("$treePath")

  val dbFactory = DocumentBuilderFactory.newInstance()
  val dBuilder = dbFactory.newDocumentBuilder()
  val xmlInput = InputSource(StringReader(xmlFile.readText()))
  val doc = dBuilder.parse(xmlInput)

  return doc
}