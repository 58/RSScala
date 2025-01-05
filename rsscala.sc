//> using scala 3.3.4
//> using toolkit 0.6.0

import sttp.client4.quick.*
import sttp.client4.Response
import sttp.model.Uri

val rss_uri: Uri = Uri.parse(args(0))
    .getOrElse(throw new IllegalArgumentException("Invalid URI"))

val response: Response[String] = quickRequest
    .get(rss_uri)
    .send()

println(response.body)
