package dburyak.benchmark.book.service

import dburyak.benchmark.book.domain.Genre

class GenreService {
    private final List<Genre> allGenres = [
            'bildungsroman',
            'picaresque novel',
            'satire',
            'folk',
            'children'
    ].collect { new Genre(name: it) }
}
