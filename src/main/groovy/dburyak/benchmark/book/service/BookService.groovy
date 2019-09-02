package dburyak.benchmark.book.service

import dburyak.benchmark.book.domain.Book

import java.time.LocalDate

import static java.time.Month.MAY

class BookService {
    private final List<Book> allBooks = [
            new Book(title: 'The Adventures of Tom Sawyer', numPages: 270, isbn: '978-1948132824',
                    publishDay: LocalDate.of(2018, MAY, 6), publisher: 'SeaWolf Press')
    ]
}
