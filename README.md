# reread

ReRead is a desktop based/self-hosted RSS feed reader.

It is a self-contained Java JAR file which only requires a Redis instance
with Redis Modules.

The project is a submission entry to [Build on Redis Hackathon](https://hackathons.redislabs.com/hackathons/build-on-redis-hackathon?tab=0)

## Table of Contents

* [Motivation](#motivation)
* [Features](#features)
* [Architecture](#architecture)
  * [Notes on Security](#notes-on-security)
* [Redis modules used](#redis-modules-used)
* [Redis commands usage](#redis-commands-usage)
* [Tech Stack](#tech-stack)
* [Deployment](#deployment)
* [Hacking](#hacking)
  * [Front-end](#front-end)
  * [Back-end](#back-end)
* [Improvement ideas](#improvement-ideas)
* [License](#license)

## Motivation

The motivation to develop `reread` came from my own personal itch. I have 100s of feeds
that I follow but no service currently allows to add all of them in a single free account.

* [Feedly](https://feedly.com)
  - is limited to 100 sources in free plan
* [InoReader](https://inoreader.com)
  - is limited to 150 sources
  - is paid to de-duplicate articles across feeds
* [NewsBlur](https://newsblur.com/)
  - is limited to 64 sources
  - no search
* [FeedSpot](https://www.feedspot.com/)
  - has no search in free plan
* [Winds](https://winds.getstream.io/)
  - only magazine style layout
  - does not allow filtering by read/unread posts
  - search does not works (atleast for me)

And last but not the least: **PRIVACY**. All these sites are ad-supported (which makes sense
for free version), but in paid versions too, they are collecting a lot of analytics about the
user behavior including the reading pattern. ReRead does not collect any information ensuring
privacy.

As I read about the Redis Hackathon I thought it would be a good idea to develop something
that I can use in future as well. And thus was born `reread`.

## Features

ReRead comes with the following features:

* Clean/simple/minimalistic UI
* Discovers RSS/Atom feeds from a given site
* Add RSS/Atom feeds
* Import feeds and folders from an OPML file
* De-dupe posts (with same content) across multiple feeds
* Multiple layouts: cards, masonary, list, magazine
* Sort by oldest/newest posts
* Filter to show only read/unread posts
* Star a post as favorite
* Bookmark a post to read later
* View all star/bookmark posts
* Create new folders
* Categorize feeds into folders
* View statistics as a graph on how frequently a feed posts
* View self reading habits such as how many posts you read every day

## Architecture

ReRead is a classic 3-tiered application, where the web tier connects with the service
tier over REST services. All data persistence happens inside Redis. ReRead includes
an embedded scheduler that 

```
   +-------+              +--------+                     +------------+
   | React |    REST      | Java   |  Redis Protocol     | Redis with |
   |  UI   |------------> | Server |-------------------> | modules    |
   +-------+              +--------+                     +------------+
                              |
                              | Continual
                              | polling          +------------+
                              +----------------> | Feed Hosts |
                                                 +------------+
```

ReRead utilizes continual polling for fetching new posts from RSS/Atom feeds. This is
limited as there is no easy way of receiving webhooks on http://localhost for feeds
with [PubSubHubBub](https://github.com/pubsubhubbub/) enabled. 

The code can however be extended to use tunneling via [ngrok](https://ngrok.com/), 
[localtunnel](https://github.com/localtunnel) or otherwise to make use of PubSubHubBub
to reduce polling. **Note**: Not all feeds/sites support hooks and thus polling may
still be required.

### Notes on Security

ReRead does not use authentication on purpose. It is intended for power users who
subscribe of hundreds/thousands of feeds and would like to run it locally or self
host.

In case of self-hosting, it is preferable to add IP-based restrictions along with
BASIC authentication using a front-proxy like [Nginx](https://www.nginx.com/) or 
[Caddy Server](https://caddyserver.com/) that will also enable a [Let's Encrypt](https://letsencrypt.org/)
based SSL certificate.

## Redis modules used

The project uses the following Redis modules for its functionality:

* [Redis Core](https://redis.io) - used to check existence of keys and for other minor 
operations based on keys.

* [RedisJSON](https://redislabs.com/redis-enterprise/redis-json/) - this serves as the document
store for us. All post data, details on feeds, when they were crawled etc is stored as JSON documents.
This allows us fine grained atomic operations such as updating a single field within the document
than read/writing the entire document again.

* [RediSearch](https://redislabs.com/redis-enterprise/redis-search/) - this is used to index
and search across all posts. The full-text search built into the UI is powered by it.

* [RedisTimeSeries](https://redislabs.com/modules/redis-timeseries/) - this is used to store the
activity behavior of user as well as the publishing behavior of the feeds. Dashboards such as number
of posts read per day, per week etc are powered using this module.

* [RedisBloom](https://redislabs.com/modules/redis-bloom/) - the most useful module of all. This helps
us de-duplicate entries not just in the same feed, but also across the board. Thus if you have read the
post once, why read it again?

## Redis commands usage



## Tech Stack

* [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* [Spring Boot](https://spring.io/projects/spring-boot)
* Redis core and enterprise modules (via Redis docker image)
* [Typescript](https://www.typescriptlang.org/)
* [React 17](https://reactjs.org/)
* [Bootstrap 5.0](https://getbootstrap.com/)
* [Apache Maven](https://maven.apache.org/) (for building server)
* [NPM](https://www.npmjs.com/)/[Yarn](https://yarnpkg.com/) (for building client)

See [pom.xml] and [package.json] file for a list of all 3rd-party libraries used.

## Deployment

You will need [Redis docker image](https://developer.redislabs.com/explore/redismod/) for using
Redis enterprise modules. Go over this link to have it ready.

**TL;DR:** A published JAR is also available for download from Github. You can skip building
the project and directly use it to have `reread` running on your local machine.

To build and deploy the project, follow the below instructions:

```sh
# create the build for the web client
$ cd web-ui
$ npm install
$ npm run build
$ cd ..

# create the build for the server
$ cd server
$ cp -r ../web-ui/dist/* src/main/resources/static
$ mvn clean package
$ cd..

# Start the docker container
$ docker run -d -p 6379:6379 redislabs/redismd

# Start the project
$ java -jar server/target/reread.jar
```

You can now access the application at http://localhost:1309

## Hacking

`reread` is 100% hackable from the word **go**. 

### Front-end

The front-end application is written in 100% Typescript and uses React
underneath. To make changes to the UI client:

```sh
# Install dependencies
$ cd web-ui
$ npm install (or yarn install)
$ npm run watch
```

This shall start a local development server on http://localhost:1234 where the React
application is continuously built and deployed. Open your favorite editor like 
[VSCode](https://code.visualstudio.com/), [Atom](https://atom.io/) or 
[Sublime Text](https://www.sublimetext.com/) and start making changes.

### Back-end

The back-end application is written in Java and uses Spring Boot. All custom
beans are defined in `SpringBeans.java` file. All services are wired to their
implementation using the `@Service` annotation. **Javadocs** are mentioned over
the classes as well as methods to indicate what they do. That should help one
get started.

## Improvement ideas

* Improve performance on OPML import - MasterFeed check takes a lot of time
* Allow users to selectively import from OPML
* Add support for podcasts
* Add settings pane to change connection to Redis (currently bound to localhost)
* Add settings to customize layouts, themes, fonts etc
* Use HTTPs proxy to serve images
* Add news sites available in free non-commercial license as [mentioned here](https://jtmuller5-98869.medium.com/replacing-the-google-news-api-with-an-rss-feed-and-jsoup-c351de353479).

## License

Apache License Version 2.0.
