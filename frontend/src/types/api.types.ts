// --- AUTH TYPES ---
export type AuthRequest = {
  username: string;
  password: string;
};

export interface AuthResponse {
  token: string;
  user: UserResponse;
  authorities: string[];
}

export interface SessionResponse {
  user: UserResponse;
  authorities: string[];
}

export type RegisterRequest = {
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  password: string;
  age: number;
};

export interface RegisterResponse {
  idUser: number;
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  message: string;
}

// --- CORE DOMAIN TYPES ---

export interface UserResponse {
  idUser: number;
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  age: number;
}

export type UserRequest = {
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  age: number;
  password?: string;
};

export interface ActivityResponse {
  idActivity: number;
  name: string;
  description?: string;
  type?: ActivityType;
  startDate: string; // LocalDate
  endDate: string; // LocalDate
  spaceId?: number;
  spaceName?: string;
  spaceLocation?: string;
}

export type ActivityType = "CLASE" | "TORNEO" | "GIMNASIO";

export type ActivityRequest = {
  name: string;
  description: string;
  type: ActivityType;
  startDate: string;
  endDate: string;
  spaceId: number;
};

export type ActivityFilters = {
  upcoming?: boolean;
  from?: string;
  to?: string;
  type?: ActivityType;
};

export interface ExerciseResponse {
  idExercise: number;
  name: string;
  type: string;
  description?: string;
  durationMin?: number;
  difficulty?: string;
  videoUrl?: string;
  isPredefined: boolean;
  userId?: number;
}

export type ExerciseRequest = {
  name: string;
  type: string;
  description?: string;
  durationMin?: number;
  difficulty?: string;
  videoUrl?: string;
  isPredefined: boolean;
  userId?: number;
};

export interface RoutineResponse {
  idRoutine: number;
  name: string;
  description?: string;
  creationDate: string; // LocalDate
  isPredesigned: boolean;
  level?: string;
  userId?: number;
  routineExercises?: RoutineExerciseResponse[];
}

export type RoutineRequest = {
  name: string;
  description?: string;
  creationDate: string;
  isPredesigned: boolean;
  level?: string;
  userId?: number;
};

export interface RoutineExerciseResponse {
  routineId: number;
  exerciseId: number;
  sets: number;
  targetReps: number;
  exerciseOrder: number;
  exerciseName?: string;
  exerciseType?: string;
  exerciseDifficulty?: string;
}

export type RoutineExerciseRequest = {
  routineId: number;
  exerciseId: number;
  sets: number;
  targetReps: number;
  exerciseOrder: number;
};

export interface SpaceResponse {
  idSpace: number;
  name: string;
  capacity?: number;
  location?: string;
}

export type SpaceRequest = {
  name: string;
  capacity?: number;
  location?: string;
};

export interface ProgressResponse {
  idProgress: number;
  userId: number;
  exerciseId: number;
  routineId?: number;
  dateLogged: string; // LocalDateTime
  reps?: number;
  durationMin?: number;
  effortLevel?: number;
  setNumber?: number;
  weightKg?: number;
}

export type ProgressRequest = {
  userId: number;
  exerciseId: number;
  routineId?: number;
  dateLogged: string;
  reps?: number;
  durationMin?: number;
  effortLevel?: number;
  setNumber?: number;
  weightKg?: number;
};

export interface RecommendationResponse {
  idRecommendation: number;
  trainerId: number;
  trainerFirstName?: string;
  trainerLastName?: string;
  userId: number;
  userFirstName?: string;
  userLastName?: string;
  description: string;
  dateCreated: string; // LocalDate
}

export type RecommendationRequest = {
  trainerId: number;
  userId: number;
  description: string;
  dateCreated: string;
};

export type DayOfWeek =
  | "MONDAY"
  | "TUESDAY"
  | "WEDNESDAY"
  | "THURSDAY"
  | "FRIDAY"
  | "SATURDAY"
  | "SUNDAY";

export interface ScheduleResponse {
  idSchedule: number;
  activityId: number;
  activityName?: string;
  activityType?: ActivityType;
  spaceName?: string;
  dayOfWeek: DayOfWeek;
  startTime: string; // LocalTime
  endTime: string; // LocalTime
}

export type ScheduleFilters = {
  day?: DayOfWeek;
  type?: ActivityType;
};

export type ScheduleRequest = {
  activityId: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
};

export interface AssignmentResponse {
  userId: number;
  userFirstName?: string;
  userLastName?: string;
  userEmail?: string;
  trainerId: number;
  trainerFirstName?: string;
  trainerLastName?: string;
  assignmentDate: string; // LocalDate
}

export type AssignmentRequest = {
  userId: number;
  trainerId: number;
};

export interface EnrollmentResponse {
  userId: number;
  activityId: number;
  enrollmentDate: string; // LocalDate
  activity: ActivityResponse;
}

export type EnrollmentRequest = {
  userId: number;
  activityId: number;
  enrollmentDate: string;
};

// --- SECURITY / RBAC TYPES ---

export interface PolicyResponse {
  id: number;
  name: string;
  description?: string;
  resource: string;
  action: string;
}

export type PolicyRequest = {
  name: string;
  description?: string;
  resource: string;
  action: string;
};

export interface RoleResponse {
  id: number;
  name: string;
  policyIds: number[];
}

export type RoleRequest = {
  name: string;
  policyIds: number[];
};

export interface UserRoleResponse {
  userId: number;
  roleId: number;
}

export type UserRoleRequest = {
  userId: number;
  roleId: number;
};

export interface RolePolicyResponse {
  roleId: number;
  policyId: number;
}

export type RolePolicyRequest = {
  roleId: number;
  policyId: number;
};

// --- REAL-TIME / STOMP TYPES ---

export interface MessageResponse {
  id: number;
  senderId: number;
  senderName?: string;
  receiverId: number;
  receiverName?: string;
  content: string;
  sentAt: string; // LocalDateTime
}

// El remitente es el usuario de la sesion; el backend lo toma de la cookie.
export type MessageRequest = {
  receiverIds: number[];
  content: string;
};

export interface ContactResponse {
  idUser: number;
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  relation: 'TRAINEE' | 'TRAINER';
}

export type NotificationType = 'NEW_ACTIVITY' | 'NEW_SPACE' | 'NEW_MESSAGE' | 'NEW_RECOMMENDATION';
export type NotificationReferenceType = 'ACTIVITY' | 'SPACE' | 'MESSAGE' | 'RECOMMENDATION';

export interface NotificationResponse {
  idNotification: number;
  userTargetId: number;
  userSourceId: number | null;
  userSourceName: string;
  type: NotificationType;
  message: string;
  referenceId: number;
  referenceType: NotificationReferenceType;
  dateSent: string; // LocalDateTime
  isRead: boolean;
}

// --- ERROR HANDLING ---

export interface ErrorResponse {
  message: string;
  details?: string;
  status: number;
}