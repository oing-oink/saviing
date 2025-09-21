const currencyFormatter = new Intl.NumberFormat('ko-KR');

const dateFormatter = new Intl.DateTimeFormat('ko-KR', {
  month: 'long',
  day: 'numeric',
  weekday: 'short',
});

export const formatCurrency = (value: number) => {
  if (!Number.isFinite(value)) {
    return '0';
  }

  const positiveValue = Math.max(0, Math.trunc(value));
  return currencyFormatter.format(positiveValue);
};

export const formatDateLabel = (value: Date | string) => {
  const date = typeof value === 'string' ? new Date(value) : value;

  if (Number.isNaN(date.getTime())) {
    return '';
  }

  return dateFormatter.format(date);
};

export const parseNumericInput = (value: string) => {
  const digitsOnly = value.replace(/[^0-9]/g, '');
  return digitsOnly.length > 0 ? Number(digitsOnly) : 0;
};
