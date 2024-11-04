
# Cloudy with a chance of LLM: Distributed Tokenization and Embeddings Generation with Hadoop and AWS EMR
### Created by: Yashvardhan Udia
### Email: yudia2@uic.edu
### NET ID: 656090513

This Scala project implements a distributed system for tokenization and embeddings generation using Hadoop's MapReduce framework and Sparks training loops for LLM training.
It processes huge text corpus(from Project Gutenburg) to create tokens with frequencies and subsequently generates word embeddings using advanced techniques (like sliding window).

## Overview

- This Scala project leverages the Sparks Training loops to process text data efficiently. 
- The primary focus is on training an LLM. 
- The project uses a pre-generated embeddings.csv file and uses these to train an LLM model using the sliding window technique. 
- This approach is particularly useful in natural language processing (NLP) applications, where large volumes of text data need to be processed and analyzed.

## Project Structure
The main files are listed in below structure
```plaintext
project-root/
├── build.sbt                   # Build configuration
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   ├── MainApp.scala    # Main entry point for the Hadoop jobs
│   │   │   ├── TokenizationMapper.scala # Mapper for tokenization logic
│   │   │   ├── TokenizationReducer.scala # Reducer for aggregating tokens
│   │   │   ├── EmbeddingMapper.scala     # Mapper for generating embeddings
│   │   │   └── EmbeddingReducer.scala     # Reducer for finalizing embeddings
│   │   └── resources/
│   │       └── application.conf         # Configuration settings for the application
│   ├── test/
│   │   └── scala/
│   │       ├── FetchEmbdEdgeCasesSpec.scala # Test suite for TokenizationMapper
│   │       ├── SparkTrainErrorHandlingSpec.scala # Test suite for TokenizationReducer
│   │       ├── WindowedDataSpec.scala     # Test suite for EmbeddingMapper
│   │       └── SparkTrainSpec.scala     # Test suite for EmbeddingReducer
│   │       └── MapReduceSpec.scala
│   │       └── FetchEmbdSpec.scala
```
## Features


## Building and Running

### Requirements

- Scala 3.5 
- Hadoop 3.3.6
- Spark 3.5.3
- Java SE Development Kit 11
- SBT (Scala Build Tool) version (1.10.1)
- dl4j 1.0.0-M2.1


## Getting Started

### Running the Application locally

1. **Clone this repository**: ```git clone https://github.com/messicode/Distributed_Systems.git```

2. **Navigate to the root directory**:
~~~
cd /path/to/root/folder/
~~~
3. **Build the Application JAR**: ``` sbt clean compile assembly ```
4. **Run the Application**: Paths are for reference
~~~
spark-submit --class MapReduce --master local[4] "C:\441 Cloud\Project\Cloud LLM\target\scala-2.12\Cloud LLM-assembly-0.1.0-SNAPSHOT.jar" "C:/441 Cloud/Project/Cloud LLM/src/main/resources/input/pg1.txt" "C:/441 Cloud/Project/Cloud LLM/src/main/resources/output" "C:/441 Cloud/Project/Cloud LLM/src/main/resources/output/embeddings.csv"
~~~
5. **Detailed output**: Output abput the scores are displayed in the logs.


## Result

