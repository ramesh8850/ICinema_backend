package com.infy.icinema.repository;

import com.infy.icinema.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
        List<Movie> findByGenre(String genre);

        List<Movie> findByLanguage(String language);

        List<Movie> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCaseOrLanguageContainingIgnoreCase(
                        String title,
                        String genre, String language);

        @org.springframework.data.jpa.repository.Query("SELECT m FROM Movie m WHERE " +
                        "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
                        "(:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
                        "(:language IS NULL OR LOWER(m.language) LIKE LOWER(CONCAT('%', :language, '%'))) AND " +
                        "(:rating IS NULL OR m.averageRating >= :rating)")
        List<Movie> filterMovies(@org.springframework.data.repository.query.Param("title") String title,
                        @org.springframework.data.repository.query.Param("genre") String genre,
                        @org.springframework.data.repository.query.Param("language") String language,
                        @org.springframework.data.repository.query.Param("rating") Double rating);
}
