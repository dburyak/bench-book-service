package dburyak.benchmark.book.service

import dburyak.benchmark.book.domain.Author

import java.time.LocalDate

import static java.time.Month.NOVEMBER

class AuthorService {
    private final List<Author> allAuthors = [
            new Author(firstName: 'Mark', lastName: 'Twain', birthDay: LocalDate.of(1835, NOVEMBER, 30))
    ]
}
