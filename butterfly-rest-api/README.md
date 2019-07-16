# Butterfly REST API

This project contains Butterfly REST API, defined as JAX-RS Swagger/OpenAPI annotated code.

#### OpenAPI YAML file

The JAX-RS code is used to generate automatically an **OpenAPI YAML file**, which can be found under `build/api` folder.

Run Gradle `resolve` task to generate it.

#### API document

An interactive API document (as a web application) can be generated from the generated API YAML file. Run Gradle `resolve`, and then `generateSwaggerUI` task to generate it.

The website files can be found at `build/swagger-ui-butterfly`.

#### API document Docker image

A Docker image, containing the API document web application, can be generated using Gradle `docker` task.

A container can be run with the following command:

```
docker run -p8080:80 butterfly-rest-doc:<tag>
```

The tag can be found by running:

```
docker images | grep butterfly-rest-doc
```

Open then a browser at http://localhost:8080 to see the REST API document.

The OpenAPI JSON file can also be accessed at http://localhost:8080/api.js.