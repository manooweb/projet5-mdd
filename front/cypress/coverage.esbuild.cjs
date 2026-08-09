const path = require('node:path');

module.exports = (builderOptions) => {
  const sourceDirectory = `${path.resolve('src')}${path.sep}`;

  builderOptions.instrumentForCoverage = (filename) =>
    filename.startsWith(sourceDirectory) && !filename.endsWith('.spec.ts');

  return {
    name: 'enable-istanbul-coverage',
    setup() {},
  };
};
