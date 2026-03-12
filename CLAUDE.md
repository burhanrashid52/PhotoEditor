# PhotoEditor

## Project Overview

An Android SDK/library for image editing, published to Maven Central. Provides a simple API for drawing, applying filters, adding text/emoji/stickers, and more.

## Architecture

### Modules
- `photoeditor/` - Core library module (published to Maven Central)
- `app/` - Sample/demo application

### Key Classes
- `PhotoEditor` / `PhotoEditorImpl` - Main API entry point (Builder pattern)
- `PhotoEditorView` - Custom RelativeLayout container for editing
- `DrawingView` - Custom canvas for brush/shape rendering
- `GraphicManager` - Manages added views (text, sticker, emoji) and undo/redo state
- `PhotoEditorViewState` - Central state holder with undo/redo stacks
- `MultiTouchListener` - Handles pinch, rotate, drag gestures

### Patterns
- **Builder pattern**: `PhotoEditor.Builder`, `ShapeBuilder`, `TextStyleBuilder`, `SaveSettings.Builder`
- **Composite pattern**: `Graphic` interface with `Text`, `Sticker`, `Emoji` implementations
- **Strategy pattern**: `Shape` interface with `BrushShape`, `LineShape`, `OvalShape`, `RectangleShape`
- **Listener pattern**: `OnPhotoEditorListener`, `BrushViewChangeListener`
- **Coroutines**: Image saving uses `Dispatchers.IO` with `SaveFileResult` sealed class

## Build & Run

```bash
# Build
./gradlew build

# Unit tests
./gradlew check

# Instrumentation tests (requires emulator)
./gradlew connectedCheck
```

## Tech Stack
- **Language**: Kotlin 2.0.0
- **Min SDK**: API 21
- **Build**: Gradle 8.5.1 + Android Gradle Plugin 8.5.1
- **Key deps**: AndroidX, Kotlin Coroutines, Glide, Material Design
- **Testing**: JUnit 4, Robolectric, Mockito, Espresso

## Git Workflow

### Branching
Create branches from `master` based on the issue number using the prefix `PE-`:
```
PE-<issue_number>-<short-description>
```
Examples:
- `PE-4-add-arrow-shape`
- `PE-5-fix-eraser-opacity`

### Deploying / Releasing
When asked to deploy, create a new tag with an incremented version following the existing `v.X.Y.Z` format:
- **Patch** (`Z`): Bug fixes, small changes
- **Minor** (`Y`): New features, non-breaking changes
- **Major** (`X`): Breaking changes

The latest tag is `v.3.1.0`. The next tags would be:
- Patch: `v.3.1.1`
- Minor: `v.3.2.0`
- Major: `v.4.0.0`

```bash
git tag v.X.Y.Z
git push origin v.X.Y.Z
```

### Changelog
When releasing, also update `CHANGELOG.md`. Append a new section at the **bottom** of the file using the version number as the heading (`### X.Y.Z`). Each entry is prefixed with a category:
- `New :` for new features
- `Fixed :` for bug fixes
- `Change :` for modifications to existing behavior
- `Deprecated :` for deprecated APIs
- `Removed :` for removed features
- `Test :` for test additions/changes

Include issue numbers where applicable (e.g., `#123`). Example:
```markdown
### 3.2.0
- New : Added circle shape support
- Fixed : #456 Undo not working after filter applied
- Change : (Breaking Change) Renamed `setFilter` to `applyFilter`
```

## Publishing
- Maven Central via `scripts/publish-mavencentral.gradle`
- JitPack via `jitpack.yml` (signing stripped)
- CI/CD via GitHub Actions (`.github/workflows/`)
