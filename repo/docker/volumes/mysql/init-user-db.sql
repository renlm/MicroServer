CREATE DATABASE mygraph;
CREATE USER 'mygraph'@'%' IDENTIFIED WITH caching_sha2_password BY 'KK_fyw4bnmdf4Ksp7IMYuPWD@20stdt^tn50jlsfy';
GRANT ALL PRIVILEGES ON mygraph.* to 'mygraph'@'%';
FLUSH PRIVILEGES;