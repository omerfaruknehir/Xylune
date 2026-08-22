(() => {
  const locale = window.TurpLocale || {};
  const ui = locale.ui || {};

  function text(selector, value) {
    if (!value) return;
    document.querySelectorAll(selector).forEach((node) => {
      if (node.textContent !== value) node.textContent = value;
    });
  }

  function aria(selector, name, value) {
    if (!value) return;
    document.querySelectorAll(selector).forEach((node) => {
      if (node.getAttribute(name) !== value) node.setAttribute(name, value);
    });
  }

  function localizeAppearance() {
    text('.appearance-launcher__label', ui.theme);
    aria('[data-theme-settings]', 'aria-label', ui.open_appearance);
    aria('[data-theme-close]', 'aria-label', ui.close_appearance);
    text('.dialog-heading h2', ui.appearance);
    text('.dialog-heading p', ui.customize_site);
    text('#theme-section-title', ui.theme);
    text('#scheme-section-title', ui.color_scheme);
    text('#icon-section-title', ui.brand_icon);

    const themeLabels = {
      app: ui.app,
      system: ui.auto,
      light: ui.light,
      dark: ui.dark,
    };
    Object.entries(themeLabels).forEach(([key, value]) => {
      text(`[data-theme-choice="${key}"] .theme-selector__label`, value);
    });

    const schemeLabels = {
      app: ui.app,
      turp: 'Turp',
      arbor: 'Arbor',
      graphite: ui.graphite,
      ocean: ui.ocean,
      violet: ui.violet,
      sunset: ui.sunset,
    };
    Object.entries(schemeLabels).forEach(([key, value]) => {
      text(`[data-scheme-choice="${key}"] .palette-choice__label`, value);
    });

    text('.appearance-switch-row__copy strong', ui.dynamic_icon);
    text('.appearance-switch-row__copy small', ui.dynamic_icon_description);
    aria('[data-dynamic-icon-toggle]', 'aria-label', ui.use_dynamic_icon);
  }

  function setupLanguagePickers() {
    const pickers = [...document.querySelectorAll('.language-picker')];
    document.addEventListener('click', (event) => {
      pickers.forEach((picker) => {
        if (picker.open && !picker.contains(event.target)) picker.open = false;
      });
    });
    document.addEventListener('keydown', (event) => {
      if (event.key !== 'Escape') return;
      pickers.forEach((picker) => { picker.open = false; });
    });
  }

  localizeAppearance();
  setupLanguagePickers();
})();
