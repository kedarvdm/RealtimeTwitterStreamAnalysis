raw_data = LOAD 'hbase://twitteruser' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('stat:poststilldate', '-loadKey true') AS (tweetid: bytearray, statusCount: chararray);

orderedData = ORDER raw_data BY statusCount DESC;

usersHighestStatusCount = LIMIT orderedData 20;

DUMP usersHighestStatusCount;

 STORE usersHighestStatusCount INTO '/Users/rishikaidnani/Desktop/HighestStatusCount';
