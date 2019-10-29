# Git基础命令


	查看当前状态：git status 				--查看当前的库里面有没有进行操作例如创建和修改
		举例：
			红色的为在工作区内的操作，add添加到暂存区后变为绿色，最后可以commit提


	删除文件：git rm 文件名					--删除的当前目录下的文件（例如删除kang.txt）
		例如：
			git rm kang.txt
	
	单个添加：git add 文件名				--当对这个文件进行创建和修改之后需要重新添加到暂存区
	批量添加：git add .						
		例如：
					git add kang.txt 添加一个kang.txt到暂存区
					git add .  	 添加所有最近有过改动的文件
	1）单个提交：git commit -m "提交信息" 文件名		--用于提交到当前git资源版本库，然后就能进行远程传输
	2）多个提交：git commit -m "提交信息"			
			举例：
				当提交之后，未对版本库进行操作，再远程交则不会显示改动行数，甚至不会改动
				提交一个this.java
					1)git commit -m "add this.java" this.java
					2)git commit -m "add *"		所有在暂存区的发生过改动的文件
	撤销提交：git rm --cached 文件名			撤销上一步的提交，将暂存区文件放回工作区

# 分支管理


	查看本地分支：git branch				--显示当前库的所有分支
								例如：
									$ git branch
									* master(这是master分支)注：master为默认创建分支
									* kang(这是kang分支)	
	查看远程分支：git branch -r				--显示提交过的远程地址的有的分支
								例如：
									$ git branch -r			
	创建本地分支：git branch 分支名 			--注意新分支创建后不会自动切换为当前分支
	
	创建新分支并立即切换到新分支：git checkout -b 分支名	--注意创建新分支后立即切换到新分支
								例如：
									/f/git/hellos/mywork (master)
									创建一个kang分支则变成
									/f/git/hellos/mywork (kang)
	切换分支：git checkout 分支名				--切换分支所用，对其他分支没有操作影响
								例如：
									/f/git/hellos/mywork (master)
									则变成
									/f/git/hellos/mywork (kang)
	删除分支：git branch -d 分支名				--删除一个分支，对于未有合并的分支是无法删除的。
								   如果想强制删除一个分支，可以使用-D选项
								例如：
									git branch -D kang
	合并分支：git merge 分支名 				--将分支与当前分支合并（注：合并之后不会删除原有分支，需要手动删除）
								例如：
								当前选中master分支f/git/hellos/mywork (master)
								git merge kang 
	修改分支名称：git branch -m 修改后的分支名		--将当前选中的分支的名称改变
								例如：
									原分支为：/f/git/hello (kang)
									git branch -m kangs
									修改后为：/f/git/hello (kangs)			

# 远程仓库相关命令

	检出（也称复制）仓库：git clone 跟github等远程仓库地址（如https://github.com/YellowKang/HelloWord.git）
		例如：git clone https://github.com/YellowKang/HelloWord.git(注意是生成在当前目录下，清先选好生成路径如：F:/某某/某某/下面)
	查看远程仓库：git remote -v
			查看到提交到的远程仓库地址地址：如
						origin  https://github.com/YellowKang/MyWork (fetch)
						origin  https://github.com/YellowKang/MyWork (push)
	添加远程仓库：git remote add [name] [url]
	删除远程仓库：git remote rm [name]
	修改远程仓库：git remote set-url --push [name] [newUrl]
	拉取（也称复制远程仓库的资源）远程仓库：git pull [物理地址如：https://github.com/YellowKang/HelloWord.git 或者设置别名] [当前使用的支线名字如master]
	
		如下：git pull https://github.com/YellowKang/HelloWord.git master
	推送远程仓库：git push [物理地址如：https://github.com/YellowKang/HelloWord.git 或者设置别名] [当前使用的支线名字如master]
	
		如下：git push https://github.com/YellowKang/HelloWord.git master
	
		注：如果推送分支到远程仓库
	
			例如：
				提交本地的kang分支，到远程的master分支，在https://github.com/YellowKang/MyWork.git上
	
				git    push 	https://github.com/YellowKang/MyWork.git  kang:master




3、版本(tag)操作相关命令
	标记版本号：git tag 版本号				--标记当前项目的版本编号
								例如：
									git tag v0.0.1
									在使用git tag 查看当前版本号

​		

	查看版本：git tag					--显示标记过的版本号，（注：未标记版本号则不显示任何事物）



	删除版本：git tag -d 版本号				--删除多余的版本号，（注：创建版本号后不会替换原来的版本号，会在原来那里加一个，可以删除也可以不删除）
								例如：
									$ git tag -d v0.0.2
									Deleted tag 'v0.0.2' (was 5a4c6b0)
									(则删除成功)



​	

	创建远程版本(本地版本push到远程)： git push origin [name]

删除远程版本：$ git push origin :refs/tags/[name]

合并远程仓库的tag到本地：$ git pull origin --tags

上传本地tag到远程仓库：$ git push origin --tags

创建带注释的tag： git tag -a [name] -m 'yourMessage'

 

4) 子模块(submodule)相关操作命令

添加子模块：$ git submodule add [url] [path]

   如：$git submodule add git://github.com/soberh/ui-libs.git src/main/webapp/ui-libs

初始化子模块：$ git submodule init  ----只在首次检出仓库时运行一次就行

更新子模块：$ git submodule update ----每次更新或切换分支后都需要运行一下

删除子模块：（分4步走哦）

 1) $ git rm --cached [path]

 2) 编辑“.gitmodules”文件，将子模块的相关配置节点删除掉

 3) 编辑“ .git/config”文件，将子模块的相关配置节点删除掉

 4) 手动删除子模块残留的目录

 

5）忽略一些文件、文件夹不提交

在仓库根目录下创建名称为“.gitignore”的文件，写入不需要的文件夹名或文件，每个元素占一行即可，如

target

bin

*.db




=====================
						--添加当前所有的操作，所有文件及文件夹的修改和操作

git rm 文件名(包括路径) 从git中删除指定文件
git clone git://github.com/schacon/grit.git 从服务器上将代码给拉下来
git config --list 看所有用户
git ls-files 看已经被提交的
git rm [file name] 删除一个文件
git commit -a 提交当前repos的所有的改变
git add [file name] 添加一个文件到git index
git commit -v 当你用－v参数的时候可以看commit的差异
git commit -m "This is the message describing the commit" 添加commit信息
git commit -a -a是代表add，把所有的change加到git index里然后再commit
git commit -a -v 一般提交命令
git log 看你commit的日志
git diff 查看尚未暂存的更新
git rm a.a 移除文件(从暂存区和工作区中删除)
git rm --cached a.a 移除文件(只从暂存区中删除)
git commit -m "remove" 移除文件(从Git中删除)
git rm -f a.a 强行移除修改后文件(从暂存区和工作区中删除)
git diff --cached 或 $ git diff --staged 查看尚未提交的更新
git stash push 将文件给push到一个临时空间中

git stash pop 将文件从临时空间pop下来

git remote add origin git@github.com:username/Hello-World.git

git push origin master 将本地项目给提交到服务器中

git pull 本地与服务器端同步

git push (远程仓库名) (分支名) 将本地分支推送到服务器上去。

git push origin serverfix:awesomebranch

git fetch 相当于是从远程获取最新版本到本地，不会自动merge
git branch branch_0.1 master 从主分支master创建branch_0.1分支
git branch -m branch_0.1 branch_1.0 将branch_0.1重命名为branch_1.0
git checkout branch_1.0/master 切换到branch_1.0/master分支
du -hs

# Git记住密码

输入一次密码即可后续不用输入

```
git config  credential.helper store     
```