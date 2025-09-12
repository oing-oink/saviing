package saviing.game.character.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saviing.game.character.infrastructure.persistence.entity.CharacterEntity;

import java.util.List;
import java.util.Optional;

/**
 * Character JPA Repository
 * Spring Data JPA를 사용한 데이터베이스 액세스
 */
@Repository
public interface CharacterJpaRepository extends JpaRepository<CharacterEntity, Long> {

    /**
     * 고객 ID로 활성 캐릭터를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 캐릭터 (Optional)
     */
    @Query("SELECT c FROM CharacterEntity c WHERE c.customerId = :customerId AND c.isActive = true")
    Optional<CharacterEntity> findActiveByCustomerId(@Param("customerId") Long customerId);

    /**
     * 고객 ID로 모든 캐릭터를 조회합니다 (비활성 포함).
     * 
     * @param customerId 고객 ID
     * @return 캐릭터 목록
     */
    List<CharacterEntity> findAllByCustomerId(Long customerId);

    /**
     * 모든 활성 캐릭터를 조회합니다.
     * 
     * @return 활성 캐릭터 목록
     */
    List<CharacterEntity> findAllByIsActiveTrue();

    /**
     * 고객 ID로 캐릭터 존재 여부를 확인합니다.
     * 
     * @param customerId 고객 ID
     * @return 캐릭터 존재 여부
     */
    boolean existsByCustomerId(Long customerId);

    /**
     * 고객 ID로 활성 캐릭터 존재 여부를 확인합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 캐릭터 존재 여부
     */
    @Query("SELECT COUNT(c) > 0 FROM CharacterEntity c WHERE c.customerId = :customerId AND c.isActive = true")
    boolean existsActiveByCustomerId(@Param("customerId") Long customerId);
}