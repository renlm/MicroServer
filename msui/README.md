# MSUI

## Git
[https://git-scm.com/download/win](https://git-scm.com/download/win)  
[https://tortoisegit.org/download](https://tortoisegit.org/download)  

```
$ git config --global --list
$ git config --global user.name "renlm"
$ git config --global user.email "renlm@21cn.com"
```

```
Git 提交
$ git init
$ git add .
$ git commit -m "Init"
$ git remote add origin https://gitee.com/renlm/msui.git
$ git push -u origin "master"
```

```
解决图标状态不显示
    win+R  
        regedit
    打开注册表后
        HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\ShellIconOverlayIdentifiers
        所有Tortoise前面加空格，排到最前边
    打开任务管理器
        Ctrl+Alt+Delete
        重启资源管理器
```

## MSYS2
[https://www.msys2.org](https://www.msys2.org)  

```
下载并安装
    开始菜单
        MSYS2 64bit
            MSYS2 MinGW Clang x64
            MSYS2 MinGW UCRT x64
            MSYS2 MinGW x64
            MSYS2 MinGW x86
            MSYS2 MSYS 
安装工具包
使用MSYS2命令行工具
    $ pacman -Syu
    $ pacman -S mingw-w64-x86_64-gcc mingw-w64-x86_64-gdb mingw-w64-x86_64-cmake mingw-w64-x86_64-make
    $ pacman -S mingw-w64-x86_64-toolchain
    $ pacman -S mingw-w64-x86_64-clang
    $ pacman -S mingw-w64-x86_64-yasm mingw-w64-x86_64-nasm
    $ pacman -S mingw-w64-x86_64-freetype
添加环境变量  
    win+R  
        control system
    环境变量 
        MSYS_DIR=C:\msys64
        MINGW64_DIR=C:\msys64\mingw64
        Path添加: %MSYS_DIR%\usr\bin;%MINGW64_DIR%\bin
查看安装版本
    $ gcc -v
    $ g++ -v
    $ cmake -version
查看CMake生成器
    $ cmake -G
```

## GO
[https://go.dev/dl](https://go.dev/dl)  
[https://dl.google.com/go/go1.23.4.windows-amd64.zip](https://dl.google.com/go/go1.23.4.windows-amd64.zip)  
	
	配置环境变量
	GOROOT：Go 语言安装根目录的路径，也就是 GO 语言的安装路径。
	GOPATH：若干工作区目录的路径，是我们自己定义的工作空间。
	GOBIN：Go 程序生成的可执行文件（executable file）的路径。
	$ go version
	$ go env
	$ go env -w GO111MODULE=on
	$ go env -w GOPROXY=https://goproxy.cn,direct
	$ go env -w GOSUMDB=goproxy.cn/sumdb/sum.golang.org

## Node.js
[https://nodejs.org](https://nodejs.org)  
[https://nodejs.cn](https://nodejs.cn)  

```
C:\nodejs
管理员身份启动PowerShell
$ Get-ExecutionPolicy -List
$ Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

安装node-gyp
https://github.renlm.cn/nodejs/node-gyp
$ npm config set registry https://registry.npmmirror.com
$ npm install -g node-gyp
```

## VS Code
[https://code.visualstudio.com](https://code.visualstudio.com)  

```
可选插件
    Git Graph
    Code Runner
    Prettier - Code formatter
    Chinese (Simplified) (简体中文) Language Pack for Visual Studio Code
```

```
设置中文
【Ctrl+Shift+P】
输入 Configure Display Language
选择 中文(简体)zh-cn
```

```
打开配置终端设置，bash命令位于Git安装目录bin中
添加自定义 [ Windows Git Bash ] 并设为默认
{
    "terminal.integrated.profiles.windows": {
        "Windows Git Bash": {
            "path": "C:\\Git\\bin\\bash.exe"
        }
    },
    "terminal.integrated.defaultProfile.windows": "Windows Git Bash"
}
```

## Webpack
[https://webpack.docschina.org](https://webpack.docschina.org)  
[https://www.npmjs.com/package/@grafana/ui](https://www.npmjs.com/package/@grafana/ui)  

```
$ npm install
$ npm start
$ npm run analyze
$ npm run build
```
