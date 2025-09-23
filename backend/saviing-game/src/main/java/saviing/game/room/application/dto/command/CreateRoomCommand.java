package saviing.game.room.application.dto.command;

import saviing.game.room.domain.model.vo.RoomNumber;

/**
 * 방 생성 Command DTO
 * 새로운 방을 생성하기 위한 요청 데이터를 담는 불변 객체이다.
 * 캐릭터 식별자와 방 번호를 포함하여 방 생성에 필요한 최소한의 정보를 제공한다.
 *
 * @param characterId 방을 소유할 캐릭터의 식별자
 * @param roomNumber 생성할 방의 번호 VO
 */
public record CreateRoomCommand(
    Long characterId,
    RoomNumber roomNumber
) {

    /**
     * Byte 타입 방 번호를 사용하여 CreateRoomCommand를 생성하는 정적 팩토리 메서드
     * 외부 API나 다른 레이어에서 primitive 타입으로 전달받을 때 사용된다.
     *
     * @param characterId 방을 소유할 캐릭터의 식별자
     * @param roomNumber 방 번호 (1-5 범위의 byte 값)
     * @return 생성된 CreateRoomCommand 인스턴스
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     * @throws IllegalArgumentException roomNumber가 null이거나 유효하지 않은 범위인 경우
     */
    public static CreateRoomCommand of(Long characterId, Byte roomNumber) {
        if (characterId == null || characterId <= 0) {
            throw new IllegalArgumentException("캐릭터 식별자는 필수이며 양수여야 합니다");
        }
        if (roomNumber == null) {
            throw new IllegalArgumentException("방 번호는 필수입니다");
        }

        return new CreateRoomCommand(characterId, RoomNumber.of(roomNumber));
    }

    /**
     * Command의 유효성을 검증하는 메서드
     * 모든 필수 필드가 올바른 값을 가지고 있는지 확인한다.
     * 서비스 레이어에서 비즈니스 로직 실행 전 호출되어야 한다.
     *
     * @throws IllegalArgumentException characterId가 null이거나 0 이하인 경우
     * @throws IllegalArgumentException roomNumber가 null인 경우
     */
    public void validate() {
        if (characterId == null || characterId <= 0) {
            throw new IllegalArgumentException("캐릭터 식별자는 필수이며 양수여야 합니다");
        }
        if (roomNumber == null) {
            throw new IllegalArgumentException("방 번호는 필수입니다");
        }
        // RoomNumber VO가 이미 생성되어 있으므로 별도 검증 불필요
    }
}