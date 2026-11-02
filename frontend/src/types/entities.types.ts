export interface User {
  idUser: number;
  firstName: string;
  lastName: string;
  institutionalEmail: string;
  age: number;
  userRoles: UserRole[];
  progresses?: Progress[];
  routines?: Routine[];
  assignments?: Assignment[];
  trainerAssignments?: Assignment[];
  recommendations?: Recommendation[];
  trainerRecommendations?: Recommendation[];
  sentMessages?: Message[];
  receivedMessages?: Message[];
  targetNotifications?: Notification[];
  sourceNotifications?: Notification[];
  userExercises?: Exercise[];
}

export interface Activity {
  idActivity: number;
  space?: Space;
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;
}

export interface Space {
  idSpace: number;
  name: string;
  capacity?: number;
  location?: string;
}

export interface Exercise {
  idExercise: number;
  name: string;
  type: string;
  description?: string;
  durationMin?: number;
  difficulty?: string;
  videoUrl?: string;
  userExercise?: User;
  isPredefined: boolean;
}

export interface Routine {
  idRoutine: number;
  userRoutine: User;
  name: string;
  description?: string;
  creationDate: string;
  isPredesigned: boolean;
  routineExercises: RoutineExercise[];
}

export interface Role {
  id: number;
  name: string;
  userRoles: UserRole[];
  rolePolicies: RolePolicy[];
}

export interface Policy {
  id: number;
  name: string;
  description?: string;
  resource: string;
  action: string;
  rolePolicies: RolePolicy[];
}

export interface Progress {
  idProgress: number;
  userProgress: User;
  exercise: Exercise;
  routine?: Routine;
  dateLogged: string;
  reps?: number;
  durationMin?: number;
  effortLevel?: number;
  setNumber?: number;
  weightKg?: number;
}

export interface Assignment {
  userAssignment: User;
  trainerAssignment: User;
  assignmentDate: string;
}

export interface Enrollment {
  user: User;
  activity: Activity;
  enrollmentDate: string;
}

export interface Recommendation {
  idRecommendation: number;
  trainer: User;
  user: User;
  description: string;
  dateCreated: string;
}

export interface Schedule {
  idSchedule: number;
  activity: Activity;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
}

export interface Message {
  id: number;
  sender: User;
  receiver: User;
  content: string;
  sentAt: string;
}

export interface Notification {
  idNotification: number;
  userTarget: User;
  userSource?: User;
  type: string;
  message: string;
  referenceId: number;
  referenceType: string;
  dateSent: string;
  isRead: boolean;
}

export interface UserRole {
  user: User;
  role: Role;
}

export interface RolePolicy {
  role: Role;
  policy: Policy;
}

export interface RoutineExercise {
  routine: Routine;
  exercise: Exercise;
  sets?: number;
  targetReps?: number;
  exerciseOrder?: number;
}
