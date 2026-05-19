# HMCTS Dev Test Frontend — Task Management UI

Express application with Nunjucks templates and the GOV.UK Design System.

## Prerequisites

- **Node.js 20 LTS** recommended (see `.nvmrc`). Node 18–22 also work.
- Yarn 3

## Run locally

Ensure the [backend API](../backend/README.md) is running on port 4000, then:

```bash
yarn install
yarn webpack
yarn start:dev
```

Application: **https://localhost:3100**

### `Cannot find module '.pnp.cjs'`

Your shell still has a stale Yarn PnP preload. Clear it, then restart:

```bash
unset NODE_OPTIONS
yarn start:dev
```

The `start:dev` script also clears `NODE_OPTIONS` automatically.

### `Utils.isRegExp is not a function` (config package)

Fixed in this repo by replacing the `config` npm package with a small local loader (`src/main/appConfig.ts`). Pull latest changes and run `yarn start:dev` again.

## Pages

| Route | Description |
|-------|-------------|
| `/` | Task list |
| `/tasks/new` | Create a new task |
| `/tasks/:id` | View task, update status, delete |

## Configuration

API base URL in `config/default.json`:

```json
{
  "api": {
    "url": "http://localhost:4000"
  }
}
```

## Tests

```bash
yarn test:unit    # API client unit tests
yarn test:routes  # Express route tests
yarn test         # unit tests (CI skips via if-env)
```

## Tech stack

- Node.js 20 LTS (18–22 supported; avoid Node 25 with old PnP setups)
- Express, Nunjucks
- GOV.UK Frontend 4.8
- Axios
- Jest, Supertest, Nock
