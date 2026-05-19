import * as fs from 'fs';
import * as path from 'path';

interface AppConfig {
  api: {
    url: string;
  };
}

const configDir = path.join(__dirname, '..', '..', 'config');

const readConfigFile = (name: string): Partial<AppConfig> => {
  const filePath = path.join(configDir, `${name}.json`);
  if (!fs.existsSync(filePath)) {
    return {};
  }
  return JSON.parse(fs.readFileSync(filePath, 'utf8')) as Partial<AppConfig>;
};

const mergeConfig = (base: Partial<AppConfig>, override: Partial<AppConfig>): AppConfig => ({
  api: {
    url: override.api?.url ?? base.api?.url ?? 'http://localhost:4000',
  },
});

const env = process.env.NODE_ENV || 'development';
const appConfig = mergeConfig(readConfigFile('default'), readConfigFile(env));

export const getApiUrl = (): string => process.env.API_URL ?? appConfig.api.url;
