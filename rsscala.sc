//> using scala 3.3.4
//> using toolkit 0.6.0
//> using dep "org.scala-lang.modules::scala-xml:2.3.0"

import sttp.client4.quick.*
import sttp.client4.Response
import sttp.model.Uri

val rss_uri: Uri = Uri.parse(args(0))
    .getOrElse(throw new IllegalArgumentException("Invalid URI"))

val response: Response[String] = quickRequest
    .get(rss_uri)
    .send()

val rss_xml: scala.xml.Elem = scala.xml.XML.loadString(response.body)

val siteTitleText = (rss_xml \ "channel" \ "title").text

println(s"Site Title: $siteTitleText")

for {
    item <- rss_xml \ "channel" \ "item"
} {
    val articleTitle = (item \ "title").text
    val articleLink = (item \ "link").text
    val articleDescription = (item \ "description").text

    if (item \ "pubDate").nonEmpty then {
        val articlePubDate = (item \ "pubDate").text
        println(s"Title: $articleTitle, Link: $articleLink, PubDate: $articlePubDate")
    } else if (item \ "dc:date").nonEmpty then {
        val articlePubDate = (item \ "dc:date").text
        println(s"Title: $articleTitle, Link: $articleLink, PubDate: $articlePubDate")
    } else {
        val articlePubDate = (item \ "<a10:updated>").text
        println(s"Title: $articleTitle, Link: $articleLink, PubDate: $articlePubDate")

    }
}
