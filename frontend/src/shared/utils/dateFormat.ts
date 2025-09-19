/**
 * 날짜 문자열을 한국 형식으로 포맷팅
 * @param dateString - ISO 날짜 문자열 (예: "2024-01-15T10:30:00Z")
 * @returns 포맷팅된 날짜 (예: "2024.01.15")
 */
export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date
    .toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
    .replace(/\. /g, '.')
    .replace(/\.$/, '');
};

/**
 * 날짜 문자열을 년-월-일 형식으로 포맷팅
 * @param dateString - ISO 날짜 문자열
 * @returns YYYY-MM-DD 형식 (예: "2024-01-15")
 */
export const formatDateYMD = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toISOString().split('T')[0];
};

/**
 * 날짜 문자열을 간단한 월.일 형식으로 포맷팅
 * @param dateString - ISO 날짜 문자열
 * @returns MM.DD 형식 (예: "01.15")
 */
export const formatDateMD = (dateString: string): string => {
  const date = new Date(dateString);
  return date
    .toLocaleDateString('ko-KR', {
      month: '2-digit',
      day: '2-digit',
    })
    .replace(/\. /g, '.')
    .replace(/\.$/, '');
};
