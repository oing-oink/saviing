package saviing.game.item.domain.model.vo;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 아이템 이미지 URL Value Object
 * 이미지 URL의 유효성을 검증합니다.
 */
public record ImageUrl(
    String value
) {
    public ImageUrl {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 비어있을 수 없습니다");
        }

        value = value.trim();
        validateUrl(value);
    }

    /**
     * String 값으로 ImageUrl을 생성합니다.
     *
     * @param value 이미지 URL
     * @return ImageUrl 인스턴스
     */
    public static ImageUrl of(String value) {
        return new ImageUrl(value);
    }

    /**
     * URL의 유효성을 검증합니다.
     *
     * @param url 검증할 URL
     * @throws IllegalArgumentException URL이 유효하지 않은 경우
     */
    private void validateUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("유효하지 않은 URL 형식입니다: " + url, e);
        }

        // HTTPS 프로토콜 권장
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            throw new IllegalArgumentException("URL은 http:// 또는 https://로 시작해야 합니다: " + url);
        }
    }

    /**
     * URL의 파일 확장자를 반환합니다.
     *
     * @return 파일 확장자 (없으면 빈 문자열)
     */
    public String getExtension() {
        int lastDotIndex = value.lastIndexOf('.');
        int lastSlashIndex = value.lastIndexOf('/');

        if (lastDotIndex > lastSlashIndex && lastDotIndex < value.length() - 1) {
            return value.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

    /**
     * 이미지 파일 확장자인지 확인합니다.
     *
     * @return 이미지 파일인지 여부
     */
    public boolean isImageFile() {
        String extension = getExtension();
        return extension.equals("jpg") || extension.equals("jpeg") ||
               extension.equals("png") || extension.equals("gif") ||
               extension.equals("webp") || extension.equals("svg");
    }
}