export type TaskStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  dueDateTime: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CreateTaskPayload {
  title: string;
  description?: string;
  status: TaskStatus;
  dueDateTime: string;
}
