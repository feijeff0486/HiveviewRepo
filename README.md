# HiveviewRepo
[![](https://jitpack.io/v/feijeff0486/HiveviewRepo.svg)](https://jitpack.io/#feijeff0486/HiveviewRepo)

项目中常用架包的临时仓库，将架包从JFrag上迁移到Jitpack。

jfrag上的aar等备份文件可在[jfrag目录](https://github.com/feijeff0486/HiveviewRepo/tree/main/jfrag)下查找。

## 依赖
首先，需要在项目根目录下的build.gradle中添加jitpack仓库：
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

然后按需添加具体的架包依赖：
```groovy
//核心库
implementation 'com.github.feijeff0486.HiveviewRepo:core:v0.0.6'
//日志库
implementation 'com.github.feijeff0486.HiveviewRepo:logger:v0.0.6'
//网络请求库
implementation 'com.github.feijeff0486.HiveviewRepo:rxrequest:v0.0.6'
//工具库
implementation 'com.github.feijeff0486.HiveviewRepo:tools:v0.0.6'
```