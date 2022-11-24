package poc.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController (private val bookShelf: BookService) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getBooks(): Map<Long,Book> {
        return bookShelf.books
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getBook(@PathVariable id: Long): Book? {
        return bookShelf.getBook(id)
    }

    @PostMapping("/new")
    fun addBook(@RequestBody book: Book): ResponseEntity<*> {
        val response: Boolean = bookShelf.addBook(book)
        return if(response) ResponseEntity(book, HttpStatus.CREATED)
        else ResponseEntity(book, HttpStatus.CONFLICT)
    }

    @PutMapping("/{id}")
    fun putBook(@RequestBody book: Book, @PathVariable id: Long): ResponseEntity<*> {
        val response = bookShelf.putBook(book, id)
        return if(response) ResponseEntity(book, HttpStatus.OK)
        else ResponseEntity(book, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id:Long): ResponseEntity<*> {
        val book = bookShelf.deleteBook(id)
        return ResponseEntity(book, HttpStatus.NO_CONTENT)
    }
}