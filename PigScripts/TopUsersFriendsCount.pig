raw_data = LOAD 'hbase://twitteruser' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('stat:friendscount', '-loadKey true') AS (tweetid: bytearray, friendsCount:chararray);

orderedData = ORDER raw_data BY friendsCount DESC;

topTwentyUsers = LIMIT orderedData 20;

DUMP topTwentyUsers;

STORE topTwentyUsers INTO '/Users/rishikaidnani/Desktop/TopFriendsCount';
