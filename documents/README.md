# CasualApp Documentation

This folder contains the detailed technical and planning material that was removed from the root `README.md`.

| Document | Purpose |
|---|---|
| [DEVELOPMENT_COMMANDS.md](DEVELOPMENT_COMMANDS.md) | Local setup, build, run, reset and troubleshooting commands |
| [API_REFERENCE.md](API_REFERENCE.md) | Current REST endpoints, parameters, statuses and response conventions |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Backend and Android architecture, package responsibilities and data flow |
| [DEMO_AND_TESTING.md](DEMO_AND_TESTING.md) | Demo preparation, test sequence and regression checklist |
| [ROADMAP.md](ROADMAP.md) | Prioritized short-term and later improvements |
| [DTO_REFACTORING.md](DTO_REFACTORING.md) | Planned request/response DTO structure and migration order |
| [DATA_MODEL_AND_SEEDING.md](DATA_MODEL_AND_SEEDING.md) | Domain models, status enums, seed-data strategy and known data limitations |
| [UI_SCREEN_MAP.md](UI_SCREEN_MAP.md) | Current Android screens, navigation paths and unfinished UI areas |

## Documentation rules

- Keep the root `README.md` focused on product vision, test users and current progress.
- Put setup commands and operational notes in `DEVELOPMENT_COMMANDS.md`.
- Update `API_REFERENCE.md` whenever a controller route or parameter changes.
- Update `ROADMAP.md` when priorities change.
- Update `DTO_REFACTORING.md` as APIs move away from direct entity serialization.
- Keep `structure.txt` as a raw snapshot only; do not manually reproduce its complete contents.
