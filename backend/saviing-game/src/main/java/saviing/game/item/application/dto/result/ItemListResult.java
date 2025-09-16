package saviing.game.item.application.dto.result;

import lombok.Builder;

import java.util.List;

/**
 * 아이템 목록 조회 결과 DTO
 */
@Builder
public record ItemListResult(
    List<ItemResult> items,
    int totalCount
) {

    /**
     * 빈 결과를 생성합니다.
     *
     * @return 빈 ItemListResult
     */
    public static ItemListResult empty() {
        return ItemListResult.builder()
            .items(List.of())
            .totalCount(0)
            .build();
    }

    /**
     * 단일 아이템 결과를 생성합니다.
     *
     * @param item 아이템 결과
     * @return 단일 아이템을 포함한 ItemListResult
     */
    public static ItemListResult of(ItemResult item) {
        return ItemListResult.builder()
            .items(List.of(item))
            .totalCount(1)
            .build();
    }

    /**
     * 아이템 목록으로 결과를 생성합니다.
     *
     * @param items 아이템 목록
     * @return ItemListResult
     */
    public static ItemListResult of(List<ItemResult> items) {
        return ItemListResult.builder()
            .items(items)
            .totalCount(items.size())
            .build();
    }

    /**
     * 결과가 비어있는지 확인합니다.
     *
     * @return 비어있는지 여부
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * 결과의 크기를 반환합니다.
     *
     * @return 아이템 개수
     */
    public int size() {
        return items.size();
    }
}