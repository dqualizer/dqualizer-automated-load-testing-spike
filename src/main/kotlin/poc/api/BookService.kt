package poc.api

import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BookService(val books: HashMap<Long,Book>) {

    init {
        val book1 = Book(1L, "Fancy Stories", "Martin", LocalDate.of(2020, 5, 22))
        val book2 = Book(2L, "Long Stories", "Steve", LocalDate.of(2018, 2, 20))
        val book3 = Book(3L, "Short Stories", "Angela", LocalDate.of(2021, 8, 4))
        books[book1.id!!] = book1
        books[book2.id!!] = book2
        books[book3.id!!] = book3
    }

    fun getBook(id: Long): Book? {
        return books[id]
    }

    fun addBook(book: Book): Boolean {
        var currentID = book.id
        if(currentID == null) {
            currentID = createNewKey()
            book.id = currentID
        }
        else if(books[currentID] != null) return false;

        books[currentID] = book
        return true;
    }

    fun putBook(book: Book, id: Long): Boolean {
        books[id] = book
        return true
    }


    fun deleteBook(id: Long): Book? {
       return books.remove(id)
    }

    private fun createNewKey(): Long {
        var key = 0L
        while (books[key] != null) {
            key++
        }
        return key
    }
}