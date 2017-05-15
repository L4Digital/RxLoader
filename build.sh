./gradlew \
    clean \
    lintRelease \
    assembleRelease \
    generateArchives \
    generatePomFileForAarPublication

open rxloader/build/reports/lint-results-release.html
