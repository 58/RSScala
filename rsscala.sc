//> using scala 3.3.4
//> using toolkit 0.6.0
//> using dep "org.scala-lang.modules::scala-xml:2.3.0"

import sttp.client4.quick.*
import sttp.client4.Response
import sttp.model.Uri

class Rsscala(val uri: Uri) {
    private lazy val rssXml: scala.xml.Elem = {
        val response: Response[String] = quickRequest
            .get(uri)
            .send()
        scala.xml.XML.loadString(response.body)
    }

    def siteTitle: String = (rssXml \ "channel" \ "title").text

    private def articleDate(item: scala.xml.Node): String = {
        if (item \ "pubDate").nonEmpty then {
            (item \ "pubDate").text
        } else if (item \ "dc:date").nonEmpty then {
            (item \ "dc:date").text
        } else {
            (item \ "a10:updated").text
        }
    }

    def articleInfo: Seq[(String, String, String, String)] = {
        for {
            item <- rssXml \ "channel" \ "item"
        } yield {
            val articleTitle = (item \ "title").text
            val articleLink = (item \ "link").text
            val articleDescription = (item \ "description").text
            val articlePubDate = articleDate(item)

            (articleTitle, articleLink, articleDescription, articlePubDate)
        }
    }
}

val rss_uri: Uri = Uri.parse(args(0))
    .getOrElse(throw new IllegalArgumentException("Invalid URI"))
val rsscala = new Rsscala(rss_uri)

print(s"site title: ${rsscala.siteTitle}\n\n")

rsscala.articleInfo.foreach { case (title, link, description, pubDate) =>
    println(s"article title: $title")
    println(s"article link: $link")
    println(s"article description: $description")
    println(s"article pubDate: $pubDate\n")
}