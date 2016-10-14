Project set up instructions:

1. Install Hadoop, HBase, Spark and PIG
2. In project ‘TweetStreamToHBase’
a. Go to Object ‘HBaseUtils’ and edit the path to hbase-site.xml of your own machine
b. Go to ‘TwitterStreaming’ class and edit the paths to paths compatible with your machine
3. In project ‘RealTimeTwitterStreaming’
a. Go to ‘routes’ and edit ‘/Visualization/*file’ path to your own
b. Go to ‘SparkReduce’ class and edit paths to that of your own machine
Project is all set to be run.


Project run instructions:

1. Run project ‘TweetStreamToHBase’ on one port (for example, 8900). Below command can be
used:
Command - ./activator “run 8900”
This will start the streaming where tweet go to HBase
2. Run project ‘RealTimeTwitterStreaming’ on another port (for example, 8901). Below command
can be used:
Command - ./activator “run 8901”
This has all the analysis. Click on any analysis and you will see the visualization of the analysis.