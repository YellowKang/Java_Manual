# 基本操作


		Ctrl+L	清空屏幕（清屏）其实还在往上面还是能翻到
		
		clear	清空当前屏幕(与上方清空类似)
	
		shutdown -h now  立即关机
		
		reboot	重启电脑

# 修改服务器名称

## 查看服务器名称

```
uname -n
```

## 修改服务器名称

临时修改

```
hostname bigkang1
```

永久修改

```
vim /etc/hostname

修改为bigkang1
```



# 目录操作


		cd ..  进入到父目录目录中，也就是父文件夹
	
		cd ~   进入到当前操作账号目录中
	
		cd /   进入到根目录中
	
		cd  ./my/jar 	进入到当前目录下面的my下面的jar文件夹
		
		ls系列后面跟上蚕参数目录不需要去木变文件夹即可查询
	
		例如：ls /root
	
		ls 	查看当前文件下面的所有文件夹及文件
	
		ls -l 	以列的方式显示目录也有简洁方式
	
		ll	这就是ls -l的简单方式
	
		ls -lh	将文件的大小以kb，m，g等方式显示（注：不会统计文件夹的大小，只会统计文件的大小）
	
		ls -la	查看当前用户目录下的所有文件例如（root用户）
	
		pwd	显示当前目录


​		

# 时间


			date	显示当前时间  格式化日期
	
				date +%Y年%m月%d日' '%H点%M分%S秒   ------》 2018年10月23日 10点35分32秒
	
				date +%F' '%T			    ------》 2018-10-23 10:36:52
	
			cal	显示当前日历
				
				可以使用cal  --help查看帮助
	
				cal -y				    显示一整年的日历
	
		对文件夹的操作
	
			新建文件夹：mkdir 文件名
	
				例如 ： mkdir kang  这样就新建了一个叫做kang的文件夹
	
			移动：mv  文件名    路径
	
				例如：mv myjar /root/a/myjar
	
				这样我们就将当前的文件移动到了    根目录下的root文件夹下的a文件夹下的叫做myjar的文件

# 文件操作

	查询文件或者子文件夹
	
		ls | grep 名称
	
		例如:
	
			ls | grep kang
	
		就能模糊查询到有kang关键字的文件和文件夹
	
		查看文件夹下面所有的文件
	
			find 文件名
	
		查看所有文件进行筛选
	
			（注意这个是根据索引来搜索的，会自动的在0点的时候更新索引如果虚拟机关了则不刷新，
	
				需要手动刷新才能搜索的到，而且不会给临时文件夹下的文件创建索引）
	
				updatedb
	
			locate 查询条件
	
				locate  my查询所有和my有关的文件和文件夹


