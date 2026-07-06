(function () {
  'use strict';

  const STORAGE_KEY = 'tiru_chat_messages';
  const FAB_SEEN_KEY = 'tiru_fab_seen';

  const INTENTS = {
    greeting: {
      keywords: ['hi', 'hello', 'hey', 'good morning', 'good afternoon', 'good evening', 'howdy', 'greetings'],
      weight: 1,
    },
    experience: {
      keywords: ['experience', 'years', 'background', 'career', 'work history', 'resume', 'cv', 'timeline', 'ntt', 'itc'],
      weight: 1,
    },
    projects: {
      keywords: [
        'project', 'projects', 'qatar', 'airline', 'airways', 'campbell', 'star', 'cintas', 'dynamics',
        'npci', 'payment', 'case study', 'achievement', 'portfolio', 'app',
      ],
      weight: 1,
    },
    skills: {
      keywords: [
        'skill', 'skills', 'kotlin', 'compose', 'jetpack', 'android', 'architecture', 'mvvm', 'technical',
        'stack', 'expertise', 'competenc',
      ],
      weight: 1,
    },
    certifications: {
      keywords: ['cert', 'certification', 'certified', 'scrum', 'csm', 'copilot', 'genai', 'credential', 'badge'],
      weight: 1,
    },
    contact: {
      keywords: ['contact', 'email', 'linkedin', 'phone', 'hire', 'reach', 'connect', 'call', 'message', 'talk'],
      weight: 1,
    },
    leadership: {
      keywords: ['lead', 'leader', 'manage', 'manager', 'team', 'okr', 'hiring', 'people', '1:1', 'mentor', 'style'],
      weight: 1,
    },
    ai: {
      keywords: ['ai', 'copilot', 'genai', 'claude', 'gemini', 'artificial', 'machine learning', 'llm', 'assistant'],
      weight: 1,
    },
    airline: {
      keywords: ['airline', 'airways', 'aviation', 'travel', 'amadeus', 'checkmytrip'],
      weight: 1.3,
    },
    banking: {
      keywords: ['banking', 'bank', 'npci', 'payment', 'nfc', 'hce', 'finance', 'fintech'],
      weight: 1.3,
    },
    enterprise: {
      keywords: ['enterprise', 'b2b', 'campbell', 'cintas', 'dynamics', 'sap', 'field service', 'retail'],
      weight: 1.2,
    },
    resume: {
      keywords: ['resume', 'cv', 'download', 'pdf', 'document'],
      weight: 1.4,
    },
    testimonials: {
      keywords: ['testimonial', 'recommendation', 'reference', 'feedback', 'review', 'endorse'],
      weight: 1.2,
    },
  };

  const KB = typeof TIRU_KNOWLEDGE !== 'undefined' ? TIRU_KNOWLEDGE : {};

  let isTyping = false;
  let panelOpen = false;
  let previousFocus = null;

  const els = {};

  function init() {
    cacheElements();
    if (!els.panel) return;

    bindEvents();
    restoreMessages();
    markFabSeen();
  }

  function cacheElements() {
    els.fab = document.getElementById('tiru-fab');
    els.panel = document.getElementById('tiru-panel');
    els.closeBtn = document.getElementById('tiru-close');
    els.clearBtn = document.getElementById('tiru-clear');
    els.messages = document.getElementById('tiru-messages');
    els.input = document.getElementById('tiru-input');
    els.sendBtn = document.getElementById('tiru-send');
    els.prompts = document.getElementById('tiru-prompts');
    els.liveRegion = document.getElementById('tiru-live');
  }

  function bindEvents() {
    els.fab.addEventListener('click', () => {
      trackEvent('Tiru Open');
      togglePanel();
    });
    els.closeBtn.addEventListener('click', closePanel);
    if (els.clearBtn) els.clearBtn.addEventListener('click', clearChat);
    els.sendBtn.addEventListener('click', handleSend);
    els.input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        handleSend();
      }
    });

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && panelOpen) closePanel();
    });

    els.prompts.addEventListener('click', (e) => {
      const chip = e.target.closest('.tiru-prompt-chip');
      if (!chip || isTyping) return;
      els.input.value = chip.dataset.prompt || chip.textContent.trim();
      handleSend();
    });
  }

  function trackEvent(name) {
    if (typeof window.plausible === 'function') {
      window.plausible(name);
    }
  }

  function clearChat() {
    if (isTyping) return;
    els.messages.innerHTML = '';
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (_) { /* ignore */ }
    showWelcome();
  }

  function togglePanel() {
    if (panelOpen) {
      closePanel();
    } else {
      openPanel();
    }
  }

  function openPanel() {
    panelOpen = true;
    previousFocus = document.activeElement;
    els.panel.hidden = false;
    els.panel.classList.add('tiru-panel--open');
    els.fab.setAttribute('aria-expanded', 'true');
    document.body.classList.add('tiru-open');

    const messages = loadMessages();
    if (messages.length === 0) {
      showWelcome();
    }

    trapFocus();
    setTimeout(() => els.input.focus(), 100);
  }

  function closePanel() {
    panelOpen = false;
    els.panel.classList.remove('tiru-panel--open');
    els.fab.setAttribute('aria-expanded', 'false');
    document.body.classList.remove('tiru-open');

    setTimeout(() => {
      if (!panelOpen) els.panel.hidden = true;
    }, 300);

    if (previousFocus) previousFocus.focus();
  }

  function trapFocus() {
    const focusable = els.panel.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    const first = focusable[0];
    const last = focusable[focusable.length - 1];

    els.panel.addEventListener('keydown', function onTab(e) {
      if (e.key !== 'Tab') return;
      if (e.shiftKey && document.activeElement === first) {
        e.preventDefault();
        last.focus();
      } else if (!e.shiftKey && document.activeElement === last) {
        e.preventDefault();
        first.focus();
      }
    });
  }

  function showWelcome() {
    const greeting = renderGreeting();
    addMessage('tiru', greeting, false);
    showPrompts(true);
  }

  function handleSend() {
    const text = els.input.value.trim();
    if (!text || isTyping) return;

    els.input.value = '';
    showPrompts(false);
    addMessage('user', text);
    respond(text);
  }

  function respond(userText) {
    isTyping = true;
    els.input.disabled = true;
    els.sendBtn.disabled = true;

    const typingEl = showTypingIndicator();
    const delay = 300 + Math.random() * 300;

    setTimeout(() => {
      removeTypingIndicator(typingEl);
      const intent = matchIntent(userText);
      const html = buildResponse(intent, userText);
      addMessage('tiru', html);
      announce(html);

      isTyping = false;
      els.input.disabled = false;
      els.sendBtn.disabled = false;
      els.input.focus();
    }, delay);
  }

  function normalize(text) {
    return text.toLowerCase().replace(/[^\w\s]/g, ' ').replace(/\s+/g, ' ').trim();
  }

  function matchIntent(text) {
    const normalized = normalize(text);
    const words = normalized.split(' ');
    let best = { intent: 'fallback', score: 0 };

    for (const [intent, config] of Object.entries(INTENTS)) {
      let score = 0;
      for (const keyword of config.keywords) {
        if (normalized.includes(keyword)) {
          score += keyword.includes(' ') ? 2 : 1;
        }
        for (const word of words) {
          if (word === keyword || (word.length > 3 && keyword.startsWith(word))) {
            score += 0.5;
          }
        }
      }
      score *= config.weight;
      if (score > best.score) {
        best = { intent, score };
      }
    }

    return best.score >= 1 ? best.intent : 'fallback';
  }

  function buildResponse(intent, userText) {
    const p = KB.profile || {};
    const normalized = normalize(userText);

    switch (intent) {
      case 'greeting':
        return renderGreeting();

      case 'experience':
        return renderExperience();

      case 'projects':
        return renderProjects(normalized);

      case 'skills':
        return renderSkills(normalized);

      case 'certifications':
        return renderCertifications();

      case 'contact':
        return renderContact();

      case 'leadership':
        return renderLeadership();

      case 'ai':
        return renderAi();

      case 'testimonials':
        return renderTestimonials();

      case 'airline':
        return renderDomain('airline');

      case 'banking':
        return renderDomain('banking');

      case 'enterprise':
        return renderDomain('enterprise');

      case 'resume':
        return renderResume();

      default:
        return renderFallback();
    }
  }

  function renderGreeting() {
    const p = KB.profile || {};
    return (
      `<p>Hi! I'm <strong>Tiru</strong>, Tripaty's portfolio assistant.</p>` +
      `<p>I can answer questions about his <strong>experience</strong>, <strong>projects</strong>, ` +
      `<strong>skills</strong>, <strong>certifications</strong>, and how to <strong>get in touch</strong>.</p>` +
      `<p>What would you like to know?</p>`
    );
  }

  function renderExperience() {
    const p = KB.profile || {};
    const stats = KB.stats || {};
    let html =
      `<p><strong>${p.name}</strong> is an ${p.title} based in ${p.location} with ` +
      `<strong>${stats.years} years</strong> of experience leading Android teams of up to ` +
      `<strong>${stats.teamSize} engineers</strong>.</p>` +
      `<p><strong>Career highlights:</strong></p><ul>`;

    (KB.experience || []).forEach((job) => {
      html += `<li><strong>${job.role}</strong> at ${job.company} (${job.dates}) — ${job.highlights}</li>`;
    });

    html += `</ul><p><a href="#experience" class="tiru-link">View case studies &darr;</a></p>`;
    return html;
  }

  function renderProjects(query) {
    let studies = KB.caseStudies || [];

    const matched = studies.filter((cs) =>
      (cs.keywords || []).some((kw) => query.includes(kw)) ||
      normalize(cs.project).split(' ').some((w) => w.length > 3 && query.includes(w)) ||
      normalize(cs.company).split(' ').some((w) => w.length > 3 && query.includes(w))
    );

    if (matched.length > 0) studies = matched;

    let html = `<p>Here are Tripaty's key project achievements:</p>`;

    studies.slice(0, matched.length > 0 ? matched.length : 2).forEach((cs) => {
      html +=
        `<div class="tiru-case">` +
        `<p><strong>${cs.project}</strong> (${cs.company})</p>` +
        `<p><em>Problem:</em> ${cs.problem}</p>` +
        `<p><em>Action:</em> ${cs.action}</p>` +
        `<p><em>Result:</em> ${cs.result}</p>`;
      if (cs.link) {
        html += `<p><a href="${cs.link}" target="_blank" rel="noopener noreferrer" class="tiru-link">Google Play &rarr;</a></p>`;
      }
      html += `</div>`;
    });

    html += `<p><a href="#experience" class="tiru-link">View all case studies &darr;</a></p>`;
    return html;
  }

  function renderSkills(query) {
    const skills = KB.skills || {};
    const categories = {
      leadership: { label: 'Leadership & Delivery', match: ['lead', 'manage', 'people', 'okr', 'hiring', 'agile', 'delivery'] },
      android: { label: 'Android Engineering', match: ['kotlin', 'compose', 'android', 'mvvm', 'architecture', 'mobile'] },
      platform: { label: 'Platform & Quality', match: ['ci', 'cd', 'test', 'firebase', 'api', 'graphql', 'room'] },
      ai: { label: 'AI-Assisted Engineering', match: ['ai', 'copilot', 'genai', 'claude', 'gemini'] },
    };

    let filtered = Object.keys(categories);
    const matched = filtered.filter((cat) =>
      categories[cat].match.some((m) => query.includes(m))
    );
    if (matched.length > 0) filtered = matched;

    let html = `<p>Tripaty's core skills:</p>`;

    filtered.forEach((cat) => {
      const tags = (skills[cat] || []).slice(0, 8);
      html += `<p><strong>${categories[cat].label}</strong></p><p class="tiru-tags">`;
      tags.forEach((tag) => {
        html += `<span class="tiru-tag">${tag}</span>`;
      });
      html += `</p>`;
    });

    html += `<p><a href="#skills" class="tiru-link">View full skills section &darr;</a></p>`;
    return html;
  }

  function renderCertifications() {
    let html = `<p>Tripaty holds <strong>${(KB.certifications || []).length} certifications</strong>:</p><ul>`;

    (KB.certifications || []).forEach((cert) => {
      html +=
        `<li><a href="${cert.url}" target="_blank" rel="noopener noreferrer" class="tiru-link">` +
        `<strong>${cert.name}</strong></a> — ${cert.issuer} (${cert.date})</li>`;
    });

    html += `</ul><p><a href="#certifications" class="tiru-link">View certifications &darr;</a></p>`;
    return html;
  }

  function renderContact() {
    const p = KB.profile || {};
    const resume = p.resumeUrl || 'resume.pdf';
    return (
      `<p>Here's how to reach Tripaty:</p>` +
      `<div class="tiru-contact-actions">` +
      `<a href="mailto:${p.email}" class="tiru-action-btn tiru-action-btn--email">Email</a>` +
      `<a href="${p.linkedin}" target="_blank" rel="noopener noreferrer" class="tiru-action-btn tiru-action-btn--linkedin">LinkedIn</a>` +
      `<a href="tel:${p.phone.replace(/[^+\d]/g, '')}" class="tiru-action-btn tiru-action-btn--phone">Call</a>` +
      `<a href="${resume}" download class="tiru-action-btn tiru-action-btn--resume">Resume</a>` +
      `</div>` +
      `<p><em>${p.availability}</em></p>`
    );
  }

  function renderTestimonials() {
    const items = KB.testimonials || [];
    let html = `<p>What colleagues say about Tripaty's leadership:</p>`;
    items.slice(0, 2).forEach((t) => {
      html +=
        `<div class="tiru-case">` +
        `<p><em>"${t.quote}"</em></p>` +
        `<p><strong>${t.name}</strong> — ${t.title}, ${t.company}</p>` +
        `</div>`;
    });
    html += `<p><a href="#testimonials" class="tiru-link">Read recommendations &darr;</a></p>`;
    return html;
  }

  function renderDomain(domain) {
    const studies = (KB.caseStudies || []).filter((cs) =>
      (cs.keywords || []).some((kw) => {
        if (domain === 'airline') return ['qatar', 'airline', 'airways', 'aviation', 'travel'].includes(kw);
        if (domain === 'banking') return ['npci', 'payment', 'nfc', 'banking'].includes(kw);
        if (domain === 'enterprise') return ['campbell', 'cintas', 'dynamics', 'enterprise', 'star'].includes(kw);
        return false;
      })
    );

    const domainLabels = { airline: 'airline', banking: 'banking and payments', enterprise: 'enterprise' };
    let html = `<p>Yes — Tripaty has <strong>${domainLabels[domain]}</strong> experience:</p>`;

    if (studies.length > 0) {
      studies.forEach((cs) => {
        html += `<p><strong>${cs.project}</strong> (${cs.company}) — ${cs.result}</p>`;
      });
    } else {
      html += `<p>See case studies in the experience section for details.</p>`;
    }

    html += `<p><a href="#experience" class="tiru-link">View case studies &darr;</a></p>`;
    return html;
  }

  function renderResume() {
    const p = KB.profile || {};
    const faqs = KB.faqs || {};
    const resume = p.resumeUrl || 'resume.pdf';
    return (
      `<p>${faqs.hiring || ''}</p>` +
      `<p><a href="${resume}" download class="tiru-link">Download resume (PDF) &darr;</a></p>` +
      `<p><a href="${p.linkedin}" target="_blank" rel="noopener noreferrer" class="tiru-link">LinkedIn profile &rarr;</a></p>`
    );
  }

  function renderLeadership() {
    const faqs = KB.faqs || {};
    const stats = KB.stats || {};
    return (
      `<p>${faqs.leadership || ''}</p>` +
      `<p>At NTT DATA, Tripaty currently leads teams of <strong>8–10 Android engineers</strong> ` +
      `across enterprise programs serving <strong>${stats.users.toLowerCase()}</strong> of users.</p>` +
      `<p><a href="#about" class="tiru-link">Read full profile &darr;</a></p>`
    );
  }

  function renderAi() {
    const faqs = KB.faqs || {};
    const tools = (KB.skills && KB.skills.ai) || [];
    return (
      `<p>${faqs.ai || ''}</p>` +
      `<p class="tiru-tags">${tools.map((t) => `<span class="tiru-tag">${t}</span>`).join('')}</p>` +
      `<p><a href="#certifications" class="tiru-link">View AI certifications &darr;</a></p>`
    );
  }

  function renderFallback() {
    const prompts = (KB.quickPrompts || []).map((p) => `"${p}"`).join(', ');
    return (
      `<p>I'm not sure about that one, but I can help with experience, projects, skills, certifications, or contact info.</p>` +
      `<p>Try asking: ${prompts}</p>`
    );
  }

  function addMessage(role, html, persist = true) {
    const div = document.createElement('div');
    div.className = `tiru-msg tiru-msg--${role}`;
    div.innerHTML = `<div class="tiru-bubble">${html}</div>`;
    els.messages.appendChild(div);
    els.messages.scrollTop = els.messages.scrollHeight;

    if (persist) saveMessage(role, html);
  }

  function showTypingIndicator() {
    const div = document.createElement('div');
    div.className = 'tiru-msg tiru-msg--tiru tiru-msg--typing';
    div.innerHTML =
      `<div class="tiru-bubble"><span class="tiru-typing"><span></span><span></span><span></span></span></div>`;
    els.messages.appendChild(div);
    els.messages.scrollTop = els.messages.scrollHeight;
    return div;
  }

  function removeTypingIndicator(el) {
    if (el && el.parentNode) el.parentNode.removeChild(el);
  }

  function showPrompts(visible) {
    els.prompts.hidden = !visible;
  }

  function announce(text) {
    if (!els.liveRegion) return;
    els.liveRegion.textContent = text.replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim();
  }

  function saveMessage(role, html) {
    const messages = loadMessages();
    messages.push({ role, html, ts: Date.now() });
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(messages));
    } catch (_) { /* quota exceeded */ }
  }

  function loadMessages() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch (_) {
      return [];
    }
  }

  function restoreMessages() {
    const messages = loadMessages();
    if (messages.length === 0) return;

    messages.forEach((msg) => {
      const div = document.createElement('div');
      div.className = `tiru-msg tiru-msg--${msg.role}`;
      div.innerHTML = `<div class="tiru-bubble">${msg.html}</div>`;
      els.messages.appendChild(div);
    });
    showPrompts(false);
  }

  function markFabSeen() {
    if (!els.fab) return;
    if (!localStorage.getItem(FAB_SEEN_KEY)) {
      els.fab.classList.add('tiru-fab--pulse');
      els.fab.addEventListener('click', () => {
        localStorage.setItem(FAB_SEEN_KEY, '1');
        els.fab.classList.remove('tiru-fab--pulse');
      }, { once: true });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
