import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate

def getDefaultJobParams(String repoName = 'optaweb-vehicle-routing') {
    return KogitoJobTemplate.getDefaultJobParams(this, repoName)
}

Map getMultijobPRConfig() {
    return [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'optaweb-vehicle-routing',
                primary: true,
                env : [
                    // Disable Sonarcloud analysis.
                    DISABLE_SONARCLOUD: true
                ]
            ]
        ],
    ]
}

setupMultijobPrDefaultChecks()
// setupMultijobPrNativeChecks() // There is no requirement for Native support.
setupMultijobPrLTSChecks()

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupMultijobPrDefaultChecks() {
    KogitoJobTemplate.createMultijobPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrNativeChecks() {
    KogitoJobTemplate.createMultijobNativePRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrLTSChecks() {
    KogitoJobTemplate.createMultijobLTSPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}
