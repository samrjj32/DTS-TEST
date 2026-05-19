import axios, { AxiosInstance } from 'axios';

import { getApiUrl } from '../appConfig';
import { CreateTaskPayload, Task, TaskStatus } from '../types/task';

const apiUrl = getApiUrl();

const client: AxiosInstance = axios.create({
  baseURL: apiUrl,
  headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
  timeout: 10000,
});

export const getAllTasks = async (): Promise<Task[]> => {
  const { data } = await client.get<Task[]>('/api/tasks');
  return data;
};

export const getTask = async (id: number): Promise<Task> => {
  const { data } = await client.get<Task>(`/api/tasks/${id}`);
  return data;
};

export const createTask = async (payload: CreateTaskPayload): Promise<Task> => {
  const { data } = await client.post<Task>('/api/tasks', payload);
  return data;
};

export const updateTaskStatus = async (id: number, status: TaskStatus): Promise<Task> => {
  const { data } = await client.patch<Task>(`/api/tasks/${id}/status`, { status });
  return data;
};

export const deleteTask = async (id: number): Promise<void> => {
  await client.delete(`/api/tasks/${id}`);
};

export const formatStatusLabel = (status: TaskStatus): string =>
  status
    .split('_')
    .map(word => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ');

export const formatDateTime = (isoDate: string): string =>
  new Date(isoDate).toLocaleString('en-GB', {
    dateStyle: 'medium',
    timeStyle: 'short',
  });
