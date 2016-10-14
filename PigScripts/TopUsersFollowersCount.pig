raw_data = LOAD 'hbase://twitteruser' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('stat:followerscount', '-loadKey true') AS (tweetid: bytearray, followersCount:chararray);

orderedData = ORDER raw_data BY followersCount DESC;

topTwentyUsers = LIMIT orderedData 20;

DUMP topTwentyUsers;

STORE topTwentyUsers INTO '/Users/rishikaidnani/Desktop/TopFollowersCount';
