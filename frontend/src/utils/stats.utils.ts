import type { ProgressResponse } from "../types/api.types";

export type ChartDataPoint = {
  label: string;
  value: number;
};

export type DashboardStats = {
    weeklyReps: ChartDataPoint[];
    monthlyVolume: ChartDataPoint[];
    totalSessions: number;
    maxWeight: number;
    avgReps: number;
    activeDaysThisWeek: number;
    mostFrequentExerciseId: number | null;
};

export type StatsPeriod = "week" | "month";

export type PeriodCharts = {
  reps: ChartDataPoint[];
  volume: ChartDataPoint[];
  /** true cuando hay al menos un registro dentro del periodo. */
  hasData: boolean;
  /** Texto del rango que cubre el periodo, por ejemplo "20/11/2026 – 26/11/2026". */
  rangeLabel: string;
};

const DAY_MS = 24 * 60 * 60 * 1000;

/** Fecha local en formato YYYY-MM-DD (evita el corrimiento de dia de toISOString). */
export const toLocalDateKey = (date: Date): string => {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
};

const startOfDay = (date: Date): Date => new Date(date.getFullYear(), date.getMonth(), date.getDate());

const addDays = (date: Date, days: number): Date =>
  new Date(date.getFullYear(), date.getMonth(), date.getDate() + days);

const formatShortDate = (date: Date): string => `${date.getDate()}/${date.getMonth() + 1}`;

const volumeOf = (p: ProgressResponse): number => (p.reps || 0) * (p.weightKg || 0);

/**
 * Agrupa los registros por periodo:
 * - "week": los ultimos 7 dias, una barra por dia.
 * - "month": los ultimos 28 dias, una barra por semana.
 */
export const calculatePeriodCharts = (
  progress: ProgressResponse[],
  period: StatsPeriod,
  now: Date = new Date(),
): PeriodCharts => {
  const today = startOfDay(now);
  const buckets: { start: Date; end: Date; label: string }[] = [];

  if (period === "week") {
    for (let i = 6; i >= 0; i--) {
      const start = addDays(today, -i);
      const label = i === 0 ? "Hoy" : start.toLocaleDateString("es-CO", { weekday: "short" });
      buckets.push({ start, end: addDays(start, 1), label });
    }
  } else {
    for (let i = 3; i >= 0; i--) {
      const start = addDays(today, -(i * 7 + 6));
      const end = addDays(today, -(i * 7) + 1);
      buckets.push({ start, end, label: `${formatShortDate(start)}–${formatShortDate(addDays(end, -1))}` });
    }
  }

  const reps: ChartDataPoint[] = [];
  const volume: ChartDataPoint[] = [];
  let hasData = false;

  for (const bucket of buckets) {
    const entries = progress.filter((p) => {
      const t = new Date(p.dateLogged).getTime();
      return t >= bucket.start.getTime() && t < bucket.end.getTime();
    });
    if (entries.length > 0) {
      hasData = true;
    }
    reps.push({ label: bucket.label, value: entries.reduce((sum, p) => sum + (p.reps || 0), 0) });
    volume.push({ label: bucket.label, value: entries.reduce((sum, p) => sum + volumeOf(p), 0) });
  }

  const first = buckets[0].start;
  const last = new Date(buckets[buckets.length - 1].end.getTime() - DAY_MS);
  const rangeLabel = `${first.toLocaleDateString("es-CO")} – ${last.toLocaleDateString("es-CO")}`;

  return { reps, volume, hasData, rangeLabel };
};

/**
 * Filtra los registros entre dos fechas YYYY-MM-DD, ambas incluidas.
 * Si alguna fecha viene vacia, ese extremo queda abierto.
 */
export const filterProgressByDateRange = (
  progress: ProgressResponse[],
  from?: string,
  to?: string,
): ProgressResponse[] => {
  return progress.filter((p) => {
    const key = toLocalDateKey(new Date(p.dateLogged));
    if (from && key < from) {
      return false;
    }
    if (to && key > to) {
      return false;
    }
    return true;
  });
};

/**
 * Calculates comprehensive stats for the user dashboard.
 */
export const calculateDashboardStats = (progress: ProgressResponse[]): DashboardStats => {
  const now = new Date();
  const weekly = calculatePeriodCharts(progress, "week", now);
  const monthly = calculatePeriodCharts(progress, "month", now);

  const activeDaysThisWeek = weekly.reps.filter((_, i) => {
    const day = addDays(startOfDay(now), i - 6);
    const key = toLocalDateKey(day);
    return progress.some((p) => toLocalDateKey(new Date(p.dateLogged)) === key);
  }).length;

  // Overall KPI Metrics
  const maxWeight = progress.length > 0 ? Math.max(...progress.map(p => p.weightKg || 0)) : 0;
  const totalReps = progress.reduce((sum, p) => sum + (p.reps || 0), 0);
  const avgReps = progress.length > 0 ? Math.round(totalReps / progress.length) : 0;

  // Most frequent exercise
  const exerciseCounts: Record<number, number> = {};
  progress.forEach(p => {
      exerciseCounts[p.exerciseId] = (exerciseCounts[p.exerciseId] || 0) + 1;
  });
  let mostFrequentId = null;
  let maxCount = 0;
  for (const [id, count] of Object.entries(exerciseCounts)) {
      if (count > maxCount) {
          maxCount = count;
          mostFrequentId = Number(id);
      }
  }

  return {
    weeklyReps: weekly.reps,
    monthlyVolume: monthly.volume,
    totalSessions: progress.length,
    maxWeight,
    avgReps,
    activeDaysThisWeek,
    mostFrequentExerciseId: mostFrequentId
  };
};
