package saviing.game.room.domain.model.aggregate;

/**
 * 방 내 아이템 배치 카테고리를 나타내는 열거형
 * 아이템의 배치 영역과 타입을 구분하는 용도
 */
public enum Category {
    /**
     * 좌측 영역
     */
    LEFT,

    /**
     * 우측 영역
     */
    RIGHT,

    /**
     * 하단 영역 (바닥에 배치되는 아이템)
     */
    BOTTOM,

    /**
     * 방 색상 (배경색 관련)
     */
    ROOM_COLOR,

    /**
     * 펫 (CAT 등)
     */
    PET
}