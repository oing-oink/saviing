package saviing.game.item.presentation.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * 아이템 목록 API 응답 DTO
 */
@Builder
public record ItemListResponse(
    List<ItemResponse> items,
    int totalCount
) {

    /**
     * 빈 응답을 생성합니다.
     *
     * @return 빈 ItemListResponse
     */
    public static ItemListResponse empty() {
        return ItemListResponse.builder()
            .items(List.of())
            .totalCount(0)
            .build();
    }
}