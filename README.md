# Portfolio

Personal portfolio site for **Tripaty Kumar Sahu** — Engineering Manager, Mobile (Native Android).

**Live site:** [tripati.github.io/Portfolio](https://tripati.github.io/Portfolio/)

## Features

- Responsive portfolio with case studies, skills, certifications, and career timeline
- **Testimonials** from engineering colleagues
- **Tiru** — rule-based chatbot assistant for recruiters (client-only, no API keys)
- Contact via email (click to reveal), LinkedIn, and Tiru chatbot
- Downloadable resume PDF
- Light/dark theme toggle (dark by default)
- Mobile navigation with slide-out menu
- SEO metadata, Open Graph image, favicon, and JSON-LD Person schema
- Privacy-friendly analytics via Plausible

## Structure

```
Portfolio/
  index.html          # Page markup, styles, and site scripts
  tiru-knowledge.js   # Chatbot knowledge base (profile, skills, projects, certs)
  tiru.js             # Intent matcher, response renderer, chat UI logic
  resume.pdf          # Downloadable resume
  og-image.png        # Social sharing preview image
  favicon.svg         # Browser tab icon
```

## Local preview

```bash
python3 -m http.server 8080
```

Open [http://localhost:8080](http://localhost:8080).

## Updating content

Resume and portfolio content lives in two places:

1. **`index.html`** — visible page sections (hero, case studies, timeline, certs, testimonials)
2. **`tiru-knowledge.js`** — structured data Tiru uses for chat responses

When you update your resume, edit both files so the page and chatbot stay in sync. The hero availability badge reads from `TIRU_KNOWLEDGE.profile.availability` automatically.

Replace `resume.pdf` with your full resume PDF when ready.

## Analytics

Umami Cloud is configured for `tripati.github.io`.

1. Sign up at [cloud.umami.is](https://cloud.umami.is) and add your website.
2. Copy the website ID from **Edit website → Tracking code**.
3. Paste it into `tiru-knowledge.js` as `site.umamiWebsiteId`, then sync:

```bash
node -e "
const fs=require('fs');
const code=fs.readFileSync('tiru-knowledge.js','utf8').replace('const TIRU_KNOWLEDGE = ','globalThis.TIRU_KNOWLEDGE = ');
eval(code);
fs.writeFileSync('portfolio.json', JSON.stringify(globalThis.TIRU_KNOWLEDGE, null, 2));
const s=globalThis.TIRU_KNOWLEDGE.site;
let html=fs.readFileSync('index.html','utf8');
html=html.replace(/<script async src=\\\"https:\\/\\/cloud\\.umami\\.is\\/script\\.js\\\"[^>]*><\\/script>/,
  '<script async src=\\\"'+s.umamiScriptUrl+'\\\" data-website-id=\\\"'+s.umamiWebsiteId+'\\\" data-domains=\\\"tripati.github.io\\\"></script>');
fs.writeFileSync('index.html', html);
"
```

4. Push to `main` and confirm page views in the Umami **Realtime** dashboard.

## Deployment

Hosted on GitHub Pages from the `main` branch. Push to `main` to publish:

```bash
git push origin main
```

## Contact

- Email: tripati1987@gmail.com
- LinkedIn: [tripaty-kumar-sahu-07a8732b](https://www.linkedin.com/in/tripaty-kumar-sahu-07a8732b/)
