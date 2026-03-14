---
name: publish-maven-central
description: Guide for troubleshooting and configuring Maven Central publishing via Sonatype Central Portal. Use when the user encounters publishing failures, 402/401/404 errors from Sonatype, or needs to migrate from legacy OSSRH to the new Central Portal.
argument-hint: "[error-description]"
allowed-tools: Read, Grep, Glob, Bash, Edit, Write, WebFetch
---

# Maven Central Publishing via Sonatype Central Portal

## Context

The legacy OSSRH endpoints (`oss.sonatype.org` and `s01.oss.sonatype.org`) have been shut down. All publishing must go through the **new Central Portal** at `central.sonatype.com`.

Common symptoms of needing this migration:
- `402 Payment Required` from `s01.oss.sonatype.org`
- `404 Not Found` from `central.sonatype.com/api/v1/publisher/`
- `401 Unauthorized` from `ossrh-staging-api.central.sonatype.com`

## Plugin & URL Configuration

### Correct Plugin
Use `io.github.gradle-nexus.publish-plugin` version **2.0.0+** in the **root** `build.gradle`:

```groovy
plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}
```

### Correct URLs
The staging API compatibility layer — NOT the Portal REST API:

| Setting | URL |
|---------|-----|
| `nexusUrl` | `https://ossrh-staging-api.central.sonatype.com/service/local/` |
| `snapshotRepositoryUrl` | `https://central.sonatype.com/repository/maven-snapshots/` |

**WRONG URLs** (do NOT use):
- `https://s01.oss.sonatype.org/...` (legacy, returns 402)
- `https://central.sonatype.com/api/v1/publisher/` (Portal REST API, returns 404 for staging plugin)

### Correct Gradle Configuration

```groovy
nexusPublishing {
    packageGroup.set("<group-id>")  // e.g., "com.burhanrashid52"
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(providers.gradleProperty('ossrhUsernameV2'))
            password.set(providers.gradleProperty('ossrhPasswordV2'))
        }
    }
}
```

### Correct Workflow Command

```bash
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository --max-workers 1 \
  -PossrhUsernameV2=$OSSRH_USERNAME_V2 \
  -PossrhPasswordV2=$OSSRH_PASSWORD_V2
```

## Key Lessons Learned

### 1. Credentials Must Be Passed as Gradle Properties (`-P`), Not Just Env Vars
`System.getenv()` and even `providers.environmentVariable()` can fail silently when the `nexusPublishing` block is in the root `build.gradle`. The reliable approach is:
- Set env vars in the workflow step
- Pass them to Gradle as `-P` properties on the command line
- Read them with `providers.gradleProperty()` in the build script

### 2. `packageGroup` Is Required
Without `packageGroup.set("<your-group-id>")`, the plugin cannot resolve the correct staging profile and authentication may fail with a 401.

### 3. Remove Legacy Plugins and Dependencies
When migrating, remove:
- `io.codearte.nexus-staging` plugin and its classpath dependency
- `com.github.dcendents:android-maven-gradle-plugin` (if unused)
- `nexusStaging { ... }` block from publish scripts
- `repositories { maven { ... } }` block from publish scripts (the nexus plugin handles this)
- `sonatypeStagingProfileId` references (no longer needed)

### 4. Credential Format
Credentials must be **user tokens** generated from Central Portal (central.sonatype.com > Account > Generate User Token), NOT login email/password.

### 5. Verifying Credentials via curl
```bash
curl -u "TOKEN_USERNAME:TOKEN_PASSWORD" \
  -H "Accept: application/json" \
  "https://ossrh-staging-api.central.sonatype.com/service/local/staging/profiles"
```
- **200** = credentials valid
- **401** = credentials invalid

## Troubleshooting Checklist

When publish fails, check in order:

1. **402 from s01.oss.sonatype.org** → Migrate to Central Portal (URLs above)
2. **404 from central.sonatype.com** → Use `ossrh-staging-api.central.sonatype.com` URL
3. **401 Unauthorized** → Check:
   - Are credentials user tokens (not login password)?
   - Is `packageGroup` set?
   - Are credentials passed as `-P` Gradle properties?
   - Verify with curl (see above)
4. **Signing errors** → Ensure GPG key is correctly decoded and `SIGNING_*` env vars are set
5. **Task not found** → Use `publishToSonatype` (not `publishReleasePublicationToSonatypeRepository`)

## Files to Modify During Migration

| File | Changes |
|------|---------|
| `build.gradle` (root) | Remove old plugin, add `nexus-publish-plugin`, add `nexusPublishing` block |
| `scripts/publish-mavencentral.gradle` | Remove `repositories {}` and `nexusStaging {}` blocks, remove old credential refs |
| `.github/workflows/publish_maven.yml` | Update gradle command, pass `-P` properties, update secret names |
