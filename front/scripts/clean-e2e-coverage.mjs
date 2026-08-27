import { rm } from 'node:fs/promises';
import path from 'node:path';

await rm(path.resolve('.nyc_output'), { recursive: true, force: true });
