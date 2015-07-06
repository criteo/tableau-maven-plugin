# Tableau Maven Plugin
Integrates the Tableau Parser into Maven.

## Usage
### pom.xml
The plugin should be added to the build plugins in your `pom.xml`, like so:
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.criteo.tableau</groupId>
        <artifactId>tableau-maven-plugin</artifactId>
        <version>${tableau-maven-plugin.version}</version>
        <extensions>true</extensions> <!-- Adds the custom goals defined by the plugin-->
        <configuration>
          <replacementMap> <!-- required configuration key -->
            <dbname>${template.dbname}</dbname>
            <odbcextra>${template.odbcextra}</odbcextra>
            <port>${template.port}</port>
            <server>${template.server}</server>
            <username>${template.username}</username>
          </replacementMap>
        </configuration>
      </plugin>
    </plugins>
  </build>
```


### Goals
After a fresh pull from git:
```bash
mvn untemplate
```

To check complexity and compliance against rules:
```bash
mvn check
```

Before a push into git:
```bash
mvn template
```

## Configuration
Here are the configuration keys available to configure the plugin:

Goal | Key | Description | Required | Default value
-------| --- | ----------- | -------- | -------------
all | `workbookSourcePath` | Location of the workbook files in the project. | false | `workbooks`
`check` | `skippedRules` | Class names of the rules to skip during the check (See [Checking Workbooks](#checking-workbooks)). | false | `{}`
`check` | `maxDashboards` | Maximum number of dashboards before failing the check. | false | 3
`check` | `maxFileSize` | Maximum file size, in bytes, before failing the check. | false | 3 * 1024 * 1024
`check` | `validConnectionClasses` | Valid connection classes, any other connection class will fail the check. | false | `{"vertica"}`
`check` | `maximumQuickFiltersPerDashboard` | Maximum number of quick filter per dashboard before failing the check. | false | 3
`check` | `maximumSheetsPerDashboard` | Maximum number of sheets per dashboard before failing the check. | false | 5
`untemplate` | `replacementMap` | Key-value map, where the key will be replaced by the value (See [Untemplating](#untemplating)). | **true** | (none)

### Checking Workbooks
By default all rules are enabled, but any of them can be disabled by adding them to `skippedRules`:

Rule name | Description
--------- | -----------
`MaxFileSize` | Workbook files should be less than `maxFileSize` bytes.
`MaximumDashboards` | Workbookd should have less than `maxDashboards` dashboards.
`NoExtract` | Workbooks should not contain any extract.
`ConnectionsValidClass` | All connections in a workbook should be in `validConnectionClasses`.
`DashboardsHaveFixedSize` | All dashboards should have a fixed size.
`NoVisibleSheet` | All sheets should be hidden.
`NoUnusedSheet` | All sheets should be used in a dashboard.
`MaximumQuickFiltersPerDashboard` | Each dashboard should have less than `maximumQuickFiltersPerDashboard` quick filters.
`MaximumSheetsPerDashboard` | Each dashboard should have less than `maximumSheetsPerDashboard` sheets.

For instance, to skip the `NoExtract` and `ConnectionsValidClass` and rules,
the plugin's configuration will look like the following:
```xml
      <plugin>
        <groupId>com.criteo.tableau</groupId>
        <artifactId>tableau-maven-plugin</artifactId>
        <version>${tableau-maven-plugin.version}</version>
        <extensions>true</extensions>
        <configuration>
          <skippedRules>
            <rule>NoExtract</rule>
            <rule>ConnectionsValidClass</rule>
          </skippedRules>
          ...
        </configuration>
      </plugin>
```

### Untemplating
As seen previously, the `replacementMap` configuration key is required.

It consists of a map, that should contain the following keys:
* `dbname`, the database name of the connection.
* `odbcextra`, any extra options to the ODBC connection string.
* `port`, the port of the connection.
* `server`, the server url of the connection.
* `username`, the username to use for the connection

Any key missing will not be untemplated.

## Requirements
* Maven >= 3.2.2