​	
​			创建文件：
​				vim 文件名，如果vim没有安装则使用vi
​			例如：
​			vim hello.java，这样就创建了一个叫做hello.java的一个文件，进入之后我们可以在里面写入数据
​						，然后按一下Esc然后:wq保存并退出，更多操作可以百度vim编辑器
​	
				>> 文件名 
					
					例如：
	
						>> hello.java 这样就创建了一个叫做hello.java的文件，
	
		复制文件或文件夹：
	
			文件：
	
				cp  文件  要复制的文件路径     cp kang.txt /root/jar/这样就复制过去了
	
				cat 文件 >  要复制的文件
	
				例如：
					cat hello.txt > nice.txt
	
			文件夹：
				-r是递归复制的意思，v是显示操作过程	
	
				cp -rv 文件夹  要复制的路径	cp -rv kang /root/jar/
	
				\cp,,,,直接执行，不需要询问
					修改文件名：
			
			mv 文件名 新文件名	
			
			例如：
	
				mv kang kangs
	
				这样就将kang文件变成kangs文件了
	
				mv /root/kang /root/
			注：文件夹也可以修改
	
			移动文件并重命名
	
				mv  当前文件路径 新文件路径
	
				mv  /root/kang /root/as/kangs
	
		合并文件：
	
			cat 文件一  文件二 > 文件三
	
			例如：
				
				cat hello1.txt hello2.txt > hello3.txt
		查看文件：
			查看文件大小：
	
				查看所有的文件和目录不推荐使用
				du -h /目录或者文件的大小
			（推荐使用）
				du -ach 包含文件，带计量单位，列出明细并且统计
			vim 文件名（前提是有这个文件否则他会创建这个文件）
			vim  
	
			Esc :set nu显示行数
			例如：
				vim 文件名，进入之后就可以对这个文件进行查看了，注意修改之后按Esc然后:wq退出并保存修改
				cat 文件名就可以查看文件了
	
				例如：
					car  hello.java，就能查看当前目录下的叫做hello.java的文件了
	
			查看文件：
	
				more 文件名
	
				less 文件名   进入后按q退出
			查看多个文件：
				cat 文件1  文件2
	
				例如：
					cat kang kang1
	
				tail 文件名
					从尾部查看
	
				tail -f 文件名
					从尾部查看，并且不退出
	
				tail -n200
					从尾部查看多少行
			删除
	
				rm -rvf 文件名
	
					删除并显示文件名
	
				rm 文件名  和  rm -rf 文件名   的区别是什么？
	
					rm 文件名   他会询问是否删除，需要手动输入yes
	
					rm -rf /*   表示固定删除不会去询问是否删除
		删库跑路大典：rm -rf /*
	
		（注意，慎重使用----------------）
	
			他会将所有的文件都删除清空掉，这也是所谓的删库跑路，和格式化一样，（包括系统文件）
			不过一般的公司提供的服务器账户一般都没有这个权限

# 查询命令手册


		man   加上操作名，例如   man  ls  ，然后就是手册了，手册下箭头往下翻，q退出
	
				操作名  加上--help   例如   ls  --help
	
			查询操作日志
	
				history
	
			软链接（快捷方式）：
	
				ln -s 目标文件或目录名 新的快捷方式名 


​	

# 解压(压缩)文件


​	
​	
​				（tar解压文件）
​	
					tar -zxvf XXX.tar.gz
	
					解压这个XXX.tar.gz文件
				
				（tar压缩文件）
	
					tar -zcvf XXX.tar.gz   n1.txt
	
					压缩n1.txt这个文件为XXX.tar.gz 
			（zip解压文件）
				
					unzip XXX.zip
	
					解压XXX.zip这个压缩文件
	
				（zip压缩文件）	
						
					zip -r XXX.zip hello.java
	
					压缩hello.java这个文件为XXX.zip这个压缩文件


​	

# 系统命令


			查看磁盘分区情况：
	
				lsblk或者lsblk -f都可以			
				注：新添加硬盘后需要重新启动计算机，然后再进行分区设置
	
				fdisk /dev/sdb
				然后输入m根据帮助然后输入n   new一个分区
				然后会选择你是创建主分区还是其他这里创建主分区并设置分区号
				然后一直向下然后再w一下同步磁盘，这样就创建好了磁盘分区
	
				然后设置格式化分区会自动生成uuid标识
				mkfs -t ext4 /dev/创建的分区，ext4是分区类型
				最后挂载这个分区
				mount /dev/设备名称    挂载目录
	
				取消挂载
				umount /dev/设备名称  
	
				此时分区是创建在内存中的我们还需要将他的信息添加到硬盘之中
	
				修改/etc/fstab实现挂载
	
				vim /etc/fstab
				然后添加一行
	
				/dev/设备名 	/挂载目录     ext4  default 00
	
				添加完成后mount -a刷新生效 
	
			查询磁盘使用率
	
				df -h
	
			在虚拟机中设置网络ip
	
				打开界面右上角点击账户上面的网络，然后点击小齿轮，
				进入设置ipv4设置他为一个固定的ip，然后子网掩码225.225.225.0
				然后再设置默认子网路由和DNS服务器
	
				一般来说例如192.168.44.1，但是1的端口号已经被自己的虚拟机的主机占用了所以后面设置2避免冲突
	
				例如192.168.44.2
				DNS服务器和子网路由都是一样的，必须设置DNS服务器否则不能联网	
	    如果是修改配置信息的话修改之后需要刷新
	
				service network restart
				查询当前的所有进程
	
				ps -aux
				显示的结果：
	
					PID：进程号
	
					%CPU：进程占用的CUP比率
	
					%MEM：进程占用的物理内存比例
	
					VSZ：进程占用的虚拟内存
	
					RSS：进程占用的物理内存
	
				筛选进程 ：
	
					ps -aux | grep mysql
	
					也可以使用grep来筛选进程
	
				ps -ef显示进程和进程父类id 
	
			杀死进程：
	
				kill 进程号  加上进程号就能把进程杀掉（合法关闭）
	
				kill -9 进程号  强制杀死进程（在进程卡死的情况下无法合法关闭进程）
	
				killall 加上进程名   例如：killall mysql 就能关闭所有的mysql进程
	
			防火墙设置：
	
				停止防火墙
				systemctl stop firewalld.service
	
				开启防火墙
				systemctl start firewalld.service
	
				防火墙状态
				systemctl status firewalld.service		


				开启
				service firewall start
	
				停止
				service firewall stop
			
				查看防火墙是否开启
	
				firewall-cmd --state
	
				（running表示开启，not running表示关闭）
	
			查询所有服务：
	
				systemctl list-unit-files	(| grep firewalld）	筛选条件
	
				systemctl --type service 				按q退出
	
			设置服务关闭开机启动
		
				systemctl disabled 服务名	
	
				例如防火墙：
					
					systemctl disabled firewalld.service
	
			设置服务开启开机启动
	
				例如MySQL开机启动：
	
					systemctl enabled Mysqld
			
			查看磁盘使用情况
	
				df -hl
	
			查看服务的状态：
	
				例如查看MySQl是否启动
	
					systemctl status mysqld.service
	
			查询端口号是否被占用：
				
				查询mysql端口号是否被占用
	
				netstat -anp | grep 3306
				
				-an 按一定的顺序进行排列
	
				-p显示那个进程在调用端口
			查看端口的应用：
	
				lsof -i:端口号		例如我要检查mysql的3306端口是否开启，被谁开启
	
				lsof -i:3306
			开放端口号：
	
				开放8080端口
			
				firewall-cmd --permanent --add-port=8080/tcp
	
				不开放8080端口
				firewall-cmd --permanent --remove-port=8080/tcp
	
				重新加载防火墙（设置或删除之后刷新）
				firewall-cmd --reload
				查看防火墙的开放的端口
				firewall-cmd --permanent --list-ports
				查看防火墙的开放的服务
				firewall-cmd --permanent --list-services

# 用户操作


​	
​	
​				创建用户：
​	
					useradd 新用户名
	
					passwd 新用户名
	
					然后设置密码
	
					例如：kang123456
	
						然后重复密码
	
					然后就设置成功了，这样就成功创建了一个用户
	
					然后检查是否创建了用户
	
						id 行用户名    例如：id kangge
					然后可以切换到新创建的用户了
	
						su - kangge
	
					显示当前用户
	
						whoami
	
					查询由那个用户跳转过来的
	
						who  am  i
			删除用户：
	
				userdel kangge 就能删除了，有时候会删除不掉，这个时候关闭掉服务器，
				重新登录root账户再进去删除就可以了
	
			查询所有用户：
	
				在/etc/passwd目录里面我们可以查看用户
	
				cat /etc/passwd
	
			查询用户的密码的情况：
	
				（注：密码全部都是加密之后的所以不能查看，只能查看操作信息）
	
				cat /etc/shadow 
	
				含义：
	
					登录名：加密口令：最后一次修改时间：最小时间间隔
					：
					最大时间间隔：警告时间：不活动时间：失效时间：标志
			用户组的创建：
	
				groupadd 组名 	groupadd huashan
	
			用户组的删除：
	
				groupdel 组名	groupdel huashan	
	
			将用户拉入组中：
	
				usermod -g 用户组 用户名
			
				usermod -g huashan kangge
	
				然后id 用户名
	
					这样就能查看到他的组了
	
					id kangge
	
				修改组：
	
					usermod -g 用户组  用户名
	
					usermod -g huashan kangge
	
				在增加用户的时候就能添加组
					
					useradd -g 用户组 用户名
	
			查看组信息：
				
				在etc目录下的group文件里面
	
				cat /etc/group  就能查看组信息了

# 权限管理


				查看权限管理：
	
					进入/home目录
	
					cd /home
	
					然后 ll显示所有
	
					d代表用户文件
					
					r读
					w写
					x运行
	
					第一个---
	
					组的 r 读取权限
					组的 w 写权限
					组的 x 运行权限
	
					第二个---
			
					组以外的人的 r 读取权限
					组以外的人的 w 写权限
					组以外的人的 x 运行权限
					drwx --- ---
	
				在文件里的权限分别有
	
					[r]代表可读性（read）：可以读取查看
	
					[w]代表可写（write）：可以修改但是不代表可以删除，删除一个文件的前提条件是对该文件所在的目录有写的权限，才能删除该文件
	
					[x]代表可执行（execute）：可以被系统执行
	
				在目录里的权限
	
					[r]代表可读性（read）：可以读取，ls查看目录内容
	
					[w]代表可写（write）：可以修改，目录内创建+删除+重命名目录
	
					[x]代表可执行（execute）：可以进入该目录
	
				给用户设置权限：
	
					chmod u操作
	
					chmod u(用户user),g(group组的权限),o(其他人的权限)
	
						这个操作可以是 + - = 
						分别代表权限 + 什么权限 - 什么权限 = 什么权限
	
						注：这里的后面的kangge指的是目录
	
					例如
	
						删除权限
	
							chmod u-r,g-r,o-r kangge		这样kangge这个账户就没有了读的权限
							chmod u-w,g-w,o-w kangge 		这样kangge这个账户就没有了写的权限
							chmod u-x,g-x,o-x kangge 		这样kangge这个账户就没有了运行的权限
	
							chmod u=rwx,g=rx,o=x			也能用等于的方式赋值
						增加权限
	
							chmod u+r,g+r,o+r kangge		这样kangge这个账户就有了读的权限
							chmod u+w,g+w,o+w kangge  		这样kangge这个账户就有了写的权限
							chmod u+x,g+x,o+x kangge 		这样kangge这个账户就有了运行的权限
	
						一般来说可以
			
							读权限表示4写权限表示2运行权限表示1
							7=r+w+x,5=r+x,1=x
							chmod u=7,g=5,o=1			还能用数字的方式表示
							这样我创建一个用户给到他的权限就是
					
							chmod 751 bigkang
	
				访问其他人的权限（注：只能在root账户里面进行设置其他组的访问权限）
	
						chown bigkang kangge
	
						这样的话kangge就能访问bigkang这个目录了						

# rpm和yum命令


				rpm（ReadHat Package Manager）
	
				查看安装过的软件
	
					rpm -qa查询所有不推荐
	
					rpm -qa | grep 条件筛选的软件名
				
					例如
	
						rpm -qa | grep mysql   查询有没有安装mysql
	
				卸载软件：
			
					rpm -e 软件名 
				
					例如MySQL
				
						rpm -e mysql
				安装软件：
	
					rpm -ivh rpm包名
	
						-i安装（install）
						-v查看信息
						-h查看进度条
	
				yum（类似于java中的maven，他可以使用远程仓库进行下载）
	
					查看所有的远程的包：
	
						（尽量不要全部查询因为太多了，加上筛选条件会好很多）
	
						yum list | grep mysql
	
					安装程序：
	
						yum install 包名
	
			配置环境：
	
				手动下载安装包并配置环境变量
		
					进入
	
						vim /etc/profile
	
					然后再末尾加上
						注意不要加上空格
						JAVA_HOME=/解压放在那里的目录bin的文件
						例如：
							JAVA_HOME=/opt/jdk1.8.0_152
	
						然后加上
	
							PATH=/opt/jdk1.8.0_152/bin:$PATH
							export JAVA_HOME PATH
							安装完成之后
							source /etc/profile
							重启系统
