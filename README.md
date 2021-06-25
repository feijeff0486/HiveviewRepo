# HiveviewRepo
项目中常用架包的临时仓库，将架包从JFrag上迁移到Jitpack

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

然后添加具体的架包依赖：
