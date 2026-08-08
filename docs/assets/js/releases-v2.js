(() => {
  const container = document.querySelector('[data-release-list-v2]');
  if (!container) return;

  const repository = container.dataset.repository || 'omerfaruknehir/Xylune';
  const maxReleases = 10;
  const endpoint = `https://api.github.com/repos/${repository}/releases?per_page=${maxReleases}`;
  const locale = window.XyluneLocale || {};
  const strings = locale.release || {};
  const pageLanguage = document.documentElement.lang || 'en';
  const releaseLanguage = pageLanguage.toLowerCase().startsWith('tr') ? 'tr' : 'en';

  const labels = {
    latest: strings.latest || 'Latest',
    preRelease: strings.pre_release || 'Pre-release',
    downloadApk: strings.download_apk || 'Download APK',
    openGitHub: strings.open_github || 'Open on GitHub',
    showAll: strings.show_all || 'Show all releases',
    noNotes: strings.no_notes || 'No release notes were provided for this build.',
    loadFailed: strings.load_failed || 'The live release list could not be loaded.',
    openReleases: strings.open_releases || 'Open releases on GitHub',
  };

  function localizedReleaseMarkdown(value) {
    const text = String(value || '').replace(/\r\n?/g, '\n');
    const englishMarker = '<!-- xylune-release-notes:en -->';
    const turkishMarker = '<!-- xylune-release-notes:tr -->';
    const englishIndex = text.indexOf(englishMarker);
    const turkishIndex = text.indexOf(turkishMarker);

    if (englishIndex === -1 || turkishIndex === -1 || turkishIndex <= englishIndex) {
      return text;
    }

    let section;
    if (releaseLanguage === 'tr') {
      section = text.slice(turkishIndex + turkishMarker.length);
      section = section.replace(/^\s*##\s+Türkçe\s*\n+/i, '');
    } else {
      section = text.slice(englishIndex + englishMarker.length, turkishIndex);
      section = section.replace(/^\s*##\s+English\s*\n+/i, '');
    }
    return section.trim();
  }

  function parseSemanticVersion(value) {
    const match = String(value || '').trim().match(/^v?(\d+)\.(\d+)\.(\d+)(?:[-+]([^+]+))?$/i);
    if (!match) return null;
    return {
      numbers: [Number(match[1]), Number(match[2]), Number(match[3])],
      suffix: match[4] || null,
    };
  }

  function compareSemanticVersionsDescending(leftRelease, rightRelease) {
    const left = parseSemanticVersion(leftRelease.tag_name || leftRelease.name);
    const right = parseSemanticVersion(rightRelease.tag_name || rightRelease.name);
    if (left && right) {
      for (let index = 0; index < 3; index += 1) {
        const difference = right.numbers[index] - left.numbers[index];
        if (difference !== 0) return difference;
      }
      if (left.suffix === null && right.suffix !== null) return -1;
      if (left.suffix !== null && right.suffix === null) return 1;
      if (left.suffix !== right.suffix) return String(right.suffix).localeCompare(String(left.suffix));
    } else if (left) {
      return -1;
    } else if (right) {
      return 1;
    }

    const leftTime = Date.parse(leftRelease.published_at || leftRelease.created_at || '') || 0;
    const rightTime = Date.parse(rightRelease.published_at || rightRelease.created_at || '') || 0;
    return rightTime - leftTime;
  }

  function normalizeReleases(value) {
    return (Array.isArray(value) ? value : [])
      .filter((release) => release && !release.draft)
      .sort(compareSemanticVersionsDescending)
      .slice(0, maxReleases);
  }

  function releaseVersion(release) {
    return String(release.tag_name || release.name || 'Release').replace(/^v/i, '');
  }

  function releaseDate(release) {
    const value = release.published_at || release.created_at;
    if (!value) return '';
    try {
      return new Intl.DateTimeFormat(pageLanguage, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      }).format(new Date(value));
    } catch (_) {
      return new Intl.DateTimeFormat(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      }).format(new Date(value));
    }
  }

  function icon(name, className = '') {
    const element = document.createElement('span');
    element.className = `material-symbols-rounded ${className}`.trim();
    element.setAttribute('aria-hidden', 'true');
    element.textContent = name;
    return element;
  }

  function actionLink(label, href, { primary = false, leadingIcon = null, external = false } = {}) {
    const link = document.createElement('a');
    link.className = `button ${primary ? 'button-primary' : 'button-text'}`;
    link.href = href;
    if (external) {
      link.target = '_blank';
      link.rel = 'noopener noreferrer';
    }
    if (leadingIcon) link.append(icon(leadingIcon));
    const text = document.createElement('span');
    text.textContent = label;
    link.append(text);
    if (external) link.append(icon('open_in_new', 'external-link-icon'));
    return link;
  }

  function appendInlineMarkup(parent, value) {
    const text = String(value || '');
    const pattern = /(`[^`]+`|\*\*[^*]+\*\*|\[([^\]]+)]\((https?:\/\/[^)]+)\))/g;
    let cursor = 0;

    for (const match of text.matchAll(pattern)) {
      if (match.index > cursor) parent.append(document.createTextNode(text.slice(cursor, match.index)));
      const token = match[0];
      if (token.startsWith('`')) {
        const code = document.createElement('code');
        code.textContent = token.slice(1, -1);
        parent.append(code);
      } else if (token.startsWith('**')) {
        const strong = document.createElement('strong');
        strong.textContent = token.slice(2, -2);
        parent.append(strong);
      } else {
        const link = document.createElement('a');
        link.href = match[3];
        link.target = '_blank';
        link.rel = 'noopener noreferrer';
        link.append(document.createTextNode(match[2]), icon('open_in_new', 'external-link-icon'));
        parent.append(link);
      }
      cursor = match.index + token.length;
    }

    if (cursor < text.length) parent.append(document.createTextNode(text.slice(cursor)));
  }

  function renderReleaseNotes(markdown) {
    const root = document.createElement('div');
    root.className = 'release-notes';
    const lines = String(markdown || '').replace(/\r\n?/g, '\n').split('\n');
    let paragraph = [];
    let list = null;
    let listType = '';
    let codeLines = null;

    const flushParagraph = () => {
      if (paragraph.length === 0) return;
      const node = document.createElement('p');
      appendInlineMarkup(node, paragraph.join(' '));
      root.append(node);
      paragraph = [];
    };

    const endList = () => {
      list = null;
      listType = '';
    };

    for (const line of lines) {
      if (/^```/.test(line.trim())) {
        flushParagraph();
        endList();
        if (codeLines === null) {
          codeLines = [];
        } else {
          const pre = document.createElement('pre');
          const code = document.createElement('code');
          code.textContent = codeLines.join('\n');
          pre.append(code);
          root.append(pre);
          codeLines = null;
        }
        continue;
      }

      if (codeLines !== null) {
        codeLines.push(line);
        continue;
      }

      if (line.trim() === '') {
        flushParagraph();
        endList();
        continue;
      }

      const heading = line.match(/^(#{1,6})\s+(.+)$/);
      if (heading) {
        flushParagraph();
        endList();
        const node = document.createElement(heading[1].length <= 2 ? 'h3' : 'h4');
        appendInlineMarkup(node, heading[2]);
        root.append(node);
        continue;
      }

      const unordered = line.match(/^\s*[-*+]\s+(.+)$/);
      const ordered = line.match(/^\s*\d+[.)]\s+(.+)$/);
      const item = unordered || ordered;
      if (item) {
        flushParagraph();
        const nextType = unordered ? 'ul' : 'ol';
        if (!list || listType !== nextType) {
          list = document.createElement(nextType);
          listType = nextType;
          root.append(list);
        }
        const node = document.createElement('li');
        appendInlineMarkup(node, item[1]);
        list.append(node);
        continue;
      }

      endList();
      paragraph.push(line.trim());
    }

    if (codeLines !== null && codeLines.length > 0) {
      const pre = document.createElement('pre');
      const code = document.createElement('code');
      code.textContent = codeLines.join('\n');
      pre.append(code);
      root.append(pre);
    }

    flushParagraph();
    if (!root.hasChildNodes()) {
      const fallback = document.createElement('p');
      fallback.textContent = labels.noNotes;
      root.append(fallback);
    }
    return root;
  }

  function renderRelease(release, index) {
    const card = document.createElement('details');
    card.className = 'release-card';
    card.open = index === 0;

    const toggle = document.createElement('summary');
    toggle.className = 'release-card__toggle';

    const heading = document.createElement('div');
    heading.className = 'release-card__heading';
    const titleRow = document.createElement('div');
    titleRow.className = 'release-card__title-row';
    const title = document.createElement('h2');
    title.textContent = `Xylune ${releaseVersion(release)}`;
    titleRow.append(title);

    if (index === 0) {
      const badge = document.createElement('span');
      badge.className = 'release-badge';
      badge.textContent = labels.latest;
      titleRow.append(badge);
    }

    const meta = document.createElement('p');
    meta.className = 'release-card__meta';
    meta.textContent = [releaseDate(release), release.prerelease ? labels.preRelease : null]
      .filter(Boolean)
      .join(' · ');

    heading.append(titleRow, meta);
    toggle.append(heading, icon('expand_more', 'release-card__chevron'));

    const body = document.createElement('div');
    body.className = 'release-card__body';
    body.append(renderReleaseNotes(localizedReleaseMarkdown(release.body)));

    const actions = document.createElement('div');
    actions.className = 'release-card__actions';
    const assets = Array.isArray(release.assets) ? release.assets : [];
    const apk = assets.find((asset) => /-release\.apk$/i.test(asset.name))
      || assets.find((asset) => /\.apk$/i.test(asset.name));

    if (apk?.browser_download_url) {
      actions.append(actionLink(labels.downloadApk, apk.browser_download_url, {
        primary: true,
        leadingIcon: 'download',
      }));
    }
    if (release.html_url) {
      actions.append(actionLink(labels.openGitHub, release.html_url, { external: true }));
    }
    if (actions.hasChildNodes()) body.append(actions);

    const content = document.createElement('div');
    content.className = 'release-card__content';
    content.append(body);
    card.append(toggle, content);
    return card;
  }

  function renderReleaseList(releases) {
    const normalized = normalizeReleases(releases);
    if (normalized.length === 0) throw new Error('No published releases were returned');

    const fragment = document.createDocumentFragment();
    normalized.forEach((release, index) => fragment.append(renderRelease(release, index)));

    const footer = document.createElement('div');
    footer.className = 'release-list__footer';
    footer.append(actionLink(
      labels.showAll,
      `https://github.com/${repository}/releases`,
      { external: true },
    ));
    fragment.append(footer);

    container.replaceChildren(fragment);
    container.removeAttribute('aria-busy');
  }

  function renderFailure() {
    const fallback = document.createElement('p');
    fallback.className = 'release-status';
    fallback.append(`${labels.loadFailed} `);
    fallback.append(actionLink(
      labels.openReleases,
      `https://github.com/${repository}/releases`,
      { external: true },
    ));
    container.replaceChildren(fallback);
    container.removeAttribute('aria-busy');
  }

  async function load() {
    try {
      const response = await fetch(endpoint, {
        mode: 'cors',
        credentials: 'omit',
        cache: 'default',
      });
      if (!response.ok) throw new Error(`GitHub returned HTTP ${response.status}`);
      renderReleaseList(await response.json());
    } catch (_) {
      renderFailure();
    }
  }

  load();

  window.XyluneReleaseSort = {
    parseSemanticVersion,
    compareSemanticVersionsDescending,
  };
})();