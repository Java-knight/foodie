
############################# docker-engine安装 ####################################
# 安装 yum-utils
sudo yum install -y yum-utils
# yum配置更新docker仓库
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
# 安装下载docker-engine
sudo yum install -y docker-ce

################################ redis安装 ###############################
# 下载redis.conf脚本
wget http://download.redis.io/redis-stable/redis.conf
# docker安装 redis 的脚本(-v挂载文件、-d后台进程、-p通信端口、--name取别名)
docker run -d --rm -p 6379:6379 \
      --name red_srv \
      -v /usr/local/redis/data:/data \
      -v /usr/local/redis/redis.conf:/etc/redis/redis.conf \
      redis \
      redis-server /etc/redis/redis.conf