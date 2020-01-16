
Due to RH prod sec requirements, all plugins in CRW plugin registry must be built from sources on RH infrastructure.
`vscode-extensions-packaging` - a Jenkins Pipeline that is able to build a VSIX extension from a given repository, and publish it to dl.jboss.org, along with it's sources

##Example of using the CRW CI to build a VSIX extension, using vscode-kubernetes-tools extension, version 1.0.9 as an example:

1) Navigate to the CRW CI https://codeready-workspaces-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/view/CRW_CI/view/Builds/job/vscode-extensions-packaging/
2) Start the new build nad provide the necessary parameters:

- node - type of Jenkins node. Use the default value
- branchToBuildPlugin - branch or tag, or commit SHA of extension repository (e.g. `1.0.7`)
- extensionPath - link to repository to build the extension from (e.g. `https://github.com/Azure/vscode-kubernetes-tools`)
- publishAddr - full address for the endpoint, that is used to publish extensions. Use default value, which contains special link that would be used for uploading to https://download.jboss.org/jbosstools/vscode/3rdparty/
- publishDestination - end folder for the plugin (after /vscode/3rdparty) (e.g `vscode-kubernetes-tools`)

###Important notice:

- As the job checks out the provided extension repository, it would attempt to build it with a set of standard commands - running "npm install" and then "vsce package". Should your plugin require a different set of commands for building, the job has to be edited manually. Job's Jenkinsfile is located at https://github.com/redhat-developer/codeready-workspaces/tree/master/dependencies/che-plugin-registry/Jenkinsfile-single.
- Also along with the packaged extensions, the job must also provide sources. If you are editing the Jenkinsfile to provide 
- If extension was built from "master" branch or commit SHA, then the resulting VSIX extension and sources archive will have commit SHA appended to them.

As the job is running, check out the console output, as the job would build an extension. If it does so successfully, it would prompt a user input for approval of the artifact publish.
It allows to check out the built artifact manually before it goes to dl.jboss.org. Artifact can be accessed at the following link (insert your job number at JOB_NUMBER)
https://codeready-workspaces-jenkins.rhev-ci-vms.eng.rdu2.redhat.com/view/CRW_CI/view/Builds/job/vscode-extensions-packaging/JOB_NUMBER/artifact/

After the artifact is published, it can now be accessed at https://download.jboss.org/jbosstools/vscode/3rdparty/, along with it's sources