package com.infy.icinema.repository;

import com.infy.icinema.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
        List<Movie> findByGenre(String genre);

        List<Movie> findByLanguage(String language);

        List<Movie> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCaseOrLanguageContainingIgnoreCase(
                        String title,
                        String genre, String language);

        @Query("SELECT m FROM Movie m WHERE " +
                        "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
                        "(:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
                        "(:language IS NULL OR LOWER(m.language) LIKE LOWER(CONCAT('%', :language, '%'))) AND " +
                        "(:rating IS NULL OR m.averageRating >= :rating)")
        List<Movie> filterMovies(@Param("title") String title,
                        @Param("genre") String genre,
                        @Param("language") String language,
                        @Param("rating") Double rating);
}
