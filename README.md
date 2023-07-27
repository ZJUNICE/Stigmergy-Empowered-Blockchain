# SEB Framework

This is the simulation source code for paper "Secure and Efficient Stigmergy-Empowered Blockchain Framework for Heterogeneous Collaborative Services in the Internet of Vehicles". In this simulation, we implement four experiments, termed as Experiments A, B, C, and D (i.e., transaction sorting performance for smart contracts, system security from the perspective of chain length, computational efficiency of the transaction selection algorithm, and throughput in terms of transactions).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
The code is run and tested with JDK8 on linux and windows.\
The experiment config file located at src/main/java/util/Config. There are several attributes include distributionType, transactionPairs, transactionTypeNum etc.

## In Linux

### Prerequisites
We build and run a docker container for experiment.\
Firstly cloning the program into local address.
Then Entering the code folder,
and create a docker image for Simulation with (do not forget the dot at the end of command):

```
docker build -f ./docker/Dockerfile -t seb-simulation:v1 .
```


### Run Image

Running the image which was created to create a container for simulation:

```
docker run -it -d seb-simulation:v1
```

Then entering the simulation container with:

```
docker exec -it  `docker ps | grep seb-simulation:v1 |awk '{print $1}'` /bin/bash
```

## In Windows

### Prerequisites
JDK8 must be installed first.\
Then we recommend using Intellij IDEA to open the code and run the Experiment A,B,C,D.\
It is also feasible to install Docker on Windows, and you can follow the docker image running process mentioned above. 

## Running the simulation
jar packages were built and saved at ./target 

### Running the Experiment A
Run the simulation for Experiment A with:

```
java -cp ./target/SEB-Framework-1.0-SNAPSHOT-jar-with-dependencies.jar ExperimentA
```

The Output Structure is {Distribution Type}_V{Vehicle Number} _{Blockchain Name} _Type _{Type Id} , and the list results means the Average Number of Searches. Given an example below:
```
Gaussian_V5000_SEB_Type_1 = [5,10,12,18,22,29,35,40,46,53,61,62,69,78,79,80,81,92,92,95,103,115,113,123,131,137,141,147,151,159];
Gaussian_V5000_IOTA_Type_1 = [69,129,172,230,287,335,396,432,467,537,587,618,652,746,794,818,853,936,943,995,1073,1145,1222,1268,1284,1381,1463,1437,1495,1564];
```

### Running the Experiment B
Run the simulation for Experiment B with:

```
java -cp ./target/SEB-Framework-1.0-SNAPSHOT-jar-with-dependencies.jar ExperimentB
```

The Output Structure is {Distribution Type}_V{Vehicle Number} _{Blockchain Name} _Orphan_Height , and the list results means the Average Chain Length. Given an example below:
```
Gaussian_V5000_SEB_Orphan_Height = [17,32,43,59,75,88,104,129,147,170,195,210,217,259,262,291,313,323,355,369,386,409,427,468,501,514,541,576,568,610];
Gaussian_V5000_IOTA_Orphan_Height = [18,34,47,59,73,90,107,123,143,159,174,191,203,215,234,247,262,282,295,311,329,347,365,380,393,413,429,444,455,472];
```

### Running the Experiment C
Run the simulation for Experiment C with:

```
java -Xmx8g -cp ./target/SEB-Framework-1.0-SNAPSHOT-jar-with-dependencies.jar ExperimentC
```

The Output Structure is {Distribution Type}_{Transaction Selection Algorithm Name} _Time_Cost , and the list results means the Average Time Cost (milliseconds). Given an example below:
```
Gaussian_TSPS_Time_Cost = [78, 110, 235, 454, 547, 546, 703, 797, 797, 968, 1047, 1063, 1125, 1250, 1328, 1281, 1359, 1406, 1564, 1796, 1749, 1827, 1875, 2000, 2127];
Gaussian_TSA_Time_Cost = [281, 1421, 4703, 11094, 19922, 21125, 22375, 25048, 31360, 41579, 41312, 41718, 44813, 50218, 60985, 60328, 61777, 64958, 72273, 86182, 86344, 87139, 90208, 97194, 107064];
```


### Running the Experiment D
Run the simulation for Experiment D with:

```
java -Xmx8g -cp ./target/SEB-Framework-1.0-SNAPSHOT-jar-with-dependencies.jar ExperimentD
```

The Output Structure is {Distribution Type}_{Transaction Selection Algorithm Name} _{TPS/CTPS} , and the list results means the Tps or Ctps. Given an example below:
```
Gaussian_SEB_TPS = D1_SEB_TPS = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 219, 217, 217, 215];
Gaussian_IOTA_TPS = [10, 20, 30, 40, 50, 60, 70, 80, 90, 93, 93, 93, 94, 94, 94, 95, 94, 94, 94, 95, 94, 94, 94, 94, 93];
Gaussian_IOTA_CTPS = [7, 17, 27, 37, 47, 55, 65, 75, 85, 88, 88, 88, 89, 89, 89, 90, 89, 89, 89, 90, 89, 89, 89, 89, 88];```
```

## Authors

* YUNTAO LIU, DAOXUN LI, *RONGPENG LI, ZHIFENG ZHAO, YONGDONG ZHU, HONGGANG ZHANG

## License

See LICENSE.txt

## Acknowledgments

