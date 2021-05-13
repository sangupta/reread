# reread

Desktop based or Self-hosted RSS feed reader.

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
user behavior, there reading pattern etc.

As I read about the Redis Hackathon I thought it would be a good idea to develop something
that I can use in future as well. And thus was born `reread`.

## Technologies Used

* [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* [Spring Boot](https://spring.io/projects/spring-boot)
* Redis core and enterprise modules (via Redis docker image)
* [Typescript](https://www.typescriptlang.org/)
* [React 17](https://reactjs.org/)
* [Bootstrap 5.0](https://getbootstrap.com/)
* [Apache Maven](https://maven.apache.org/) (for building server)
* [NPM](https://www.npmjs.com/)/[Yarn](https://yarnpkg.com/) (for building client)

## Redis modules used

The project uses the following Redis modules for its functionality:

* Redis Core - used to check existence of keys and for other minor operations based on keys.

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
$ java -jar server/target/reread-1.0.jar
```

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

## Improvement items

* Improve performance on OPML import - MasterFeed check takes a lot of time
* Allow users to selectively import from OPML
* Add settings pane to change connection to Redis (currently bound to localhost)
* Add settings to customize layouts, themes, fonts etc
* Use HTTPs proxy to serve images
* Add news sites available in free non-commercial license as [mentioned here](https://jtmuller5-98869.medium.com/replacing-the-google-news-api-with-an-rss-feed-and-jsoup-c351de353479).

## License

Apache License Version 2.0.
