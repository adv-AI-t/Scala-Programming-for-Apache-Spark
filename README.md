# 📊 Structured Streaming Word Count with Apache Spark

This project implements a real-time word count using Apache Spark's Structured Streaming and a simple socket source. Words are streamed via a port (9999), and the application processes and counts them live.

---

## 🛠️ Setup Instructions with Bash Commands

### 1. Download and Extract Apache Spark
```bash
wget https://archive.apache.org/dist/spark/spark-3.5.3/spark-3.5.3-bin-hadoop3.tgz
tar -xvzf spark-3.5.3-bin-hadoop3.tgz
mv spark-3.5.3-bin-hadoop3 ~/Spark/
```
_**Explanation**: Downloads Spark 3.5.3 with Hadoop 3 and sets it up in the home directory._

---

### 2. Launch Spark Shell or Run Your Scala Code
```bash
cd ~/Spark/spark-3.5.3-bin-hadoop3/bin
```
_**Explanation**: Opens the interactive Spark shell with Scala for testing._

---

### 3. Enter scala code
```bash
./spark-shell
```

After this, if everything is right, you should see this:
```
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.5.3
      /_/
         
Using Scala version 2.12.18 (Java HotSpot(TM) 64-Bit Server VM, Java 22.0.1)
Type in expressions to have them evaluated.
Type :help for more information.

scala> 
```


Open the spark-shell, then paste the contents of `wordcount.scala` directly into it. Then press enter.

_**Explanation**: Launch the Spark shell, paste your code into the interactive prompt, and press Enter to execute._

---


### 4. Expose Port 9999 Using `nc` (Netcat)
```bash
nc -lk 9999
```
_**Note**: Run this command in a new terminal, not in the spark-shell._
_**Explanation**: Listens for incoming text on port 9999._

---

### 5. Run
```bash
WordCount.main(Array())
```



## 🌐 Sample Input and Output

### Input via Netcat:
```bash
advait
joshi
hi
mr
joshi
advait
joshi
hi
hello
world
```

### Console Output (Streaming Batches):
```
-------------------------------------------
Batch: 1
-------------------------------------------
+------+-----+
| value|count|
+------+-----+
|advait| 1|
+------+-----+
.
.
.
-----------------------------------------
Batch: 10
-----------------------------------------
+------+-----+
| value|count|
+------+-----+
|advait| 2|
| hello| 1|
| mr| 1|
| world| 1|
| hi| 2|
| joshi| 3|
+------+-----+
```

---
## 📜 Code Explanation

```scala
val spark = SparkSession.builder.appName("wordCount").master("local[*]").getOrCreate()
val lines = spark.readStream.format("socket").option("host", "localhost").option("port", 9999).load()
import spark.implicits._
val words = lines.as[String].flatMap(_.split(" "))
val wordCount = words.groupBy("value").count()
val query = wordCount.writeStream.outputMode("complete").format("console").start()
query.awaitTermination()
```

- **SparkSession**: Main entry point to use Spark functionality.
- **readStream**: Reads streaming data from a TCP socket (localhost:9999).
- **flatMap + split**: Splits lines into words.
- **groupBy + count**: Groups words and counts occurrences.
- **writeStream**: Outputs the result to the console.
