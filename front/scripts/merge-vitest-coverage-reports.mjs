import { readFile, writeFile } from 'node:fs/promises';
import { createRequire } from 'node:module';
import path from 'node:path';

const require = createRequire(import.meta.url);
const { createCoverageMap } = require('istanbul-lib-coverage');
const { createContext } = require('istanbul-lib-report');
const reports = require('istanbul-reports');
const SummarizerFactory = require('istanbul-lib-report/lib/summarizer-factory');

const coverageRoot = path.resolve('../coverage');
const unitReportDirectory = path.join(coverageRoot, 'front-vitest-unit');
const integrationReportDirectory = path.join(coverageRoot, 'front-vitest-integration');
const globalReportDirectory = path.join(coverageRoot, 'front-vitest');
const packageConfiguration = await readJson(path.resolve('package.json'));
const minimumIntegrationRatio = packageConfiguration.testRatio?.minimumIntegration;

if (typeof minimumIntegrationRatio !== 'number') {
  throw new Error('The integration test ratio must be configured in package.json.');
}

const [unitCoverage, integrationCoverage, unitTestCount, integrationTestCount] = await Promise.all([
  readJson(path.join(unitReportDirectory, 'coverage-final.json')),
  readJson(path.join(integrationReportDirectory, 'coverage-final.json')),
  readTestCount(path.join(unitReportDirectory, 'test-results.json')),
  readTestCount(path.join(integrationReportDirectory, 'test-results.json')),
]);

const coverageMap = createCoverageMap(unitCoverage);
coverageMap.merge(integrationCoverage);

const context = createContext({
  dir: globalReportDirectory,
  coverageMap,
});
const tree = new SummarizerFactory(coverageMap).pkg;

for (const reporterName of ['html', 'lcov', 'text-summary']) {
  tree.visit(reports.create(reporterName), context);
}

const totalTestCount = unitTestCount + integrationTestCount;
if (totalTestCount === 0) {
  throw new Error('No unit or integration test result was found.');
}

const ratio = integrationTestCount / totalTestCount;
const targetReached = ratio >= minimumIntegrationRatio;

console.log(
  `Test ratio: ${integrationTestCount} integration / ${totalTestCount} total = ${(ratio * 100).toFixed(2)}%`,
);

await addGlobalReportDetails(
  path.join(globalReportDirectory, 'index.html'),
  integrationTestCount,
  totalTestCount,
  ratio,
  targetReached,
);
await addNavigation(
  path.join(unitReportDirectory, 'index.html'),
  '<a href="../front-vitest/index.html">Back to global coverage</a>',
);
await addNavigation(
  path.join(integrationReportDirectory, 'index.html'),
  '<a href="../front-vitest/index.html">Back to global coverage</a>',
);

async function readJson(filePath) {
  return JSON.parse(await readFile(filePath, 'utf8'));
}

async function readTestCount(filePath) {
  const result = await readJson(filePath);
  if (typeof result.numTotalTests !== 'number') {
    throw new Error(`Unable to read the test count from ${filePath}.`);
  }

  return result.numTotalTests;
}

async function addGlobalReportDetails(
  reportPath,
  integrationCount,
  totalCount,
  integrationRatio,
  targetReached,
) {
  const status = targetReached ? 'TARGET REACHED' : 'BELOW TARGET';
  const color = targetReached ? '#4f8a10' : '#9f6000';
  const ratioBlock = `<!-- test-ratio:start -->\n<div style="margin: 1em 0; padding: 0.8em; border: 1px solid ${color};">\n  <strong>Integration test ratio:</strong>\n  ${integrationCount} / ${totalCount} tests (${(integrationRatio * 100).toFixed(2)}%), minimum ${(minimumIntegrationRatio * 100).toFixed(2)}% -\n  <strong style="color: ${color};">${status}</strong>\n</div>\n<!-- test-ratio:end -->`;

  let report = await readFile(reportPath, 'utf8');
  report = replaceOrInsert(
    report,
    '<!-- test-ratio:start -->',
    '<!-- test-ratio:end -->',
    ratioBlock,
    '</h1>',
  );
  report = replaceOrInsert(
    report,
    '<!-- coverage-navigation:start -->',
    '<!-- coverage-navigation:end -->',
    navigationBlock(
      'Detailed coverage reports: <a href="../front-vitest-unit/index.html">Unit tests</a> | <a href="../front-vitest-integration/index.html">Integration tests</a>',
    ),
    '</table>',
  );

  await writeFile(reportPath, report);
}

async function addNavigation(reportPath, navigation) {
  const report = await readFile(reportPath, 'utf8');
  const updatedReport = replaceOrInsert(
    report,
    '<!-- coverage-navigation:start -->',
    '<!-- coverage-navigation:end -->',
    navigationBlock(navigation),
    '</table>',
  );

  await writeFile(reportPath, updatedReport);
}

function navigationBlock(content) {
  return `<!-- coverage-navigation:start -->\n<div style="margin: 1em 0;">${content}</div>\n<!-- coverage-navigation:end -->`;
}

function replaceOrInsert(report, startMarker, endMarker, block, insertionMarker) {
  const start = report.indexOf(startMarker);
  const end = report.indexOf(endMarker);
  if (start >= 0 && end >= start) {
    return report.slice(0, start) + block + report.slice(end + endMarker.length);
  }

  const insertionPoint = report.indexOf(insertionMarker);
  if (insertionPoint >= 0) {
    const afterMarker = insertionPoint + insertionMarker.length;
    return report.slice(0, afterMarker) + block + report.slice(afterMarker);
  }

  const bodyEnd = report.indexOf('</body>');
  if (bodyEnd >= 0) {
    return report.slice(0, bodyEnd) + block + report.slice(bodyEnd);
  }

  throw new Error(`Unable to locate ${insertionMarker} or </body> in the coverage report.`);
}
