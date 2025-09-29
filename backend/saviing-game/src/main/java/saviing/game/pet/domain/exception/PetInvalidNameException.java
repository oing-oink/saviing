package saviing.game.pet.domain.exception;

/**
 * 펫 이름이 올바르지 않을 때 발생하는 예외
 */
public class PetInvalidNameException extends PetException {

    public PetInvalidNameException(String name) {
        super(PetErrorCode.PET_INVALID_NAME,
              String.format("올바르지 않은 펫 이름입니다. 입력된 값: %s", name));
    }

    public static PetInvalidNameException nullName() {
        return new PetInvalidNameException("null");
    }

    public static PetInvalidNameException emptyName() {
        return new PetInvalidNameException("빈 문자열");
    }

    public static PetInvalidNameException tooLong(String name) {
        return new PetInvalidNameException(name);
    }
}