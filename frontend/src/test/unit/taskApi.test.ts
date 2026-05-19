import nock from 'nock';

import { getApiUrl } from '../../main/appConfig';
import {
  createTask,
  deleteTask,
  formatStatusLabel,
  getAllTasks,
  getTask,
  updateTaskStatus,
} from '../../main/services/taskApi';

const apiUrl = getApiUrl();

describe('taskApi', () => {
  afterEach(() => {
    nock.cleanAll();
  });

  test('getAllTasks returns tasks from API', async () => {
    const tasks = [
      {
        id: 1,
        title: 'Review documents',
        status: 'PENDING',
        dueDateTime: '2026-06-01T14:00:00',
        createdAt: '2026-05-01T10:00:00',
      },
    ];

    nock(apiUrl).get('/api/tasks').reply(200, tasks);

    await expect(getAllTasks()).resolves.toEqual(tasks);
  });

  test('getTask returns a single task', async () => {
    const task = {
      id: 2,
      title: 'Hearing prep',
      status: 'IN_PROGRESS',
      dueDateTime: '2026-06-02T09:00:00',
      createdAt: '2026-05-02T10:00:00',
    };

    nock(apiUrl).get('/api/tasks/2').reply(200, task);

    await expect(getTask(2)).resolves.toEqual(task);
  });

  test('createTask posts payload to API', async () => {
    const payload = {
      title: 'New task',
      status: 'PENDING' as const,
      dueDateTime: '2026-07-01T12:00:00',
    };
    const created = { id: 3, ...payload, createdAt: '2026-05-03T10:00:00' };

    nock(apiUrl).post('/api/tasks', payload).reply(201, created);

    await expect(createTask(payload)).resolves.toEqual(created);
  });

  test('updateTaskStatus patches status', async () => {
    const updated = {
      id: 1,
      title: 'Review documents',
      status: 'COMPLETED',
      dueDateTime: '2026-06-01T14:00:00',
      createdAt: '2026-05-01T10:00:00',
    };

    nock(apiUrl).patch('/api/tasks/1/status', { status: 'COMPLETED' }).reply(200, updated);

    await expect(updateTaskStatus(1, 'COMPLETED')).resolves.toEqual(updated);
  });

  test('deleteTask calls delete endpoint', async () => {
    nock(apiUrl).delete('/api/tasks/1').reply(204);

    await expect(deleteTask(1)).resolves.toBeUndefined();
  });

  test('formatStatusLabel formats enum values', () => {
    expect(formatStatusLabel('IN_PROGRESS')).toBe('In Progress');
  });
});
