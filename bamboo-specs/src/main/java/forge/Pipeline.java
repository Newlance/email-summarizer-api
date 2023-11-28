package forge;


import static com.lmig.forge.bamboo.specs.patterns.BuildAddOn.SONARQUBE;
import static com.lmig.forge.bamboo.specs.patterns.DeploymentAddOn.CONFIGURE_IDP;
import static com.lmig.forge.bamboo.specs.patterns.DeploymentAddOn.STANDARD_CHANGE;
import static forge.PipelineParameters.PIPELINE_CONFIGURATION;

import com.lmig.forge.bamboo.specs.components.executables.Jdk;
import com.atlassian.bamboo.specs.api.BambooSpec;
import com.lmig.forge.bamboo.specs.patterns.AddOns;
import com.lmig.forge.bamboo.specs.patterns.build.maven.MavenAppBuild;
import com.lmig.forge.bamboo.specs.patterns.deployment.cloudfoundry.CloudFoundryBlueGreenFileDeployment;

@BambooSpec
public class Pipeline {

  private static final AddOns ADD_ONS = new AddOns()
  .buildAddOns(
     /* Add an available build add-on here */
  )
  .deploymentAddOns(
    /* Add an available deployment add-on here */
  );

  public static void main(String[] args) {
    /**
     * BuildPattern: MavenAppBuild
     *
     * For additional information see https://docs.forge.lmig.com/articles/specs/patterns/build/maven/mavenappbuild
     */
    new MavenAppBuild(PIPELINE_CONFIGURATION)
        .disableAutoPublishingOfDeploymentConfiguration()
        .jdkVersion(Jdk.JDK_17)
        .addOns(ADD_ONS)
        .disableAddOns(SONARQUBE)
        .publish();

    /**
     * DeploymentPattern: CloudFoundryBlueGreenFileDeployment
     *
     * For additional information see https://docs.forge.lmig.com/articles/specs/patterns/deployment/cloudfoundry/cloudfoundrybluegreenfiledeployment
     */
    new CloudFoundryBlueGreenFileDeployment(PIPELINE_CONFIGURATION)
        .addOns(ADD_ONS)
        .autoDeployAfterSuccessfulBuild()
        .publish();
  }
}
