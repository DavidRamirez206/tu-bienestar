import type {
  ExerciseRequest,
  ExerciseResponse,
  RoutineExerciseRequest,
  RoutineRequest,
} from "../types/api.types";

export type RoutineExerciseDraft = {
  exerciseId: number;
  exerciseName?: string;
  sets: number;
  targetReps: number;
  exerciseOrder: number;
};

// Las fechas sin hora se leen en la zona local: new Date("2026-11-02") es UTC y en Colombia
// se mostraria como el dia anterior.
export const formatDateOnly = (value: string): string => {
  const [year, month, day] = value.split("-").map(Number);
  return new Date(year, month - 1, day).toLocaleDateString();
};

export const todayAsDateOnly = (): string => {
  const now = new Date();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  return `${now.getFullYear()}-${month}-${day}`;
};

export const buildRoutinePayload = ({
  name,
  description,
  creationDate,
}: {
  name: string;
  description: string;
  creationDate: string;
}): RoutineRequest => {
  return {
    name: name.trim(),
    description: description.trim() || undefined,
    creationDate,
    isPredesigned: false,
  };
};

export const buildCustomExercisePayload = ({
  name,
  type,
  description,
  durationMin,
  difficulty,
  videoUrl,
}: {
  name: string;
  type: string;
  description: string;
  durationMin: string;
  difficulty: string;
  videoUrl: string;
}): ExerciseRequest => {
  return {
    name: name.trim(),
    type: type.trim(),
    description: description.trim() || undefined,
    durationMin: durationMin.trim() ? Number(durationMin) : undefined,
    difficulty: difficulty.trim() || undefined,
    videoUrl: videoUrl.trim() || undefined,
    isPredefined: false,
  };
};

export const createDraftFromExercise = (
  exerciseId: number,
  currentCount: number,
  exerciseName?: string,
): RoutineExerciseDraft => {
  return {
    exerciseId,
    exerciseName,
    sets: 3,
    targetReps: 10,
    exerciseOrder: currentCount + 1,
  };
};

export const mapDraftToRoutineExerciseRequest = (
  routineId: number,
  draft: RoutineExerciseDraft,
): RoutineExerciseRequest => {
  return {
    routineId,
    exerciseId: draft.exerciseId,
    sets: draft.sets,
    targetReps: draft.targetReps,
    exerciseOrder: draft.exerciseOrder,
  };
};

export const sortExercisesByName = (
  exercises: ExerciseResponse[],
): ExerciseResponse[] => {
  return [...exercises].sort((a, b) => a.name.localeCompare(b.name));
};
