package bsa.java.concurrency.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultDtoImpl implements SearchResultDTO {

    private UUID id;
    private String url;
    private Double matchPercent;

    @Override
    public UUID getImageId() {
        return this.id;
    }

    @Override
    public Double getMatchPercent() {
        return this.matchPercent;
    }

    @Override
    public String getImageUrl() {
        return this.url;
    }

}
