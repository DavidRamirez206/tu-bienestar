/**
 * Utility to parse dates coming from the server.
 * Since the backend uses LocalDateTime/LocalDate without timezone,
 * and Supabase/PostgreSQL usually stores in UTC, we need to append 'Z'
 * to ensure the browser treats them as UTC and converts to local time.
 */
export const parseServerDate = (dateStr: string | null | undefined): Date | null => {
  if (!dateStr) {return null;}

  // If it's just a date (YYYY-MM-DD), return it as is but be careful with timezones
  // For LocalDateTime (contains 'T'), we append 'Z' if missing
  let sanitized = dateStr;
  if (sanitized.includes('T') && !sanitized.includes('Z') && !sanitized.includes('+')) {
    sanitized += 'Z';
  }

  const date = new Date(sanitized);
  return isNaN(date.getTime()) ? null : date;
};

export const formatLocalTime = (dateStr: string | null | undefined): string => {
  const date = parseServerDate(dateStr);
  if (!date) {return 'N/A';}
  return date.toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const formatLocalDate = (dateStr: string | null | undefined): string => {
  const date = parseServerDate(dateStr);
  if (!date) {return 'N/A';}
  return date.toLocaleDateString();
};

export const formatLocalDateTime = (dateStr: string | null | undefined): string => {
  const date = parseServerDate(dateStr);
  if (!date) {return 'N/A';}
  return date.toLocaleString();
};
