import { spawn } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const projectDirectory = fileURLToPath(new URL('../', import.meta.url));
process.loadEnvFile(new URL('../../.env', import.meta.url));

if (!process.env.SONAR_TOKEN) {
  throw new Error('SONAR_TOKEN is required in the repository root .env file.');
}

const mavenWrapper = new URL('../mvnw', import.meta.url);
const sonarGoal = 'org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar';
const child = spawn(fileURLToPath(mavenWrapper), ['clean', 'verify', sonarGoal], {
  cwd: projectDirectory,
  env: process.env,
  stdio: 'inherit',
});

child.on('exit', (code) => process.exitCode = code ?? 1);
