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

Plausible is configured for `tripati.github.io`. Add the site at [plausible.io](https://plausible.io) to start collecting page views.

## Deployment

Hosted on GitHub Pages from the `main` branch. Push to `main` to publish:

```bash
git push origin main
```

## Contact

- Email: tripati1987@gmail.com
- LinkedIn: [tripaty-kumar-sahu-07a8732b](https://www.linkedin.com/in/tripaty-kumar-sahu-07a8732b/)
