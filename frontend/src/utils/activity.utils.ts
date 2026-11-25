import type { ActivityType, DayOfWeek } from "../types/api.types";

export const ACTIVITY_TYPES: { value: ActivityType; label: string }[] = [
  { value: "CLASE", label: "Clase" },
  { value: "TORNEO", label: "Torneo" },
  { value: "GIMNASIO", label: "Gimnasio" },
];

export const typeLabel = (type?: string) =>
  ACTIVITY_TYPES.find((option) => option.value === type)?.label ?? "Sin tipo";

export const DAYS: { value: DayOfWeek; label: string }[] = [
  { value: "MONDAY", label: "Lunes" },
  { value: "TUESDAY", label: "Martes" },
  { value: "WEDNESDAY", label: "Miércoles" },
  { value: "THURSDAY", label: "Jueves" },
  { value: "FRIDAY", label: "Viernes" },
  { value: "SATURDAY", label: "Sábado" },
  { value: "SUNDAY", label: "Domingo" },
];

export const dayLabel = (day: string) => DAYS.find((option) => option.value === day)?.label ?? day;

/** Dia de la semana de hoy en el formato del backend. */
export const today = (): DayOfWeek => DAYS[(new Date().getDay() + 6) % 7].value;

/** "07:30:00" -> "07:30". */
export const formatTime = (time: string) => time.slice(0, 5);
