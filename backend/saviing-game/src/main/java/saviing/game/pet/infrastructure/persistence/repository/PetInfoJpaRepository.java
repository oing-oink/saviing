package saviing.game.pet.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saviing.game.pet.infrastructure.persistence.entity.PetInfoEntity;

/**
 * PetInfo JPA Repository
 * Spring Data JPA를 사용한 펫 정보 데이터베이스 액세스
 */
@Repository
public interface PetInfoJpaRepository extends JpaRepository<PetInfoEntity, Long> {

    /**
     * 인벤토리 아이템 ID로 펫 정보를 조회합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 펫 정보 (Optional)
     */
    Optional<PetInfoEntity> findByInventoryItemId(Long inventoryItemId);

    /**
     * 인벤토리 아이템 ID로 펫 정보 존재 여부를 확인합니다.
     *
     * @param inventoryItemId 인벤토리 아이템 ID
     * @return 펫 정보 존재 여부
     */
    boolean existsByInventoryItemId(Long inventoryItemId);

}