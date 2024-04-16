<div align="center">
    <h1>我的电视</h1>
<div align="center">

![GitHub repo size](https://img.shields.io/github/repo-size/yaoxieyoulei/mytv-android)
![GitHub Repo stars](https://img.shields.io/github/stars/yaoxieyoulei/mytv-android)
![GitHub all releases](https://img.shields.io/github/downloads/yaoxieyoulei/mytv-android/total)

</div>
    <p>使用Android原生开发的电视直播软件</p>

<img src="https://github.com/yaoxieyoulei/my_tv/blob/main/screenshots/shot_3.png?raw=true" width="96%"/>
<br/>
<img src="https://github.com/yaoxieyoulei/my_tv/blob/main/screenshots/shot_1.png?raw=true" width="96%"/>
<br/>
<img src="https://github.com/yaoxieyoulei/my_tv/blob/main/screenshots/shot_2.png?raw=true" width="96%"/>
</div>

## 使用

### 操作方式

遥控器操作方式主流电视直播软件一致；

- 频道切换：使用上下方向键，或者数字键切换频道；屏幕上下滑动；
- 频道选择：OK键；单击屏幕；
- 设置页面：菜单、帮助键、长按OK键；双击屏幕；

### 自定义直播源

1. 进入设置页面
2. 请求网址：`http://<设备IP>:10481`
3. 按界面提示操作

不支持多源，只会选择频道的第一个源，其他忽略

## 下载

可以通过右侧release进行下载或拉取代码到本地进行编译

## 说明

- 主要解决 [my_tv](https://github.com/yaoxieyoulei/my_tv)（flutter）在低端设备上播放（4k）视频卡顿掉帧
- 仅支持Android5及以上
- 网络环境必须支持IPV6
- 只在自家电视上测过，其他电视稳定性未知

## 功能

- [x] 换台反转
- [x] 数字选台
- [x] 节目单
- [x] 开机自启
- [ ] 自动更新
- [ ] 自定义直播源
- [ ] 性能优化

## 更新日志

[更新日志](./CHANGELOG.md)

## 声明

此项目（我的电视）是个人为了兴趣而开发, 仅用于学习和测试。 所用API皆从官方网站收集, 不提供任何破解内容。

## 致谢

- [my-tv](https://github.com/lizongying/my-tv)
- [参考设计稿](https://github.com/lizongying/my-tv/issues/594)
- [IPV6直播源](https://github.com/zhumeng11/IPTV)
- [live](https://github.com/fanmingming/live)
- 等等
