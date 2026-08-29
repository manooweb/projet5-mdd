import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    coverage: {
      reportsDirectory: '../docs/reports/coverage/front-vitest-integration',
    },
  },
});
