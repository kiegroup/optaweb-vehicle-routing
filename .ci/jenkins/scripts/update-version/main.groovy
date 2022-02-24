void execute(def pipelinesCommon) {
    maven.mvnVersionsUpdateParentAndChildModules(pipelinesCommon.getDefaultMavenCommand(), pipelinesCommon.getOptaPlannerVersion(), !pipelinesCommon.isRelease())
}

return this
