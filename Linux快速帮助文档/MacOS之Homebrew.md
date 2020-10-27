



# 国内加速源

​		切换brew.git源

```shell
cd "$(brew --repo)"

# 替换源
# 清华源
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git
# 中科大源
git remote set-url origin https://mirrors.ustc.edu.cn/brew.git
```

​		切换homebrew-core.git源

```shell
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"

# 替换源
# 清华源
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git
# 中科大源
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-core.git
```

​			切换homebrew-cask.git源

```shell
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-cask"
# 清华源
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/homebrew-cask.git
# 中科大源
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-cask.git
```



```shell
# 清华源
echo 'export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.tuna.tsinghua.edu.cn/homebrew-bottles' >> ~/.bash_profile 
# 中科大源
echo 'export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.ustc.edu.cn/homebrew-bottles' >> ~/.bash_profile 


source ~/.bash_profile
```

