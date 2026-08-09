import { defineConfig } from 'cypress';
import registerCodeCoverageTasks from '@cypress/code-coverage/task';

export default defineConfig({
  allowCypressEnv: false,
  expose: {
    codeCoverage: {
      exclude: ['src/main.ts'],
    },
  },
  e2e: {
    baseUrl: 'http://127.0.0.1:4200',
    setupNodeEvents(on, config) {
      registerCodeCoverageTasks(on, config);
      return config;
    },
  },
});
