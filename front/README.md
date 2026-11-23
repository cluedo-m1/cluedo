# Front

Client web **Angular** (TypeScript). Il ne communique qu'avec l'API REST : l'URL du back est définie une seule fois, dans `src/app/api.service.ts`, et tous les appels passent par `ApiService`.

Prérequis : Node.js 22.12+ ou 24 (Angular 22).

```bash
./gradlew bootRun --args='--spring.profiles.active=h2,demo'   # back, depuis la racine du dépôt
cd front && npm ci && npm start                                # front, sur http://localhost:4200
```

L'origine `http://localhost:4200` est autorisée par le CORS du back (`app.cors.origines-autorisees`).

```bash
npm test          # tests unitaires (Vitest)
npm run build     # build de production dans dist/
```
