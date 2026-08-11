(function () {
  const style = document.createElement('style');

  style.textContent = `
    .renderedMarkdown a[href="/"] {
      font-weight: 700;
      text-decoration: none;
    }

    .renderedMarkdown a[href="/"]:hover {
      text-decoration: underline;
    }
  `;

  document.head.appendChild(style);
})();
