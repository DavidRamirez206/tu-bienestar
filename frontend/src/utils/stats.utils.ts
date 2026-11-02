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

/**
 * Calculates comprehensive stats for the user dashboard.
 */
export const calculateDashboardStats = (progress: ProgressResponse[]): DashboardStats => {
  const now = new Date();
  
  // 1. Weekly Reps (Last 7 days)
  const weeklyReps: ChartDataPoint[] = [];
  let activeDaysCount = 0;
  for (let i = 6; i >= 0; i--) {
    const date = new Date(now);
    date.setDate(date.getDate() - i);
    const dateStr = date.toISOString().split('T')[0];
    const label = i === 0 ? 'Hoy' : date.toLocaleDateString(undefined, { weekday: 'short' });
    
    const dayEntries = progress.filter(p => p.dateLogged.startsWith(dateStr));
    const totalReps = dayEntries.reduce((sum, p) => sum + (p.reps || 0), 0);
    if (dayEntries.length > 0) {activeDaysCount++;}
    
    weeklyReps.push({ label, value: totalReps });
  }

  // 2. Monthly Volume (Weight * Reps) - Last 4 Weeks
  const monthlyVolume: ChartDataPoint[] = [];
  const weekLabels = ['Hace 3 sem', 'Hace 2 sem', 'Semana pasada', 'Esta semana'];
  for (let i = 3; i >= 0; i--) {
    const start = new Date(now);
    start.setDate(start.getDate() - (i + 1) * 7);
    const end = new Date(now);
    end.setDate(end.getDate() - i * 7);
    
    const weekEntries = progress.filter(p => {
        const pDate = new Date(p.dateLogged);
        return pDate >= start && pDate < end;
    });
    
    // Volume = Sum of (reps * weight)
    const totalVolume = weekEntries.reduce((sum, p) => sum + ((p.reps || 0) * (p.weightKg || 0)), 0);
    monthlyVolume.push({ label: weekLabels[3-i], value: totalVolume });
  }

  // 3. Overall KPI Metrics
  const maxWeight = progress.length > 0 ? Math.max(...progress.map(p => p.weightKg || 0)) : 0;
  const totalReps = progress.reduce((sum, p) => sum + (p.reps || 0), 0);
  const avgReps = progress.length > 0 ? Math.round(totalReps / progress.length) : 0;

  // 4. Most frequent exercise
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
    weeklyReps,
    monthlyVolume,
    totalSessions: progress.length,
    maxWeight,
    avgReps,
    activeDaysThisWeek: activeDaysCount,
    mostFrequentExerciseId: mostFrequentId
  };
};
