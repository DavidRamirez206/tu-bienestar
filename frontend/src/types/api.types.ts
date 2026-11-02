// --- AUTH TYPES ---
export type AuthRequest = {
  username: string;
  password: string;
};

export interface AuthResponse {
  token: string;
  user: UserResponse;
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
  startDate: string; // LocalDate
  endDate: string; // LocalDate
  spaceId?: number;
}

export type ActivityRequest = {
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  spaceId?: number;
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
  userId?: number;
  routineExercises?: RoutineExerciseResponse[];
}

export type RoutineRequest = {
  name: string;
  description?: string;
  creationDate: string;
  isPredesigned: boolean;
  userId?: number;
};

export interface RoutineExerciseResponse {
  routineId: number;
  exerciseId: number;
  sets: number;
  targetReps: number;
  exerciseOrder: number;
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

export interface ScheduleResponse {
  idSchedule: number;
  activityId: number;
  dayOfWeek: string;
  startTime: string; // LocalTime
  endTime: string; // LocalTime
}

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
  trainerId: number;
  trainerFirstName?: string;
  trainerLastName?: string;
  assignmentDate: string; // LocalDate
}

export type AssignmentRequest = {
  userId: number;
  trainerId: number;
  assignmentDate: string;
};

export interface EnrollmentResponse {
  userId: number;
  activityId: number;
  activityName?: string;
  enrollmentDate: string; // LocalDate
  activityEndDate?: string; // LocalDate
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
  receiverId?: number;
  receiverName?: string;
  content: string;
  sentAt: string; // LocalDateTime
}

export type MessageRequest = {
  senderId: number;
  receiverId: number | null;
  content: string;
  sentAt: string;
};

export interface NotificationResponse {
  idNotification: number;
  userTargetId: number;
  userSourceId: number;
  type: string;
  message: string;
  referenceId: number;
  referenceType: string;
  dateSent: string; // LocalDateTime
  isRead: boolean;
}

export type NotificationRequest = {
  userTargetId: number;
  userSourceId: number;
  type: string;
  message: string;
  referenceId: number;
  referenceType: string;
  dateSent: string;
  isRead: boolean;
};

// --- ERROR HANDLING ---

export interface ErrorResponse {
  message: string;
  details?: string;
  status: number;
}