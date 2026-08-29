import { spawn } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const projectDirectory = fileURLToPath(new URL('../', import.meta.url));
process.loadEnvFile(new URL('../../.env', import.meta.url));

if (!process.env.SONAR_TOKEN) {
  throw new Error('SONAR_TOKEN is required in the repository root .env file.');
}

const child = spawn('npx', ['--no-install', 'sonar-scanner-npm'], {
  cwd: projectDirectory,
  env: process.env,
  stdio: 'inherit',
});

child.on('exit', (code) => process.exitCode = code ?? 1);