A sample output of trained LLM model's configuration.json file:
```
{
  "backpropType" : "Standard",
  "cacheMode" : "NONE",
  "confs" : [ {
    "cacheMode" : "NONE",
    "dataType" : "FLOAT",
    "epochCount" : 5,
    "iterationCount" : 0,
    "layer" : {
      "@class" : "org.deeplearning4j.nn.conf.layers.DenseLayer",
      "activationFn" : {
        "@class" : "org.nd4j.linalg.activations.impl.ActivationReLU",
        "max" : null,
        "negativeSlope" : null,
        "threshold" : null
      },
      "biasInit" : 0.0,
      "biasUpdater" : null,
      "constraints" : null,
      "gainInit" : 1.0,
      "gradientNormalization" : "None",
      "gradientNormalizationThreshold" : 1.0,
      "hasBias" : true,
      "hasLayerNorm" : false,
      "idropout" : null,
      "iupdater" : {
        "@class" : "org.nd4j.linalg.learning.config.Adam",
        "beta1" : 0.9,
        "beta2" : 0.999,
        "epsilon" : 1.0E-8,
        "learningRate" : "NaN",
        "learningRateSchedule" : {
          "@class" : "org.nd4j.linalg.schedule.ExponentialSchedule",
          "gamma" : 0.96,
          "initialValue" : 0.01,
          "scheduleType" : "EPOCH"
        }
      },
      "layerName" : "layer0",
      "nin" : 400,
      "nout" : 100,
      "regularization" : [ ],
      "regularizationBias" : [ ],
      "timeDistributedFormat" : null,
      "weightInitFn" : {
        "@class" : "org.deeplearning4j.nn.weights.WeightInitXavier"
      },
      "weightNoise" : null
    },
    "maxNumLineSearchIterations" : 5,
    "miniBatch" : true,
    "minimize" : true,
    "optimizationAlgo" : "STOCHASTIC_GRADIENT_DESCENT",
    "seed" : 42,
    "stepFunction" : null,
    "variables" : [ "W", "b" ]
  }, {
    "cacheMode" : "NONE",
    "dataType" : "FLOAT",
    "epochCount" : 5,
    "iterationCount" : 0,
    "layer" : {
      "@class" : "org.deeplearning4j.nn.conf.layers.OutputLayer",
      "activationFn" : {
        "@class" : "org.nd4j.linalg.activations.impl.ActivationIdentity"
      },
      "biasInit" : 0.0,
      "biasUpdater" : null,
      "constraints" : null,
      "gainInit" : 1.0,
      "gradientNormalization" : "None",
      "gradientNormalizationThreshold" : 1.0,
      "hasBias" : true,
      "idropout" : null,
      "iupdater" : {
        "@class" : "org.nd4j.linalg.learning.config.Adam",
        "beta1" : 0.9,
        "beta2" : 0.999,
        "epsilon" : 1.0E-8,
        "learningRate" : "NaN",
        "learningRateSchedule" : {
          "@class" : "org.nd4j.linalg.schedule.ExponentialSchedule",
          "gamma" : 0.96,
          "initialValue" : 0.01,
          "scheduleType" : "EPOCH"
        }
      },
      "layerName" : "layer1",
      "lossFn" : {
        "@class" : "org.nd4j.linalg.lossfunctions.impl.LossMSE"
      },
      "nin" : 100,
      "nout" : 100,
      "regularization" : [ ],
      "regularizationBias" : [ ],
      "timeDistributedFormat" : null,
      "weightInitFn" : {
        "@class" : "org.deeplearning4j.nn.weights.WeightInitXavier"
      },
      "weightNoise" : null
    },
    "maxNumLineSearchIterations" : 5,
    "miniBatch" : true,
    "minimize" : true,
    "optimizationAlgo" : "STOCHASTIC_GRADIENT_DESCENT",
    "seed" : 42,
    "stepFunction" : null,
    "variables" : [ "W", "b" ]
  } ],
  "dataType" : "FLOAT",
  "epochCount" : 5,
  "inferenceWorkspaceMode" : "ENABLED",
  "inputPreProcessors" : { },
  "iterationCount" : 5,
  "tbpttBackLength" : 20,
  "tbpttFwdLength" : 20,
  "trainingWorkspaceMode" : "ENABLED",
  "validateOutputLayerConfig" : true
}
```





## Contributing
Contributions are welcome! Please fork the repository and submit pull requests with any enhancements. Ensure to add unit tests for new features.

## License

This project is licensed under the [MIT License](https://github.com/messicode/Distributed_Systems/blob/master/LICENSE.txt). Feel free to use, modify, and distribute it as per the license terms.

## YOUTUBE DEMO (Coming soon)
[My video]()
## NOTE

- This project was successfully run on Windows 10 using command line
