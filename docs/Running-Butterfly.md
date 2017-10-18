
# Running Butterfly

Butterfly can be run by its CLI tool.

Run `butterfly` in an Unix based system, or `butterfly.cmd` in Windows, to show its usage and the various command line options. See in the example below how to run Butterfly in Linux.

```
butterfly
```

## Transformation execution options

| Option | Argument | Description |
|--------|----------|-------------|
|`-t`|transformation template class name|Sets the Java class name of the transformation template to be executed. This option has precedence over `-s`. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (`-u`) is specified|
|`-s`|transformation template shortcut number|Sets the shortcut number to the transformation template to be executed. If both shortcut (`-s`) and template class (`-t`) name are supplied, the shortcut will be ignored. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (`-u`) is specified|
|`-u`|upgrade version|Sets the version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template. If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible.|

## Application transformation execution examples

### Specifying the transformation template by its class name

Use option `-t` to set the transformation template class to be used, as seen in the example below. Notice that you can list all possible transformation template classes by running `butterfly -l`.

```
butterfly myapp -t com.extensiontest.SampleTransformationTemplate
```

### Specifying the transformation template by a shortcut

Use option `-s` to set the transformation template to be used by its shortcut number, as seen in the example below. Notice that you can list all possible transformation template shortcuts by running `butterfly -l`.

```
butterfly myapp -s 1
```

### Automatic transformation template resolution

Whenever options `-t` and `-s` are not set, then Butterfly will try to resolve automatically which transformation template should be used to transform the application based on its content. See the example below.

```
butterfly myapp
```

### Upgrading an application to a specific framework version

If the chosen transformation template is an upgrade template, use option `-u` to set an specific framework version to upgrade the application to. In the example below, the application framework will be upgraded from version 1.0.0 to version 2.0.0.

```
butterfly myapp -t com.extensiontest.SampleUpgradeTemplate_1_0_0_to_2_0_0 -u 2.0.0
```

### Upgrading an application to the latest framework available version

If the chosen transformation template is an upgrade template and option `-u` is not set, then the application will be automatically upgraded all the way to the latest framework version supported by the extension. In the example below the application framework will be upgraded from version 1.0.0 to version 3.0.0.

```
butterfly myapp -t com.extensiontest.SampleUpgradeTemplate_1_0_0_to_2_0_0
```

## Additional execution options

| Option | Argument | Description |
|--------|----------|-------------|
|`-?` or `-h`|_NA_|Shows help|
|`-l`|_NA_|Lists all registered extensions and their transformation templates|
|`-o`|Path to folder|Sets the folder location in the file system where the transformed application should be placed. If not set, tt defaults to same location where original application is. Transformed application is placed under a new folder whose name is same as original folder, plus _"-transformed-yyyyMMddHHmmssSSS"_ suffix.|
|`-z`|_NA_|The transformation result is the transformed application folder into a zip file|
|`-r`|Path to file (including its name), or just a file name|Creates a result file in JSON format containing details, not about the transformation itself, but about the CLI execution|
|`-v`|_NA_|Runs Butterfly in verbose mode, printing log messages not just in a log file, but also on the console|
|`-d`|_NA_|Runs Butterfly in debug mode|