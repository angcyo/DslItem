# DslItem
基于`DslAdapter`的`DslAdapterItem`库, 挑选能用的, 把界面拼起来.

请先了解[DslAdapter](https://github.com/angcyo/DslAdapter).

针对[DslAdapter](https://github.com/angcyo/DslAdapter)库, 编写的一些常用的`DslAdapterItem`, 欢迎PR, 欢迎提出想要的`Item`建议.

![](https://img.shields.io/badge/License-MIT-EA660C) ![](https://img.shields.io/badge/Api-16+-FA0730) ![](https://img.shields.io/badge/AndroidX-yes-0A803C)
![](https://img.shields.io/badge/Kotlin-yes-B0F9F9)

# 愿望

安卓界面的编写形式应该是`Item+Item+Item`,轻松+高度可复用:

如下形式:

```kotlin
renderAdapter{
    头部Item()
    轮播图Item()
    列表Item()
    列表Item()
    列表Item()
    ...
    尾部Item()
    加载更多Item()
}
```

借助[DslAdapter](https://github.com/angcyo/DslAdapter)库,再结合本库,把界面`+`起来.

希望广大同仁 能与我共同维护此库 欢迎PR.


![](https://raw.githubusercontent.com/angcyo/DslItem/master/png/item.png)

> 对应`Item`的属性和说明, 请依赖之后在源码内查看. 因为太丰富了, 源码看了就能懂.

# 使用`JitPack`的方式, 引入库.

## 根目录中的 `build.gradle`

```kotlin
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## APP目录中的 `build.gradle`

```kotlin
dependencies {
    implementation 'com.github.angcyo:DslItem:1.0.8'
}
```

---
**群内有`各(pian)种(ni)各(jin)样(qun)`的大佬,等你来撩.**

# 联系作者

[点此QQ对话](http://wpa.qq.com/msgrd?v=3&uin=664738095&site=qq&menu=yes)  `该死的空格`    [点此快速加群](https://shang.qq.com/wpa/qunwpa?idkey=cbcf9a42faf2fe730b51004d33ac70863617e6999fce7daf43231f3cf2997460)

[开源地址](https://github.com/angcyo/DslAdapter)

![](https://gitee.com/angcyo/res/raw/master/code/all_in1.jpg)

![](https://gitee.com/angcyo/res/raw/master/code/all_in2.jpg)
