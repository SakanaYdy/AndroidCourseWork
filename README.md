# 猪猪视频

一个安卓视频播放器，设置多种数据来源。

## 详细介绍

该软件主要分为多个板块，登录注册页面，视频播放页面，个人中心页面等。

### 登录注册页面

1. 用户信息存储于SQLite数据库中，在进行登录验证时，从数据库中读取数据进行判断，注册同理（保证用户id跟用户名唯一）。

自己封装实现了DbcUtils类以及DbContect类用来连接以及操作数据库。

以下是封装实现的该应用的一些数据库操作。

![image.png](https://pic.leetcode.cn/1702651396-gWoGeb-image.png)

2. 实现了记住密码功能，当用户登录成功时点击保存密码，那么下次用户打开APP或者是进入到登录页面时，就会进行自动填充，不需要再次输入。

使用SharedPreferences类来将记住密码的账号密码存储在缓存中，如果下一个不选择记住密码则进行清空。初始为空值。

![image.png](https://pic.leetcode.cn/1702651430-eTvQYk-image.png)

### 视频播放页面

#### ListView  -- ListViewFragment

使用简单的继承自BaseFragment的实现类，其显示内容为数据库中所有用户的所有视频，通过查询数据库的方式去InitData，单个item实现点击播放以及暂停功能。

#### RecycleView -- ChannelFragment



#### 抖音 --  TikTokListFragment

同样继承自BaseFragment，但是这里的显示内容为爬虫所获取到的存储在云端的视频连接进行显示。

```java
public class TikTokListFragment extends BaseFragment
```

在上面类中实现View视图以及适配器

布局设置

![image.png](https://pic.leetcode.cn/1702651461-chZpSC-image.png)

每一行使用两列布局，实现两个视频同时在一行显示，扩充视图中的视频容量。

#####  视图显示

fragment_tiktok_list 设置主要格式，里面就设置一个RecycleView，然后定义了一个item_tiktok_list来设置每一块的布局，包括视频跟标题。

##### 适配器设置

![image.png](https://pic.leetcode.cn/1702651484-xbBmge-image.png)

onCreateViewHolder 方法用于创建并返回一个TikTokListViewHolder对象。这个对象表示RecyclerView中每个列表项的视图。

onBindViewHolder 方法用于将数据绑定到TikTokListViewHolder中的视图上。它从数据集合中获取特定位置的TiktokBean对象，然后将其标题设置到TextView上，将封面图加载到ImageView中。

![image.png](https://pic.leetcode.cn/1702651506-JLaypF-image.png)

TikTokListViewHolder 是一个内部类，表示RecyclerView中每个列表项的视图持有者。它包含了列表项中显示的ImageView和TextView，以及一个点击事件监听器。当列表项被点击时，它会启动TikTok2Activity，并传递相应的位置信息。

### 个人中心页面

这里主要实现一些APP的关于账户的操作，包括退出登录，修改密码，显示个人信息等操作。

该页面通过Intent进行组件间的通信，从登录页面获取到当前登录用户的信息，并显示在用户名中。

![image.png](https://pic.leetcode.cn/1702651523-VuUHwK-image.png)

修改密码功能，为了减少Fragment的设计数量，选择使用弹窗的形式进行操作。

![image.png](https://pic.leetcode.cn/1702651541-Xnkfab-image.png)

通过 `LayoutInflater` 创建一个自定义的视图（`R.layout.dialog_update_password`），该视图包含三个输入框用于输入原密码、新密码和确认新密码。在点击确定按钮时，可以通过 `view.findViewById()` 获取输入框的引用，并获取输入的内容进行相应的处理。确保在你的布局文件中包含了这三个输入框。

### 我的 页面

该页面功能较为简单，用来显示用户个人所上传的视频信息。主要操作在于将LoginActivity中的登录信息传递到该Fragment。

（上面个人中心页面也使用该方式）首先是使用Intent通信，将LoginActivity中的登录信息，包括个人账号名以及密码（用于修改密码页面作用）等信息传递到MainActivity。然后在这个里面创建加入Fragment的时候，使用setArguments方法，将从Login接收到的消息传递给Fragment组件。然后通过Adapter参数构造传递给数据构造InitData函数，然后再去数据库查询相关用户的视频信息。