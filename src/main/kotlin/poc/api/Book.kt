package poc.api

import java.time.LocalDate

data class Book(
    var id: Long?,
    var name: String,
    var author: String,
    var releaseDate: LocalDate
    )