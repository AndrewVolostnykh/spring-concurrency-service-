package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    Image findOneById(UUID id);

    @Query(value = "select cast(id as varchar(255)) as imageId, " +
                            "hemmingMatchPercent(hash, :hash) * 100 as matchPercent, " +
                            "url as imageUrl " +
                    " from images" +
                    " where hemmingMatchPercent(hash, :hash) >= :threshold", nativeQuery = true)
    List<SearchResultDTO> getSearchResult(@Param("hash") long hash, @Param("threshold") double threshold);
}
