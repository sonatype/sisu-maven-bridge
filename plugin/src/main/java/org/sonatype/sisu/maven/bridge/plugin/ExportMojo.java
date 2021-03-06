/*
 * Copyright (c) 2009-2012 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 */
package org.sonatype.sisu.maven.bridge.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Repository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @goal export
 * @phase process-test-resources
 * @requiresDependencyResolution test
 */
public class ExportMojo
    extends AbstractMojo
{

  /**
   * @parameter default-value="${session}"
   * @required
   * @readonly
   */
  private MavenSession session;

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * @parameter expression="${project.build.directory}/test-classes/injected-test.properties"
   */
  private File propertiesFile;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if ("pom".equals(project.getPackaging())) {
      return;
    }

    final Properties properties = eventuallyReadExistingProperties();

    setIfNotPresent(properties, "mavenbridge.localRepository", new File(session.getSettings().getLocalRepository()));
    setIfNotPresent(properties, "mavenbridge.repositories", getRepositories());
    setIfNotPresent(properties, "mavenbridge.globalSettings", session.getRequest().getGlobalSettingsFile());
    setIfNotPresent(properties, "mavenbridge.userSettings", session.getRequest().getUserSettingsFile());
    setIfNotPresent(properties, "mavenbridge.checksumPolicy", session.getRequest().getGlobalChecksumPolicy());
    setIfNotPresent(properties, "mavenbridge.offline", String.valueOf(session.isOffline()));
    setIfNotPresent(properties, "mavenbridge.profiles", getProfiles());

    writeProperties(properties);
  }

  private String getProfiles() {
    final List<String> profiles = session.getRequest().getActiveProfiles();
    if (profiles != null && !profiles.isEmpty()) {
      return StringUtils.join(session.getRequest().getActiveProfiles().iterator(), ",");
    }
    return null;
  }

  private String getRepositories() {
    final List<Repository> repositories = project.getRepositories();
    if (repositories != null && !repositories.isEmpty()) {
      final StringBuilder sb = new StringBuilder();
      for (final Repository repository : repositories) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(String.format("%s::%s::%s", repository.getId(), repository.getLayout(), repository.getUrl()));
      }
      return sb.toString();
    }
    return null;
  }

  private void setIfNotPresent(final Properties properties, final String key, final String value) {
    // TODO ? shall we not override exiting properties? because if "mvn clean" is not called second run will not
    // overwrite
    if (value != null && !properties.containsKey(key)) {
      properties.setProperty(key, value);
    }
  }

  private void setIfNotPresent(final Properties properties, final String key, final File value) {
    if (value != null) {
      setIfNotPresent(properties, key, value.getAbsolutePath());
    }
  }

  private Properties eventuallyReadExistingProperties() throws MojoFailureException {
    final Properties properties = new Properties();
    FileInputStream in = null;
    try {
      in = new FileInputStream(propertiesFile);
      properties.load(in);
      getLog().debug("Using existing properties file '" + propertiesFile.getAbsolutePath() + "'");
      logProperties(properties);
    }
    catch (final FileNotFoundException e) {
      // no problem, we will make one
    }
    catch (IOException e) {
      throw new MojoFailureException("Could not read provided properties file '" + propertiesFile.getAbsolutePath()
          + "'", e);
    }
    finally {
      IOUtil.close(in);
    }
    return properties;
  }

  private void writeProperties(final Properties properties) throws MojoFailureException {
    FileOutputStream out = null;
    try {
      propertiesFile.getParentFile().mkdirs();
      propertiesFile.createNewFile();

      out = new FileOutputStream(propertiesFile);
      properties.store(out, "Parts generated by sisu-maven-bridge-plugin");
      getLog().info("Properties exported to file '" + propertiesFile.getAbsolutePath() + "'");
      logProperties(properties);
    }
    catch (IOException e) {
      throw new MojoFailureException("Could not write provided properties file '" + propertiesFile.getAbsolutePath()
          + "'", e);
    }
    finally {
      IOUtil.close(out);
    }
  }

  private void logProperties(final Properties properties) {
    if (getLog().isDebugEnabled() && !properties.isEmpty()) {
      getLog().debug("");
      for (Map.Entry<Object, Object> entry : properties.entrySet()) {
        getLog().debug(entry.toString());
      }
    }
  }

}
