(function () {
  const statusBlock = document.getElementById('service-status');
  const statusText = document.getElementById('service-status-text');
  const refreshButton = document.getElementById('refresh-status');

  if (!statusBlock || !statusText || !refreshButton) {
    return;
  }

  const setStatus = function (state, label) {
    statusBlock.classList.remove(
      'status-block--healthy',
      'status-block--degraded',
      'status-block--unavailable'
    );
    statusBlock.classList.add('status-block--' + state);
    statusText.textContent = label;
  };

  const getJson = function (url) {
    return fetch(url, {
      method: 'GET',
      headers: { Accept: 'application/json' },
      cache: 'no-store'
    }).then(function (response) {
      return response.json();
    });
  };

  const checkStatus = function () {
    refreshButton.disabled = true;
    setStatus('unavailable', 'Checking...');

    Promise.all([getJson('/actuator/health'), getJson('/actuator/health/db')])
      .then(function (results) {
        const applicationHealth = results[0];
        const databaseHealth = results[1];

        if (applicationHealth.status === 'UP' && databaseHealth.status === 'UP') {
          setStatus('healthy', 'API and database operational');
          return;
        }

        if (databaseHealth.status !== 'UP') {
          setStatus('degraded', 'API available, database unavailable');
          return;
        }

        setStatus('degraded', 'Service status: ' + applicationHealth.status);
      })
      .catch(function () {
        setStatus('unavailable', 'Unavailable');
      })
      .finally(function () {
        refreshButton.disabled = false;
      });
  };

  refreshButton.addEventListener('click', checkStatus);
  checkStatus();
})();
