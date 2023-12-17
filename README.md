# 猪猪视频

一个安卓视频播放器

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
### 仿Youtube 视频播放
主要布局XML说明如下：

首先在fragment_channel 页面显示视频列表，该列表最外层使用SwipeRefreshLayout布局，这是Google官方推荐的下拉刷新布局控件，只需要把RecycleView或者ListView放在里面就可以实现简单的下拉刷新。

![image.png](https://pic.leetcode.cn/1702699007-vUkpMA-image.png)

#### 下拉刷新SwipeRefreshLayout

在需要刷新视频内容的ChannelFragment中，代码如下。

其中，刷新算法updateChannelId通过一个`List<String> remainingChannels`，每次从中选择随机项（视频id），然后从列表中移除。当移除为空的时候，使用` Collections.shuffle(remainingChannels);`进行打乱，从而进行下一轮的随机刷新选择。

```java
public class ChannelFragment extends Fragment
{
    private SwipeRefreshLayout swipeRefreshLayout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
          swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        // 下拉刷新时的监听事件
         swipeRefreshLayout.setOnRefreshListener(this::refreshContent);
    }          
```

![image.png](https://pic.leetcode.cn/1702804023-QYiEgu-image.png)

在values/channel_id.xml下，提供了几个Youtube频道爬取id：

![image.png](https://pic.leetcode.cn/1702804086-RgGODG-image.png)

#### Googleapis爬取视频

由于是爬取的Youtube视频，需要到Google的开发者控制台https://console.cloud.google.com/ 下申请启用[YouTube Data API v3](https://console.cloud.google.com/apis/api/youtube.googleapis.com/overview?project=youtubeapi-407214) 服务。在”凭据“处获取API KEY，此密钥用于访问爬取服务googleapis。

![image.png](https://pic.leetcode.cn/1702804332-ZfdJft-image.png)

![image.png](https://pic.leetcode.cn/1702804473-iioVZS-image.png)

在其中，maxResults表示最大请求数量，比如如果是访问频道视频，最多会爬取20个视频信息。googleapis获取的频道链接内容是Json格式，需要做Parse解析工作，得到需要的视频信息列表。

#### OkHTTP 请求Url

上面准备工作完成后，需要对URL进行请求，并处理响应(response)，通过响应的内容（Json字符串）进行解析，得到数据。在使用OkHTTP的时候，在ChannelFragment中，进行引用：

```java
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
```

`RequestYoutubeAPI`类是ChannelFragment中的内部类，用于GoogleapiURL的请求。它是一个继承自AsyncTask的类。由于主线程用于处理 UI 事件，如果在主线程中执行一些耗时的操作（例如网络请求、文件读写等），可能会导致界面卡顿或 ANR（Application Not Responding）错误。

为了解决这个问题，可以使用 AsyncTask 将耗时的任务放在后台线程中执行，而在主线程中执行一些与 UI 相关的操作。 在早期版本的 Android 中，AsyncTask 主要用于在后台线程执行异步任务，并在主线程中更新 UI。它 不是传统意义上的多线程或协程。它是一种轻量级的异步任务框架， 其内部实现依赖于线程池和消息队列，以简化在 Android 应用中进行异步操作的代码编写。

```java
private class RequestYoutubeAPI extends AsyncTask<Void, String, String>
    // AsyncTask 用于后台线程执行异步任务的类，它基于线程池和消息处理机制。
```

在使用的时候，它的三个类型：`AsyncTask<Params, Progress, Result>` 描述如下：

| 属性       | 描述                                   |
| ---------- | -------------------------------------- |
| `Params`   | 执行任务前，传入的参数的类型           |
| `Progress` | 后台线程执行的时候，用来表示进度的类型 |
| `Result`   | 表示执行结果的类型                     |

要使用 AsyncTask ，必须新建一个类来继承它，并且重写 `doInBackground` 方法。通常也会重写 `onPostExecute` 方法。 执行异步任务的时候，我们主要关心下面这4个方法。

| 方法                                      | 描述                                                         |
| ----------------------------------------- | ------------------------------------------------------------ |
| `onPreExecute()`                          | **执行任务前**在ui线程调用。通用用来设置任务，比如在界面上显示一个进度条。在本项目中没有使用到。 |
| `Result doInBackground(Params... params)` | 在 `onPreExecute()` 结束后立即调用这个方法。**耗时的异步任务就在这里操作。**执行任务时传入的参数会被传到这里。本项目中，用于OkHTTP的网络请求。 |
| `onProgressUpdate(Progress... values)`    | 在 ui 线程中执行。后台任务还在进行的时候，这里负责处理进度信息。比如在这显示进度条动画，修改文字显示等。在本项目中没有使用到。 |
| `onPostExecute(Result result)`            | **后台任务结束了调这个方法。它在 ui 线程执行**。最后的结果会传到这。本项目中，用于在这个方法中更新 UI，处理执行结果。 |



