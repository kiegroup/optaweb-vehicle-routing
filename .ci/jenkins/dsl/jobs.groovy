import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate

BUILDCHAIN_CONFIG_BRANCH = '%{process.env.GITHUB_BASE_REF.replace(/(\\d*)\\.(.*)\\.(.*)/g, (m, n1, n2, n3) => `\\${+n1-7}.\\${n2}.\\${n3}`)}'

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
                    BUILDCHAIN_CONFIG_BRANCH: BUILDCHAIN_CONFIG_BRANCH
                ]
            ]
        ],
    ]
}

setupMultijobPrDefaultChecks()
setupMultijobPrNativeChecks()
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
