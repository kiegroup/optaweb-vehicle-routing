import org.kie.jenkins.jobdsl.model.Folder
import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils

OPTAWEB_VEHICLE_ROUTING = 'optaweb-vehicle-routing'

def getDefaultJobParams() {
    return KogitoJobUtils.getDefaultJobParams(this, OPTAWEB_VEHICLE_ROUTING)
}

Map getMultijobPRConfig() {
    return [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: OPTAWEB_VEHICLE_ROUTING,
                primary: true,
            ]
        ],
    ]
}

KogitoJobUtils.createAllEnvsPerRepoPRJobs(this, { jobFolder -> getMultijobPRConfig() }, { return getDefaultJobParams() })

// Nightly
KogitoJobUtils.createAllJobsForArtifactsRepository(this, OPTAWEB_VEHICLE_ROUTING, ['optaplanner'])