package io.ingestr.mavenplugin;


import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Properties;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;


@Mojo(name = "compile", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.COMPILE)
public class CompileMojo extends AbstractMojo {
    @Component
    private MavenProject mavenProject;

    @Component
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Executing Ingestr Maven Plugin 'compile' goal");
        executeMojo(
                plugin(
                        groupId("org.apache.maven.plugins"),
                        artifactId("maven-compiler-plugin"),
                        version("3.8.1")
                ),
                goal("compile"),
                configuration(
                        element(name("annotationProcessorPaths"),
                                element(name("path"),
                                        element(name("groupId"), "org.projectlombok"),
                                        element(name("artifactId"), "lombok"),
                                        element(name("version"), mavenProject.getProperties().getProperty("lombok.version"))),
                                element(name("path"),
                                        element(name("groupId"), "io.micronaut"),
                                        element(name("artifactId"), "micronaut-inject-java"),
                                        element(name("version"), mavenProject.getProperties().getProperty("micronaut.version"))),
                                element(name("path"),
                                        element(name("groupId"), "io.micronaut"),
                                        element(name("artifactId"), "micronaut-validation"),
                                        element(name("version"), mavenProject.getProperties().getProperty("micronaut.version"))))
                ),
                executionEnvironment(
                        mavenProject,
                        mavenSession,
                        pluginManager
                )
        );


        getLog().info("Generating Ingestr Build Properties...");

        Properties properties = new Properties();
        properties.setProperty("loader.name", mavenProject.getArtifactId());
        properties.setProperty("loader.group", mavenProject.getGroupId());
        properties.setProperty("loader.version", mavenProject.getVersion());
        properties.setProperty("loader.buildDate", Instant.now().toString());

        try (FileOutputStream fos = new FileOutputStream(new File(mavenProject.getBasedir(), "target/classes/build.properties"))) {
            properties.store(fos, "Ingestr Build Properties");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}
