package bsa.java.concurrency.image.dto;

import org.springframework.beans.factory.annotation.Value;

public interface SearchResultDTO {
    @Value("#{target.imageId}")
    String getImageId();
    @Value("#{target.matchPercent}")
    Double getMatchPercent();
    @Value("#{target.imageUrl}")
    String getImageUrl();
}
