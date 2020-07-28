# exampleProductTeamName Performance Tests

---

`Inspired from things I have worked on or seen.`

---

Run performance tests using Gatling.

## Structure of the tests

 * /user-files/data contains csv files with the load testing urls.
 * /user-files/simulations contains the scala tests for each app.

Note that for a test to be runnable using the custom `run.sh` script a the package of a test class under /user-files/simulations must match the directory structure from that position and start with `examplePackageName`. For example a test at `/user-files/simulations/examplePackageName/Example1.scala` should have a package of `examplePackageName.examplePackageName`.

## Load test best practices

The following approach is strongly suggested for effective load tests.

* ***Always*** load test off any proxies. Most proxies are not capable of handling the kind of loads we want our apps to handle and so the proxy will fail before the app under test itself.
* ***Always*** test from a large EC2 instance using a test one for example(instructions below). A dedicated EC2 instance will run the load tests and no other service so all the instance's resources can be put into load testing. Running load tests from a development machine at high load will often mean that the development machine and the network are the bottlenecks, rather than the app under test. A compute-optimised instance like `c4.2xlarge` will do the job well.
* When trying to improve performance under load try to use the same load test profile throughout and change one thing at a time between test runs. This will help pin down what effect each change has and identify which one gives the best improvement.

## Gatling

The [Gatling cheat sheet](http://gatling.io/docs/current/cheat-sheet/) is a useful resource when writing load tests.

## Install on an AirBubble/AirLock/Test AWS EC2 instance

 * Zip example-gatling-performance-load-tests project:  ``` zip -r -X example-gatling-performance-load-tests.zip * -x "*/\.*"```
 * Launch an Test EC2 instance
 * Copy example-gatling-performance-load-tests.zip to the EC2 (scp): ``` scp example-gatling-performance-load-tests.zip 1.1.1.1,eu-west-1:~```
 * Login to the EC2
 * Install zip, unzip, java and nano: ``` sudo yum install zip unzip nano java-1.8.0-openjdk-devel -y```
 * Unpack the tests: ``` unzip example-gatling-performance-load-tests.zip -d example-gatling-performance-load-tests```


## Run

Use the runner script:

    ./run.sh <test-class-name>

e.g.

    $ ./run.sh Example1Simulation1

It also supports partial matching of class names so its possible to run multiple tests synchronously. For example the following will run all Example1 tests one after the other.

    $ ./run.sh Example1

Alternatively use the `gatling` script directly:
```
./bin/gatling.sh
```

## Change settings

You can use nano/vi/vim on the scala configuration files:

```
find ./user-files -name "*.scala"

```
## Local Setup

If you intend to run Gatling on your laptop, you may need to edit `conf/gatling.conf` with a valid trustStore and keyStore path & password.

```
ssl {
  trustStore {
    file = "/path/to/example.jks"
    password = "<password>"
  }
  keyStore {
    type = "PKCS12"
    file = "/path/to/example.p12"
    password = "<password>"
  }
}
```

If you're working behind a proxy you'll need to add some config to your HTTP request declaration, e.g.:

```
val httpProtocol = http.baseURL("https://exmample.api.com").proxy(Proxy("www-cache.example.com", 80))
```

## Specifics

### Theme Api

If you intend to load test the /theme endpoint by id you will need to prepare some test data:

* Run ExampleThemeConnector to retrieve the list of ids from the topic-connector search api. This will save it in /var/tmp/theme-connector-topicmatter-load-testing.csv.
* Then run ExampleThemeCreate to create a list of themes for each of these topicmatters. This will save it in /var/tmp/theme-db-load-testing.csv. Try ```wc -l /var/tmp/theme-connector-topicmatter--load-testing.csv``` and make sure users x seconds < topicmatter count.
* Then you can load test the theme-api /theme endpoint.
* Once all finished, please delete test data from DataBase.
