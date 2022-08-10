<a name="readme-top"></a>
# Deployment Guide

## Production Deployments

For **production** deployments, deploy using `docker-compose` via the [MyLibrary App][app-repo].
This is the recommended method of deployment for production systems.
Go to the [MyLibrary App][app-repo] and following the deployment instructions there.

Performing the deployment using the [MyLibrary App][app-repo] should be something similar to the following:
```shell
docker-compose up
```

The **development** deployment can also use `docker-compose` in the same way.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Development Deployment using **Docker**

To manually use `docker` rather than `docker-compose`, run the following:

```shell
docker image build -t service .
docker run -it service
```

```
docker build --help
# -t, --tag list                Name and optionally a tag in the
```

```
docker run --help
# -i, --interactive             Keep STDIN open even if not attached.
# -t, --tty                     Allocate a pseudo-TTY
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>

## Development Deployment using **Maven**

Running locally is easiest using spring boot:

```shell
mvn spring-boot:run
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>

<!-- LINKS -->
[app-repo]: https://github.com/TAMULib/MyLibrary