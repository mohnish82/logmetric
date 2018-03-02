# LogMetric  

A command line utility to load log file into a database and allowing querying on that data. It reads pipe(|) delimited access log files in the following format.

```
2017-01-01 00:00:11.763|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"
2017-01-01 00:00:21.164|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"
```

## Config

Project configuration can be changed by modifying the `application.properties` file under `src/main/resources`. Default configuration is set to talk to local MySql/MariaDB instance, on default 3306 port. The database schema file also exists under the same folder. The database needs to be setup with the schema specified in the schema file prior to running the app.


## Build

To obtain an executable JAR file, use the following. This generates the executable JAR and places it under `build/libs`.

```sh
$ ./gradlew ean build
```

## Usage

Application expects the following command line arguments to provide the metric, 'source ip address that accessed system a certain number of tinmes during an interval. The following 3 input arguments control the output.

- `startDate` (required) - Start date to consider while searching for log entries. 
- `duration` (required) - Duration from the start date. This is a string (either "hourly" or "daily") to indicate the relative duration from start date.
- `threshold` (required) - Number of log entries for an IP address source.
- `accesslog` (optional) - Absolute/relative path to the access log file. This is not a required argument, but if provided, the program would first load all the entries into the database and then perform the search as per the other arguments.

E.g.

1. To find IP addresses with more than 250 entries in an hour, since 2017-01-01.15:00:00.

   `java -jar logmetric.jar -startDate=2017-01-01.15:00:00 --duration=hourly --threshold=250`


## Run

1. To upload log file into the database, and then perform search on that data.

```sh
$ java -jar logmetric.jar --accesslog=../some_access_log_file.log \ 
	-startDate=2017-01-01.15:00:00 --duration=hourly --threshold=250
```

2. To search log data already uploaded to database.

```sh
$ java -jar logmetric.jar -startDate=2017-01-01.15:00:00 \ 
	--duration=hourly --threshold=250

```

## Sample SQL

1. IP address from which more than certain number of requests originated in a given time period.

```sql
select source_ip from logs where access_time > '2017-01-01 00:00:00' and access_time < '2017-01-01 00:10:00' group by source_ip having count(*) > 5;
```

2. Requests made by a certain IP address.

```sql
select source_ip, count(*) count from logs where source_ip='192.168.169.194' and access_time > '2017-01-01 00:00:00' and access_time < '2017-01-01 00:10:00';
```
