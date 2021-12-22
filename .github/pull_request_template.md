<!--
Thank you for submitting this pull request.

Please provide all relevant information as outlined below. Feel free to delete
a section if that type of information is not available.
-->

### JIRA

<!-- Add a JIRA ticket link if it exists. -->
<!-- Example: https://issues.redhat.com/browse/PLANNER-1234 -->

### Referenced pull requests

<!-- Add URLs of all referenced pull requests if they exist. This is only required when making
changes that span multiple kiegroup repositories and depend on each other. -->
<!-- Example:
* https://github.com/kiegroup/droolsjbpm-build-bootstrap/pull/1234
* https://github.com/kiegroup/drools/pull/3000
* https://github.com/kiegroup/optaplanner/pull/899
* etc.
-->

<details>
<summary>
How to replicate CI configuration locally?
</summary>

We do "simple" maven builds, they are just basically maven commands, but just because we have multiple repositories related between them and one change could affect several of those projects by multiple pull requests, we use [build-chain tool](https://github.com/kiegroup/github-action-build-chain) to handle cross repository builds and be sure that we always use latest version of the code for each repository.

[build-chain tool](https://github.com/kiegroup/github-action-build-chain) is not only a github-action tool but a CLI one, so in case you posted multiple pull requests related with this change you can easily reproduce the same build by executing it locally. See [local execution](https://github.com/kiegroup/github-action-build-chain#local-execution) details to get more information about it.
</details>

<details>
<summary>
How to retest this PR or trigger a specific build:
</summary>

* <b>a pull request</b> please add comment: <b>Jenkins retest</b> (using <i>this</i> e.g. <b>Jenkins retest this</b> optional but no longer required)
 
* for a <b>full downstream build</b> 
  * for <b>jenkins</b> job: please add comment: <b>Jenkins run fdb</b>
  * for <b>github actions</b> job: add the label `run_fdb`
    
* <b>a compile downstream build</b> please  add comment: <b>Jenkins run cdb</b>

* <b>a full production downstream build</b> please add comment: <b>Jenkins execute product fdb</b>

* <b>an upstream build</b> please add comment: <b>Jenkins run upstream</b>
</details>
