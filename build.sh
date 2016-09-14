./gradlew \
    clean \
    lintRelease \
    assembleRelease \
    generateArchives \
    generatePomFileForAarPublication

open rxloader/build/outputs/lint-results-release.html
