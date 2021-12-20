void execute(def pipelinesCommon) {
    githubscm.findAndStageNotIgnoredFiles('pom.xml')

    // Ignore NPM registry
    sh 'sed \'s;repository.engineering.redhat.com/nexus/repository/;;\' -i */package-lock.json'
    sh 'git add */package-lock.json'
}

return this
