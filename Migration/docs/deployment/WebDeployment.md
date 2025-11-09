# Web Deployment Guide

## Overview

This document describes how to build and deploy the Trachtenberg Multiplication web application.

## Prerequisites

- Node.js 18+ installed
- Expo CLI installed (`npm install -g expo-cli`)
- Access to deployment server

## Build Configuration

### app.json Web Settings

The web build is configured in `app.json`:

```json
{
  "expo": {
    "web": {
      "favicon": "./assets/favicon.png",
      "bundler": "metro",
      "output": "static"
    }
  }
}
```

**Configuration Options:**
- `bundler`: Uses Metro bundler for consistency with mobile platforms
- `output`: "static" generates static HTML/CSS/JS files for hosting
- `favicon`: Path to favicon image

## Building for Production

### 1. Build Command

```bash
cd Migration/expo-project
npx expo export:web
```

### 2. Build Output

The command generates static files in the `dist/` directory:
- `index.html` - Main HTML file
- `_expo/static/js/` - JavaScript bundles
- `_expo/static/css/` - Stylesheets
- `_expo/static/media/` - Images and other assets

### 3. Build Optimization

The build process automatically:
- Minifies JavaScript and CSS
- Optimizes images
- Generates source maps for debugging
- Bundles dependencies efficiently

## Deployment Options

### Option 1: Static File Hosting

Upload the contents of `dist/` to any static file server:
- AWS S3 + CloudFront
- Netlify
- Vercel
- GitHub Pages
- Your own nginx/Apache server

### Option 2: Self-Hosted Server (nginx)

#### nginx Configuration

Create `/etc/nginx/sites-available/trachtenberg`:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/trachtenberg;
    index index.html;

    # SPA routing support
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets
    location /_expo/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

#### Deployment Steps

```bash
# 1. Build the app
npx expo export:web

# 2. Transfer files to server
scp -r dist/* user@your-server:/var/www/trachtenberg/

# 3. Enable nginx site
sudo ln -s /etc/nginx/sites-available/trachtenberg /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### Option 3: HTTPS with Let's Encrypt

```bash
# Install certbot
sudo apt-get install certbot python3-certbot-nginx

# Get certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal is set up automatically
```

## Testing the Deployment

### 1. Local Testing

Before deploying, test the build locally:

```bash
# Build
npx expo export:web

# Serve locally
npx serve dist

# Open browser to http://localhost:3000
```

### 2. Production Testing

After deployment, verify:

1. **Functionality**
   - Tutorial navigation works
   - Practice mode generates problems
   - Settings persist
   - Hints system functions correctly

2. **Performance**
   - Page loads in < 3 seconds
   - Smooth animations
   - No console errors

3. **Responsive Design**
   - Test on mobile devices
   - Test on tablets
   - Test on desktop

4. **Cross-Browser**
   - Chrome
   - Firefox
   - Safari
   - Edge

## Performance Optimization

### Bundle Size Analysis

Check bundle size after build:

```bash
ls -lh dist/_expo/static/js/
```

Expected bundle sizes:
- Main bundle: ~500KB (gzipped: ~150KB)
- Vendor bundle: ~200KB (gzipped: ~60KB)

### Lighthouse Audit

Run Google Lighthouse to verify performance:

```bash
npm install -g lighthouse
lighthouse https://your-domain.com --view
```

Target scores:
- Performance: 90+
- Accessibility: 95+
- Best Practices: 90+
- SEO: 90+

## Troubleshooting

### Build Fails

**Error: "Access denied" or API errors**
- Solution: Check internet connection and Expo account status

**Error: "Module not found"**
- Solution: Run `npm install` and try again

### Deployment Issues

**Blank page after deployment**
- Check: Browser console for errors
- Check: Correct `base` path in routing
- Check: All assets uploaded correctly

**Routing doesn't work (404 on refresh)**
- Check: Server configured for SPA routing (try_files)
- Check: .htaccess for Apache or nginx.conf for nginx

**Assets not loading**
- Check: Relative paths in build output
- Check: CORS headers if assets on different domain

## Environment Variables

If you need environment-specific configuration:

1. Create `.env.production`:
```
EXPO_PUBLIC_API_URL=https://api.your-domain.com
```

2. Access in code:
```typescript
const apiUrl = process.env.EXPO_PUBLIC_API_URL;
```

3. Rebuild for changes to take effect

## Continuous Deployment

### GitHub Actions Example

Create `.github/workflows/deploy-web.yml`:

```yaml
name: Deploy Web

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2

      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '18'

      - name: Install dependencies
        run: |
          cd Migration/expo-project
          npm ci

      - name: Build web app
        run: |
          cd Migration/expo-project
          npx expo export:web

      - name: Deploy to server
        run: |
          # Add your deployment script here
          # e.g., rsync, scp, or cloud provider CLI
```

## Monitoring

### Uptime Monitoring

Set up monitoring with:
- UptimeRobot
- Pingdom
- StatusCake

### Analytics

Consider adding:
- Google Analytics
- Plausible Analytics (privacy-focused)
- Custom event tracking

## Maintenance

### Updates

To update the deployed app:

```bash
# 1. Pull latest changes
git pull

# 2. Install dependencies
npm install

# 3. Build
npx expo export:web

# 4. Deploy
# (use your deployment method)
```

### Rollback

Keep previous builds for quick rollback:

```bash
# On server
mv /var/www/trachtenberg /var/www/trachtenberg-backup
mv /var/www/trachtenberg-previous /var/www/trachtenberg
```

## Security Considerations

1. **HTTPS Required**: Always use HTTPS in production
2. **Content Security Policy**: Add CSP headers for XSS protection
3. **CORS**: Configure properly if using external APIs
4. **Rate Limiting**: Implement if exposing any APIs

## Support

For deployment issues:
- Check Expo documentation: https://docs.expo.dev/distribution/publishing-websites/
- Review nginx documentation: https://nginx.org/en/docs/
- Contact your hosting provider for server-specific issues

## Version History

- v1.0.0 (2025-11-09): Initial deployment configuration
