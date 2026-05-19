import { app } from '../../main/app';
import nock from 'nock';
import request from 'supertest';

import { expect } from 'chai';

import { getApiUrl } from '../../main/appConfig';

const apiUrl = getApiUrl();

describe('Tasks routes', () => {
  afterEach(() => {
    nock.cleanAll();
  });

  describe('GET /', () => {
    it('should render task list when API is available', async () => {
      nock(apiUrl)
        .get('/api/tasks')
        .reply(200, [
          {
            id: 1,
            title: 'Review documents',
            status: 'PENDING',
            dueDateTime: '2026-06-01T14:00:00',
            createdAt: '2026-05-01T10:00:00',
          },
        ]);

      const response = await request(app).get('/');

      expect(response.status).to.equal(200);
      expect(response.text).to.include('Review documents');
    });
  });

  describe('GET /tasks/new', () => {
    it('should render create task form', async () => {
      const response = await request(app).get('/tasks/new');

      expect(response.status).to.equal(200);
      expect(response.text).to.include('Create a task');
    });
  });
});
