# 课程讲解文档

## 一.课程的核心思想（技术）

Kotlin + 组件化 + Kotlin Gradle DSL

## 2.Kotlin Gradle DSL

传统Gradle - Groovy 

Kotlin Gradle - Kotlin

## 3.组件化App

远古应用： App(一堆的代码)

远古应用升级版： App + Base（抽取通用代码）

远古应用Pro: App + Base + lib（lib_log,lib_network,lib_map）

MVC

MVP （MVC升级版本） m(data) v(ui) p(impl)->逻辑

组件化 App + (N + Module（App）) - N Library

如何动态构建组件化？

## 4.构建组件化App

App 

Module 笑话 地图 星座 语音设置 系统设置 天气 应用管理 开发者模式

lib lib_base lib_network lib_voice

## 5.服务保活

- 1.像素保活，也就是通过服务中启动一个窗口像素1px，来达到保活的手段[欺诈系统]
- 2.系统自带，系统做了一些友好的保活 - FLAG
    - START_STICKY:当系统内存不足的时候，杀掉了服务，那么在系统内存不再紧张的时候，启动服务
    - START_NOT_STICKY:当系统内存不足的时候，杀掉了服务，直到下一次startService才启动
    - START_REDELIVER_INTENT:重新传递Intent值
    - START_STICKY_COMPATIBILITY:START_STICKY兼容版本，但是它也不能保证系统kill掉服务一定能重启
- 3.JobSheduler
    - 工作任务，标记着这个服务一直在工作，也是作为一种进程死后复活的手段
    - 缺点：耗电，高版本不兼容
- 4.进程相互唤醒，双进程保活
    - QQ - 微信
- 5.前台服务
    - 我在前台运行，我绑定通知栏，在服务中创建通知栏
