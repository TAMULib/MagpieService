<a name="readme-top"></a>
# Metadata Assignment GUI Providing Ingest and Export Service Deployment Guide

## Production Deployments

For **production** deployments, deploy using `docker-compose` via the [MAGPIE App Repo][app-repo].
This is the recommended method of deployment for production systems.
Go to the [MAGPIE App Repo][app-repo] and following the deployment instructions there.

Performing the deployment using the [MAGPIE App Repo][app-repo] should be something similar to the following:
```shell
docker-compose up
```

The **development** deployment can also use `docker-compose` in the same way.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Development Deployment using Docker

To manually use `docker` rather than `docker-compose`, run the following:

```shell
docker image build -t magpieservice .
docker run -it magpieservice
```

<sub>_* Note: `-t magpieservice` and `-it magpieservice` may be changed to another tag name as desired, such as `-t developing_on_this` and `-it developing_on_this`._</sub><br>

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Development Deployment using Maven

Manual deployment can be summed up by running:

```shell
mvn spring-boot:run
```

Those steps are a great way to start but they also fail to explain the customization that is often needed.
There are multiple ways to further configure this for deployment to better meet the desired requirements.

It is highly recommended only to perform *manual installation* when developing.
For **production** deployment, please use `docker-compose` via the [MAGPIE App Repo][app-repo] or use the **Docker** method above.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


### Directly Configuring the `src/main/resources/application.yml` File

This method of configuration works by altering the configuration file.

With this in mind, the deployment steps now look like:

```shell
# Edit 'src/main/resources/application.yml' here.

mvn spring-boot:run
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>


<!-- LINKS -->
[app-repo]: https://github.com/TAMULib/MAGPIE
