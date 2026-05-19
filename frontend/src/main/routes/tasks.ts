import axios from 'axios';
import { Application, Request, Response } from 'express';

import {
  createTask,
  deleteTask,
  formatDateTime,
  formatStatusLabel,
  getAllTasks,
  getTask,
  updateTaskStatus,
} from '../services/taskApi';
import { CreateTaskPayload, TaskStatus } from '../types/task';

const TASK_STATUSES: TaskStatus[] = ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];

const buildStatusItems = (selected?: TaskStatus) =>
  TASK_STATUSES.map(status => ({
    value: status,
    text: formatStatusLabel(status),
    selected: status === selected,
  }));

const buildCreateStatusItems = (selected: TaskStatus = 'PENDING') => buildStatusItems(selected);

const getMinDueDateTime = (): string => {
  const min = new Date();
  min.setMinutes(min.getMinutes() + 1);
  const pad = (value: number): string => String(value).padStart(2, '0');
  return `${min.getFullYear()}-${pad(min.getMonth() + 1)}-${pad(min.getDate())}T${pad(min.getHours())}:${pad(min.getMinutes())}`;
};

const renderCreateForm = (
  res: Response,
  options: { formError: string | null; values: Record<string, unknown> }
): void => {
  const status = (options.values.status as TaskStatus) || 'PENDING';
  res.render('tasks/create', {
    statusItems: buildCreateStatusItems(status),
    minDueDateTime: getMinDueDateTime(),
    formError: options.formError,
    values: options.values,
  });
};

const parseApiError = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    if (error.code === 'ECONNREFUSED' || error.code === 'ENOTFOUND') {
      return 'Cannot reach the backend API. Start it with: cd backend && ./gradlew bootRun (port 4000).';
    }

    const details = error.response?.data?.details;
    if (Array.isArray(details) && details.length > 0) {
      return details.join(', ');
    }

    const message = error.response?.data?.message;
    if (typeof message === 'string' && message.length > 0) {
      return message;
    }

    if (error.message) {
      return error.message;
    }

    return `Request failed (${error.code ?? 'unknown error'}).`;
  }
  return 'An unexpected error occurred. Please try again.';
};

export default function (app: Application): void {
  app.get('/', async (req: Request, res: Response) => {
    try {
      const tasks = await getAllTasks();
      res.render('tasks/list', {
        tasks,
        formatDateTime,
        formatStatusLabel,
        error: null,
        deleted: req.query.deleted === 'true',
      });
    } catch (error) {
      res.render('tasks/list', {
        tasks: [],
        formatDateTime,
        formatStatusLabel,
        error: parseApiError(error),
        deleted: false,
      });
    }
  });

  app.get('/tasks/new', (req: Request, res: Response) => {
    const apiError = typeof req.query.error === 'string' ? req.query.error : null;
    renderCreateForm(res, {
      formError: apiError,
      values: { status: 'PENDING' },
    });
  });

  const handleCreateTask = async (req: Request, res: Response): Promise<void> => {
    const { title, description, status, dueDateTime } = req.body;
    const values = { title, description, status, dueDateTime };

    if (!title || !status || !dueDateTime) {
      res.status(400);
      renderCreateForm(res, {
        formError: 'Title, status and due date/time are required.',
        values,
      });
      return;
    }

    const payload: CreateTaskPayload = {
      title: String(title).trim(),
      description: description ? String(description).trim() : undefined,
      status: status as TaskStatus,
      dueDateTime: String(dueDateTime).length === 16 ? `${String(dueDateTime)}:00` : String(dueDateTime),
    };

    try {
      const task = await createTask(payload);
      res.redirect(`/tasks/${task.id}`);
    } catch (error) {
      console.error('Create task failed:', error);
      res.status(400);
      renderCreateForm(res, { formError: parseApiError(error), values });
    }
  };

  app.post('/tasks', handleCreateTask);
  app.post('/tasks/new', handleCreateTask);

  app.get('/tasks/:id', async (req: Request, res: Response) => {
    const id = Number(req.params.id);
    try {
      const task = await getTask(id);
      res.render('tasks/view', {
        task,
        statusItems: buildStatusItems(task.status),
        formatDateTime,
        formatStatusLabel,
        error: null,
        success: null,
      });
    } catch (error) {
      res.status(404).render('not-found');
    }
  });

  app.post('/tasks/:id/status', async (req: Request, res: Response) => {
    const id = Number(req.params.id);
    const { status } = req.body;

    try {
      const task = await updateTaskStatus(id, status as TaskStatus);
      res.render('tasks/view', {
        task,
        statusItems: buildStatusItems(task.status),
        formatDateTime,
        formatStatusLabel,
        error: null,
        success: 'Task status updated successfully.',
      });
    } catch (error) {
      const task = await getTask(id);
      res.status(400).render('tasks/view', {
        task,
        statusItems: buildStatusItems(task.status),
        formatDateTime,
        formatStatusLabel,
        error: parseApiError(error),
        success: null,
      });
    }
  });

  app.post('/tasks/:id/delete', async (req: Request, res: Response) => {
    const id = Number(req.params.id);
    try {
      await deleteTask(id);
      res.redirect('/?deleted=true');
    } catch (error) {
      try {
        const task = await getTask(id);
        res.status(400).render('tasks/view', {
          task,
          statusItems: buildStatusItems(task.status),
          formatDateTime,
          formatStatusLabel,
          error: parseApiError(error),
          success: null,
        });
      } catch {
        res.status(404).render('not-found');
      }
    }
  });
}
