import { readdir, rm, mkdir, writeFile } from 'node:fs/promises';
import { spawn } from 'node:child_process';
import path from 'node:path';

const integrationTestFiles = await findIntegrationTestFiles('src');

if (integrationTestFiles.length > 0) {
  const child = spawn('npx', ['ng', 'test', '--configuration', 'integration', '--watch=false'], {
    stdio: 'inherit',
  });

  child.once('exit', (code) => {
    process.exitCode = code ?? 1;
  });
} else {
  const reportDirectory = path.resolve('../docs/reports/coverage/front-vitest-integration');
  await rm(reportDirectory, { recursive: true, force: true });
  await mkdir(reportDirectory, { recursive: true });
  await Promise.all([
    writeFile(path.join(reportDirectory, 'coverage-final.json'), '{}\n'),
    writeFile(path.join(reportDirectory, 'test-results.json'), '{"numTotalTests":0}\n'),
    writeFile(
      path.join(reportDirectory, 'index.html'),
      '<!doctype html><html lang="en"><head><meta charset="utf-8"><title>Integration test coverage</title></head><body><h1>Integration test coverage</h1><p>No integration tests have been added yet.</p></body></html>\n',
    ),
  ]);

  console.log('No integration tests found. Generated an empty integration coverage report.');
}

async function findIntegrationTestFiles(directory) {
  const entries = await readdir(directory, { recursive: true });
  return entries.filter((entry) => entry.endsWith('.integration.spec.ts'));
}
