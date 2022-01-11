# Ingestr Maven Plugin

This is the Maven plugin to help build, run and deploy Ingestr Loaders built using the Ingestr framework.

## Documentation

To read the full documentation about the Ingestr framework go to the [Documentation](https://ingestr.io/docs/latest) on
the Ingestr.io website.

### Running integration tests

You can run integration tests by executing `mvn verify -Prun-its`

If you want to run individual tests, you can execute `mvn verify -Prun-its "-Dinvoker.test=dockerfile*"`. In this case,
`dockerfile*` will match all test projects under `src/it` folder with a name that starts with "dockerfile".

### Debugging

To debug the plugin, you first need to publish a snapshot to your Maven local:

```shell
$ mvn install
```

Then you need a sample application. The one at `examples/java` is the most up-to-date, but you can in principle generate
a new one from Micronaut Starter. Then, change its `pom.xml` to set the following property:

```xml

<micronaut-maven-plugin.version>1.1.5-SNAPSHOT</micronaut-maven-plugin.version>
```

Pointing to whatever snapshot version you published before.

Finally, from the sample application folder, run the Maven goal you are interested into with `mvnDebug` instead
of `mvn`:

```shell
mvnDebug package -Dpackaging=docker
```

Then in your IDE, attach a remote debugger to port 8000.